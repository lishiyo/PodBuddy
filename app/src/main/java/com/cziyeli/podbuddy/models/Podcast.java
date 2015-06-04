package com.cziyeli.podbuddy.models;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Podcast search results stored here => sent to CursorLoader
 *
 */

// Override id to use ActiveAndroid data as content provider
@Table(name = "podcasts", id = BaseColumns._ID)
public class Podcast extends Model {

    @Column(name = "producer_name")
    public String producer_name;

    @Column(name = "podcast_name")
    public String podcast_name;

    @Column(name = "artwork_url")
    public String artwork_url;

    @Column(name = "feed_url")
    public String feed_url;

    @Column(name = "podcast_id")
    public long podcast_id;

    // Make sure to have a default constructor for every ActiveAndroid model
    public Podcast() {
        super();
    }

    public static List<Podcast> getAll() {
        return new Select().from(Podcast.class).execute();
    }

    public static int count(){
        return Podcast.getAll().size();
    }

    public static void clearAll() {
        new Delete().from(Podcast.class).execute();
    }

}
