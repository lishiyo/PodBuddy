package com.cziyeli.podbuddy.models;


import android.provider.BaseColumns;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.bdenney.itunessearch.ITunesSearchClient;
import com.bdenney.itunessearch.PodcastEpisode;
import com.bdenney.itunessearch.PodcastInfo;
import com.cziyeli.podbuddy.Config;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Podcasts that have been favorited stored here
 *
 */

// Override id to use ActiveAndroid data as content provider
@Table(name = "podcast_favs", id = BaseColumns._ID)
public class PodcastFav extends Model {

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

    @Column(name="rated")
    public int rated;

    @Column(name = "rating")
    public int rating;

    @Column(name = "time_last_listen", index = true)
    public Date time_last_listen; // stored as a timestamp

    @Column(name = "num_listens")
    public int num_listens;

    public PodcastFav() {
        super();
        this.favorited = 1;
    }

    /** QUERIES **/

    public static List<PodcastFav> getAll() {
        return new Select().from(PodcastFav.class).execute();
    }

    public static void createOrDestroyFav(Podcast podcast) {
        Log.e(Config.DEBUG_TAG, "podcast id: " + String.valueOf(podcast.podcast_id));
        PodcastFav fav = PodcastFav.findFavByPodcastId(podcast.podcast_id);

        if (fav == null) { // not favorited yet, create
            fav = new PodcastFav();
            fav.producer_name = podcast.producer_name;
            fav.podcast_name = podcast.podcast_name;
            fav.artwork_url = podcast.artwork_url;
            fav.feed_url = podcast.feed_url;
            fav.podcast_id = podcast.podcast_id;
            fav.save();

            podcast.favorited = 1;
            podcast.save();
            Log.d(Config.DEBUG_TAG, "creating fav! name: " + fav.podcast_name + " and id: " + fav.podcast_id);

        } else { // already favorited - unfavorite
            fav.delete();

            podcast.favorited = 0;
            podcast.save();
            Log.d(Config.DEBUG_TAG, "deleting fav! name: " + podcast.podcast_name + " and id: " + podcast.podcast_id);

        }
    }

    public static PodcastFav findFavByPodcastId(long p_id) {
        return new Select().from(PodcastFav.class).where("podcast_id = ?", p_id).executeSingle();
    }

    public static int count(){
        return PodcastFav.getAll().size();
    }

    public static void clearAll() {
        new Delete().from(PodcastFav.class).execute();
    }

    // Returns all current podcast_ids in PodcastFav table
    public static ArrayList<Long> currentIds() {
        List<PodcastFav> podcasts = PodcastFav.getAll();
        int length = podcasts.size();
        ArrayList<Long> ids = new ArrayList<Long>(length);
        for (PodcastFav podcast : podcasts) {
            ids.add(podcast.podcast_id);
        }

        return ids;
    }

    public static String[] allLatestEpisodeUrls() {
        List<PodcastFav> favs = getAll();
        String[] allUrls = new String[favs.size()];
        long podcast_id;
        PodcastInfo podcastInfo;
        PodcastEpisode episode;

        for (int i = 0; i < allUrls.length; i++) {
            podcast_id = favs.get(i).podcast_id;
            podcastInfo = ITunesSearchClient.getPodcastInfo(podcast_id);
            episode = ITunesSearchClient.getLatestEpisode(podcastInfo);
            allUrls[i] = episode.getMediaUrl();
        }

        return allUrls;
    }


    /** UTILS **/

    // parse strings into date object for time_last_listen
    public void setDateFromString(String date) throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
        sf.setLenient(true);
        this.time_last_listen = (Date) sf.parse(date);
    }

}
