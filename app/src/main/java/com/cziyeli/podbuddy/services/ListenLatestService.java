package com.cziyeli.podbuddy.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.bdenney.itunessearch.ITunesSearchClient;
import com.bdenney.itunessearch.PodcastEpisode;
import com.bdenney.itunessearch.PodcastInfo;
import com.cziyeli.podbuddy.Config;
import com.google.gson.Gson;

/**
 * Created by connieli on 6/5/15.
 */
public class ListenLatestService extends IntentService {

    public static final String ACTION_RESPONSE = Config.PACKAGE_BASE + ".LISTEN";

    public ListenLatestService() {
        super("ListenLatestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long podcast_id = intent.getLongExtra(Config.LISTEN_IN, 0);
        PodcastEpisode episode = null;

        // Query API
        try {
            PodcastInfo podcastInfo = ITunesSearchClient.getPodcastInfo(podcast_id);
            episode = ITunesSearchClient.getLatestEpisode(podcastInfo);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Store to database?

        broadcastResults(episode);
    }

    protected void broadcastResults(PodcastEpisode episode) {
        Log.d(Config.DEBUG_TAG, "ListenLatestService broadcastResults: " + episode.getTitle());

        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_RESPONSE);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);

        intentResponse.putExtra(Config.LISTEN_OUT, new Gson().toJson(episode));
        sendBroadcast(intentResponse);
    }
}
