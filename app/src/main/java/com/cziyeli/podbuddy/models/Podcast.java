package com.cziyeli.podbuddy.models;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Podcast search results stored here => sent to CursorLoader
 *
 */

// Override id to use ActiveAndroid data as content provider
@Table(name = "podcasts", id = BaseColumns._ID)
public class Podcast extends Model {

    @Column(name = "podcast_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE, index = true)
    public long podcast_id;

    @Column(name = "producer_name")
    public String producer_name;

    @Column(name = "podcast_name")
    public String podcast_name;

    @Column(name = "artwork_url")
    public String artwork_url;

    @Column(name = "feed_url")
    public String feed_url;

    @Column(name="favorited")
    public int favorited;

    // Make sure to have a default constructor for every ActiveAndroid model
    public Podcast() {
        super();
    }

    public static List<Podcast> getAll() {
        return new Select().from(Podcast.class).execute();
    }

    public static Podcast findPodcastByPodcastId(long p_id) {
        return new Select().from(Podcast.class).where("podcast_id = ?", p_id).executeSingle();
    }

    public static int count(){
        return Podcast.getAll().size();
    }

    public static void clearAll() {
        new Delete().from(Podcast.class).execute();
    }

    /** METHODS **/

    // Create the Fav if new, or delete from Favs to unfavorite
    public void createOrDestroyFav() {
        PodcastFav fav = PodcastFav.findFavByPodcastId(this.podcast_id);

        if (fav == null) { // not favorited yet, create
            fav = new PodcastFav();
            fav.producer_name = this.producer_name;
            fav.podcast_name = this.podcast_name;
            fav.artwork_url = this.artwork_url;
            fav.feed_url = this.feed_url;
            fav.podcast_id = this.podcast_id;
            fav.save();
        } else { // already favorited - unfavorite
            fav.delete();
        }

        this.toggleFav();
    }

    public void toggleFav() {
        this.favorited = (this.favorited == 0) ? 1 : 0;
        this.save();
    }

    // Returns whether podcast_id exists in podcast_favs
    public static boolean hasBeenFavorited(long p_id) {
        ArrayList<Long> currentFavs = PodcastFav.currentIds();
        return currentFavs.contains(p_id);
    }
}
