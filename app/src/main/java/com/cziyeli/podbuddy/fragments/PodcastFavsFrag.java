package com.cziyeli.podbuddy.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.activeandroid.content.ContentProvider;
import com.bdenney.itunessearch.PodcastEpisode;
import com.cziyeli.podbuddy.Config;
import com.cziyeli.podbuddy.R;
import com.cziyeli.podbuddy.adapters.PodcastFavsAdapter;
import com.cziyeli.podbuddy.models.PodcastFav;
import com.cziyeli.podbuddy.services.ListenLatestService;
import com.google.gson.Gson;

import java.io.IOException;

/**
 * Created by connieli on 6/3/15.
 */
public class PodcastFavsFrag extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_ID = 0;
    private PodcastFavsAdapter mAdapter;
    private ListView mListView;

    public ListenLatestReceiver mListenReceiver;
    public boolean mEpisodeIsPlaying = false;
    public MediaPlayer mPodcastPlayer;

    Button stopButton;

    public static PodcastFavsFrag newInstance() {
        PodcastFavsFrag f = new PodcastFavsFrag();
        return f;
    }

    // STEP 2
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new PodcastFavsAdapter(getActivity(), null);
    }

    // STEP 3
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_podcast_favs, container, false);
        mListView = (ListView) v.findViewById(R.id.podcast_favs_list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mListenListener);

        // TEST stop
        stopButton = (Button) v.findViewById(R.id.stopPlayerBtn);
        

        return v;
    }

    public ListView.OnItemClickListener mListenListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Cursor cursor = (Cursor) mAdapter.getItem(position);
//            long podcast_id = cursor.getLong(cursor.getColumnIndexOrThrow("podcast_id"));
//            startListenService(podcast_id);
            Log.d(Config.DEBUG_TAG, "++ clicked position in list, not starting listen service! ");

            /** go to PodcastFav detail view **/
        }
    };

    // STEP 4
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        // Register Listen Latest Receiver
        mListenReceiver = new ListenLatestReceiver();
        IntentFilter intentFilter = new IntentFilter(ListenLatestService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(mListenReceiver, intentFilter);
        Log.e(Config.DEBUG_TAG, "onactivitycreated");

        // Initialize a Loader with id '0'. If the Loader with this id already
        // exists, then the LoaderManager will reuse the existing Loader.
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    public void onPause(){
        super.onPause();
        Log.e(Config.DEBUG_TAG, "onPause");
        if (mListenReceiver != null) {
            getActivity().unregisterReceiver(mListenReceiver);
        }
    }


    /** LOADER CALLBACKS **/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri contentProviderUri = ContentProvider.createUri(PodcastFav.class, null);
        Loader<Cursor> loader = new CursorLoader(getActivity(),
                contentProviderUri, null, null, null, null);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(Config.DEBUG_TAG, "+++ onLoadFinished() called! +++");
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(Config.DEBUG_TAG, "+++ onLoaderReset() called! +++");
        mAdapter.swapCursor(null);
    }

    /** LISTEN LOGIC **/

    public class ListenLatestReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String jsonMyObject;
            Bundle extras = intent.getExtras();

            jsonMyObject = extras.getString(Config.LISTEN_OUT);
            PodcastEpisode episode = new Gson().fromJson(jsonMyObject, PodcastEpisode.class);
//            Uri uri = Uri.parse(episode.getMediaUrl());
            Log.d(Config.DEBUG_TAG, "listenlatestreceiver on receive! " + episode.getMediaUrl());
            // need to check create or pause here

            if (extras != null) {
                // If mediaPlayer already playing, stop on click
                if (mEpisodeIsPlaying) {
                    Log.e(Config.DEBUG_TAG, "mEpisodeIsPlaying");
                    mPodcastPlayer.reset();
                    mEpisodeIsPlaying = false;
                    return;
                }

                Log.e(Config.DEBUG_TAG, "episode is not playing!");

                // create or prepare if already
                if (mPodcastPlayer == null) {
                    mPodcastPlayer = new MediaPlayer();
                }
//                    mPodcastPlayer = MediaPlayer.create(context, uri);

                try {
                    mPodcastPlayer.reset();
                    mPodcastPlayer.setDataSource(episode.getMediaUrl());
                    mPodcastPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mPodcastPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        if (mp == mPodcastPlayer) {
                            mPodcastPlayer.start();
                            mEpisodeIsPlaying = true;
                        }
                    }
                });

                mPodcastPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.stop();
                        mp.release();
                        mEpisodeIsPlaying = false;
                    }
                });

            }

        }
    }

}
