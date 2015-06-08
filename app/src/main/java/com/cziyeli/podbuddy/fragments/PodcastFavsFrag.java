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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.content.ContentProvider;
import com.bdenney.itunessearch.PodcastEpisode;
import com.cziyeli.podbuddy.Config;
import com.cziyeli.podbuddy.PodcastFavDetailActivity;
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
        return new PodcastFavsFrag();
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
    @Override
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
    }

    @Override
    public void onPause(){
        super.onPause();
        if (mListenReceiver != null) {
            getActivity().unregisterReceiver(mListenReceiver);
        }
    }

    /** Click Fav Podcast row => detail view for ratings **/
    public ListView.OnItemClickListener mListenListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Cursor cursor = (Cursor) mAdapter.getItem(mStartPos);
//            long podcast_id = cursor.getLong(cursor.getColumnIndexOrThrow("podcast_id"));
//            startListenService(podcast_id);
            /** send mStartPos to PodcastFav detail view **/
            Activity act = getActivity();
            Intent detailIntent = new Intent(act, PodcastFavDetailActivity.class );
            detailIntent.putExtra(Config.DETAIL_IN, position);
            act.startActivity(detailIntent);
        }
    };

    /** LOADER CALLBACKS **/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri contentProviderUri = ContentProvider.createUri(PodcastFav.class, null);
        Loader<Cursor> loader = new CursorLoader(getActivity(),
                contentProviderUri, null, null, null, "time_last_listen DESC");

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        // TODO: start IntentService to get PodcastFavs.allLatestEpisodeUrls()
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
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
            playIntent.setAction(MediaPlayerService.ACTION_START_NEW);

            // TODO: Instead of passing url, pass String[] mMediaUrls and mStartPos
            playIntent.putExtra(Config.MEDIA_URL, episode.getMediaUrl());

            act.startService(playIntent);
        }

    }

}
