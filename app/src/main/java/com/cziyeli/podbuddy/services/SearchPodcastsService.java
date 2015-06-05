package com.cziyeli.podbuddy.services;


import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.bdenney.itunessearch.ITunesSearchClient;
import com.bdenney.itunessearch.PodcastInfo;
import com.cziyeli.podbuddy.Config;
import com.cziyeli.podbuddy.models.Podcast;
import com.cziyeli.podbuddy.models.PodcastFav;

import java.util.ArrayList;
import java.util.List;

/**
 * Spawns worker thread, one request at a time - automatically calls stopSelf() when work queue is empty.
 */

public class SearchPodcastsService extends IntentService {
    public static final String ACTION_RESPONSE = Config.PACKAGE_BASE + ".SEARCH";

    public SearchPodcastsService() {
        super("SearchPodcastsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Gets data from the incoming Intent
        String dataString = intent.getStringExtra(Config.SEARCH_IN);

        // Query API
        List<PodcastInfo> podcastInfoList = ITunesSearchClient.searchPodcasts(dataString);

        // Fresh search
        Podcast.clearAll();

        // Cache new search results to database
        savePodcasts(podcastInfoList);
    }

    protected void savePodcasts(List<PodcastInfo> podcastInfoList) {
        PodcastInfo podcastInfo;
        ArrayList<Long> currentFavs = PodcastFav.currentIds();

        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < podcastInfoList.size(); i++) {
                podcastInfo = podcastInfoList.get(i);

                Podcast podcast = new Podcast();
                podcast.podcast_id = podcastInfo.getPodcastId();
                podcast.favorited = currentFavs.contains(podcast.podcast_id) ? 1 : 0;
                podcast.producer_name = podcastInfo.getProducerName();
                podcast.podcast_name = podcastInfo.getPodcastName();
                podcast.artwork_url = podcastInfo.getArtworkUrl();
                podcast.feed_url = podcastInfo.getFeedUrl();
                podcast.podcast_id = podcastInfo.getPodcastId();

                Log.d(Config.DEBUG_TAG, "savePodcasts: " + podcastInfo.toString() + " favorited: " + String.valueOf(podcast.favorited));

                podcast.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } catch(SQLiteException e) {
            e.printStackTrace();
            broadcastResults(false);
        } finally {
            ActiveAndroid.endTransaction();
            broadcastResults(true);
        }
    }

    protected void broadcastResults(boolean success) {
        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_RESPONSE);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra(Config.SEARCH_OUT, success);
        sendBroadcast(intentResponse);
    }
}
