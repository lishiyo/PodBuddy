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

    // Return existing PodcastFav as new Podcast or create
    public static Podcast findOrCreateFromInfo(long p_id) {
//        PodcastFav existingFav = new Select()
//                                .from(PodcastFav.class)
//                                .where("podcast_id = ?", podcastInfo.getPodcastId())
//                                .executeSingle();


        Podcast podcast = new Podcast();

        if (Podcast.hasBeenFavorited(p_id)) {
//            podcast.producer_name = existingFav.producer_name;
//            podcast.podcast_name = existingFav.podcast_name;
//            podcast.artwork_url = existingFav.artwork_url;
//            podcast.feed_url = existingFav.feed_url;
//            podcast.podcast_id = existingFav.podcast_id;
            podcast.favorited = 1;
        } else {
//            podcast.producer_name = podcastInfo.getProducerName();
//            podcast.podcast_name = podcastInfo.getPodcastName();
//            podcast.artwork_url = podcastInfo.getArtworkUrl();
//            podcast.feed_url = podcastInfo.getFeedUrl();
//            podcast.podcast_id = podcastInfo.getPodcastId();
            podcast.favorited = 0;
        }

        return podcast;
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

    // Returns whether podcast_id exists in podcast_favs
    public static boolean hasBeenFavorited(long p_id) {
        ArrayList<Long> currentFavs = PodcastFav.currentIds();
        return currentFavs.contains(p_id);
    }
}
