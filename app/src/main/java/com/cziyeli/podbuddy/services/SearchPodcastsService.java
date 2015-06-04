package com.cziyeli.podbuddy.services;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.bdenney.itunessearch.ITunesSearchClient;
import com.bdenney.itunessearch.PodcastInfo;
import com.cziyeli.podbuddy.Config;
import com.cziyeli.podbuddy.models.Podcast;

import java.util.ArrayList;
import java.util.List;

/**
 * Spawns worker thread, one request at a time - automatically calls stopSelf() when work queue is empty.
 */

public class SearchPodcastsService extends IntentService {
    public static final String ACTION_RESPONSE = Config.PACKAGE_BASE + ".RESPONSE";

    public SearchPodcastsService() {
        super("SearchPodcastsService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getStringExtra(Config.QUERY_TAG);
        // Do work
        List<PodcastInfo> podcastInfoList = ITunesSearchClient.searchPodcasts(dataString);
        Log.d(Config.DEBUG_TAG, "workIntent onHandleIntent: " + dataString + " returned size: " + String.valueOf(podcastInfoList.size()));

        savePodcasts(podcastInfoList);
//        ArrayList<PodcastInfo> podcastArray = processPodcastInfo(podcastInfoList);
//        broadcastResults(podcastArray);
    }
//
//    private ArrayList<PodcastInfo> processPodcastInfo(List<PodcastInfo> podcastInfoList) {
//        ArrayList<PodcastInfo> test = new ArrayList<PodcastInfo>(Arrays.<PodcastInfo>asList(new List<PodcastInfo>[]{podcastInfoList}));
//    }

    // Process List<PodcastInfo> => Podcast models => save to database
    protected void savePodcasts(List<PodcastInfo> podcastInfoList) {
        PodcastInfo podcastInfo;

        for (int i = 0; i < podcastInfoList.size(); i++) {
            // podcast.toString() => Science Friday Audio Podcast - Science Friday

            podcastInfo = podcastInfoList.get(i);
            Log.d(Config.DEBUG_TAG, "savePodcasts: " + podcastInfo.getPodcastName() + " by: " + podcastInfo.getProducerName());

            Podcast podcast = new Podcast();
            podcast.producer_name = podcastInfo.getPodcastName();
            podcast.podcast_name = podcastInfo.getPodcastName();
            podcast.artwork_url = podcastInfo.getArtworkUrl();
            podcast.feed_url = podcastInfo.getFeedUrl();
            podcast.podcast_id = podcastInfo.getPodcastId();

            podcast.save();
        }
    }

    protected void broadcastResults(ArrayList<PodcastInfo> podcastArray) {
        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_RESPONSE);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
//        intentResponse.putExtra(Config.PODCAST_DATA, podcastArray);
        sendBroadcast(intentResponse);
    }
}
