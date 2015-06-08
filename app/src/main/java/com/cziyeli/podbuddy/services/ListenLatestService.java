package com.cziyeli.podbuddy.services;

import android.app.IntentService;
import android.content.Intent;

import com.bdenney.itunessearch.ITunesSearchClient;
import com.bdenney.itunessearch.PodcastEpisode;
import com.bdenney.itunessearch.PodcastInfo;
import com.cziyeli.podbuddy.Config;
import com.cziyeli.podbuddy.models.PodcastFav;
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

        // Query API and update podcastFav info
        try {
            PodcastInfo podcastInfo = ITunesSearchClient.getPodcastInfo(podcast_id);
            episode = ITunesSearchClient.getLatestEpisode(podcastInfo);
            updatePodcast(podcast_id);
        } catch(Exception e) {
            e.printStackTrace();
        }

        broadcastResults(episode);
    }

    private void updatePodcast(long podcast_id) {
        PodcastFav fav = PodcastFav.findFavByPodcastId(podcast_id);
        fav.updateLastListen();
    }

    protected void broadcastResults(PodcastEpisode episode) {
        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_RESPONSE);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra(Config.LISTEN_OUT, new Gson().toJson(episode));
        sendBroadcast(intentResponse);
    }
}
