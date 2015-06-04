package com.cziyeli.podbuddy.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bdenney.itunessearch.PodcastInfo;
import com.cziyeli.podbuddy.Config;
import com.cziyeli.podbuddy.R;
import com.cziyeli.podbuddy.adapters.PodcastSearchAdapter;
import com.cziyeli.podbuddy.models.Podcast;
import com.cziyeli.podbuddy.services.SearchPodcastsService;

import java.util.ArrayList;

/**
 * Created by connieli on 6/3/15.
 */
public class PodcastSearchFrag extends Fragment implements LoaderManager.LoaderCallbacks<Object> {
    // id is specific to the fragment's LoaderManager
    public static final int LOADER_ID = 1;
    public String mQuery = "";

    private TextView mSearchTitleView;
    private Activity mActivity;
    public SearchPodcastsReceiver mPodcastsReceiver;
    public ArrayList<PodcastInfo> mPodcastInfos = null;

    // Custom CursorAdapter to bind podcast results to podcast_search_list
    public PodcastSearchAdapter mAdapter;

    public static PodcastSearchFrag newInstance(String query) {
        PodcastSearchFrag f = new PodcastSearchFrag();
        Bundle args = new Bundle();
        args.putString(Config.QUERY_TAG, query);
        f.setArguments(args);

        return f;
    }

    // STEP 2
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mQuery = bundle.getString(Config.QUERY_TAG);
    }

    // STEP 3
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_podcast_search, container, false);
        mSearchTitleView = (TextView) v.findViewById(R.id.search_title);

        return v;
    }

    // STEP 4
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        mActivity = getActivity();

        // TEST
        mSearchTitleView.setText("QUERY: " + mQuery);

        startSearchService();

        // Initialize a Loader with id '1'. If the Loader with this id already
        // exists, then the LoaderManager will reuse the existing Loader.
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    /** SEARCH LOGIC **/

    protected void startSearchService() {
        mPodcastsReceiver = new SearchPodcastsReceiver();
        IntentFilter intentFilter = new IntentFilter(SearchPodcastsService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        mActivity.registerReceiver(mPodcastsReceiver, intentFilter);

        Intent intent = new Intent(mActivity, SearchPodcastsService.class);
        intent.putExtra(Config.QUERY_TAG, mQuery);
        mActivity.startService(intent);
    }

    /** LOADER CALLBACKS **/

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        Log.d(Config.DEBUG_TAG, "+++ onCreateLoader() called! +++");
//        return new AppListLoader(getActivity());
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        Log.d(Config.DEBUG_TAG, "+++ onLoadFinished() called! +++");

    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
        Log.d(Config.DEBUG_TAG, "+++ onLoaderReset() called! +++");

    }

    public class SearchPodcastsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = Podcast.count();
            Log.d(Config.DEBUG_TAG, "onReceive in frag search items: " + String.valueOf(count));
//            mPodcastInfos = (ArrayList<PodcastInfo>) intent.getSerializableExtra("data");
//            if (mPodcastInfos != null && mPodcastInfos.size() > 0) {
//                Log.d(Config.DEBUG_TAG, "found mPodcastInfos");
////                mAdapter.updateData(mPodcastInfos);
//            } else {
//                Toast.makeText(mActivity, "no podcasts were found", Toast.LENGTH_LONG).show();
//            }
        }
    }
}
