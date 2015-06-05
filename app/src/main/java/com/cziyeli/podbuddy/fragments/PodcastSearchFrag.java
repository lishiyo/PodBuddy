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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.content.ContentProvider;
import com.cziyeli.podbuddy.Config;
import com.cziyeli.podbuddy.R;
import com.cziyeli.podbuddy.adapters.PodcastSearchAdapter;
import com.cziyeli.podbuddy.models.Podcast;
import com.cziyeli.podbuddy.services.SearchPodcastsService;

/**
 * Created by connieli on 6/3/15.
 * Attached FavBtn listeners in Adapter
 */
public class PodcastSearchFrag extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // id is specific to the fragment's loader
    public static final int LOADER_ID = 1;
    public String mQuery = "";
    private TextView mSearchTitleView;
    private ListView mListView;
    private SearchPodcastsReceiver mPodcastsReceiver;
    private PodcastSearchAdapter mAdapter;

    public static PodcastSearchFrag newInstance(String query) {
        PodcastSearchFrag f = new PodcastSearchFrag();
        Bundle args = new Bundle();
        args.putString(Config.SEARCH_IN, query);
        f.setArguments(args);

        return f;
    }

    // STEP 2
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mQuery = bundle.getString(Config.SEARCH_IN);
        mAdapter = new PodcastSearchAdapter(getActivity(), null);
    }

    // STEP 3
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_podcast_search, container, false);
        mSearchTitleView = (TextView) v.findViewById(R.id.search_title);
        mSearchTitleView.setText(mQuery);
        mListView = (ListView) v.findViewById(R.id.podcast_search_list);
        mListView.setAdapter(mAdapter);
//        mListView.setOnItemClickListener(mFavListener);

        return v;
    }

    // STEP 4
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        startSearchService();

        // Initialize a Loader with id '1'. If the Loader with this id already
        // exists, then the LoaderManager will reuse the existing Loader.
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(mPodcastsReceiver);
    }

//    public ListView.OnItemClickListener mFavListener = new ListView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Cursor cursor = (Cursor) mAdapter.getItem(position);
//            long _id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
//            Podcast podcast = Podcast.load(Podcast.class, _id);
//            PodcastFav.createOrDestroyFav(podcast);
//
//            Log.d(Config.DEBUG_TAG, "++ Favlistener clicked position: " + String.valueOf(_id));
//        }
//    };


    /** LOADER CALLBACKS **/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(Config.DEBUG_TAG, "+++ onCreateLoader() called! +++");

        /**
         * Don't need custom CursorLoader unless hitting database rather than through AA.
         * If using built-in CursorLoader, use a Content Provider (query, getType, CRUD).
         * CursorLoader queries and fills cursor in a background thread
         */

        Uri contentProviderUri = ContentProvider.createUri(Podcast.class, null);
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


    /** SEARCH LOGIC **/

    public void startSearchService() {
        mPodcastsReceiver = new SearchPodcastsReceiver();
        IntentFilter intentFilter = new IntentFilter(SearchPodcastsService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        Activity act = getActivity();
        act.registerReceiver(mPodcastsReceiver, intentFilter);

        Intent intent = new Intent(act, SearchPodcastsService.class);
        intent.putExtra(Config.SEARCH_IN, mQuery);
        act.startService(intent);
    }

    public class SearchPodcastsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = Podcast.count();

            if (!intent.getBooleanExtra(Config.SEARCH_OUT, false)) {
                Log.d(Config.DEBUG_TAG, "failed to get data! count: " + String.valueOf(count));
                Toast.makeText(getActivity(), "no podcasts were found", Toast.LENGTH_LONG).show();
            } else {
                Log.d(Config.DEBUG_TAG, "got data! count: " + String.valueOf(count));
            }

        }
    }
}
