package com.cziyeli.podbuddy.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
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
import android.widget.ListView;

import com.activeandroid.content.ContentProvider;
import com.bdenney.itunessearch.PodcastEpisode;
import com.cziyeli.podbuddy.Config;
import com.cziyeli.podbuddy.R;
import com.cziyeli.podbuddy.adapters.PodcastFavsAdapter;
import com.cziyeli.podbuddy.models.PodcastFav;
import com.cziyeli.podbuddy.services.ListenLatestService;
import com.cziyeli.podbuddy.services.MediaPlayerService;
import com.google.gson.Gson;

/**
 * Created by connieli on 6/3/15.
 */
public class PodcastFavsFrag extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_ID = 0;
    private PodcastFavsAdapter mAdapter;
    private ListView mListView;

    public ListenLatestReceiver mListenReceiver;


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

        return v;
    }

    // STEP 4
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        // Initialize a Loader with id '0'. If the Loader with this id already
        // exists, then the LoaderManager will reuse the existing Loader.
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register Listen Latest Receiver
        mListenReceiver = new ListenLatestReceiver();
        IntentFilter intentFilter = new IntentFilter(ListenLatestService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(mListenReceiver, intentFilter);
        Log.e(Config.DEBUG_TAG, " +++ onResume +++");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.e(Config.DEBUG_TAG, " +++ onPause +++");
        if (mListenReceiver != null) {
            getActivity().unregisterReceiver(mListenReceiver);
        }
    }


    /** Click Fav Podcast row => detail view for ratings **/
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

        // TODO: start IntentService to get PodcastFavs.allLatestEpisodeUrls()
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
            Activity act = getActivity();

            Bundle extras = intent.getExtras();
            String jsonMyObject = extras.getString(Config.LISTEN_OUT);
            PodcastEpisode episode = new Gson().fromJson(jsonMyObject, PodcastEpisode.class);

            Intent playIntent = new Intent( act, MediaPlayerService.class );
//            playIntent.setAction(MediaPlayerService.ACTION_PLAY);
            playIntent.setAction(MediaPlayerService.ACTION_START_NEW);

            // TODO: Instead of passing url, pass String[] mMediaUrls and position
            playIntent.putExtra(Config.MEDIA_URL, episode.getMediaUrl());


            act.startService(playIntent);
        }



        /** OLD **/
//        public void createPodcastPlayer(Context context, Intent intent) {
//            String jsonMyObject;
//            Bundle extras = intent.getExtras();
//
//            jsonMyObject = extras.getString(Config.LISTEN_OUT);
//            PodcastEpisode episode = new Gson().fromJson(jsonMyObject, PodcastEpisode.class);
//            Log.d(Config.DEBUG_TAG, "listenlatestreceiver on receive! " + episode.getMediaUrl());
//            // need to check create or pause here
//
//            if (extras != null) {
//                // If mediaPlayer already playing, stop on click
//                if (mEpisodeIsPlaying) {
//                    Log.e(Config.DEBUG_TAG, "mEpisodeIsPlaying");
//                    mPodcastPlayer.reset();
//                    mEpisodeIsPlaying = false;
//                    return;
//                }
//
//                Log.e(Config.DEBUG_TAG, "episode is not playing!");
//
//                // create or prepare if already
//                if (mPodcastPlayer == null) {
//                    mPodcastPlayer = new MediaPlayer();
//                }
////                    mPodcastPlayer = MediaPlayer.create(context, uri);
//
//                try {
//                    mPodcastPlayer.reset();
//                    mPodcastPlayer.setDataSource(episode.getMediaUrl());
//                    mPodcastPlayer.prepareAsync();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                mPodcastPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        if (mp == mPodcastPlayer) {
//                            mPodcastPlayer.start();
//                            mEpisodeIsPlaying = true;
//                        }
//                    }
//                });
//
//                mPodcastPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        mp.stop();
//                        mp.release();
//                        mEpisodeIsPlaying = false;
//                    }
//                });
//
//            }

//        }
    }

}
