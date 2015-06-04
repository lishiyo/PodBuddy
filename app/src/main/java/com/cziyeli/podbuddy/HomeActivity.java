package com.cziyeli.podbuddy;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cziyeli.podbuddy.adapters.PodcastSearchAdapter;
import com.cziyeli.podbuddy.fragments.PodcastFavsFrag;
import com.cziyeli.podbuddy.fragments.PodcastSearchFrag;

/**
 * Home view - show frag Favs, SearchResults
 */
public class HomeActivity extends SearchableActivity {
    // custom CursorAdapter sitting between Podcast table and ListView
    private PodcastSearchAdapter mAdapter;
    private TextView mHomeTitle;
    public RelativeLayout mFragContainer;
    public int mFragContainerId = R.id.podcast_list_container;

    // Fragment manager
    public FragmentManager mFM;
    public PodcastFavsFrag mPodcastFavsFrag;
    public PodcastSearchFrag mPodcastSearchFrag;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mFM = getSupportFragmentManager();
        mHomeTitle = (TextView) findViewById(R.id.home_title);
        mFragContainer = (RelativeLayout) findViewById(R.id.podcast_list_container);

        if (savedInstanceState == null) {
            setupFragFavs();
        }
    }

    protected void setupFragFavs() {
        // frag handles data loading from content provider
        if (mFragContainer != null) {
            mPodcastFavsFrag = new PodcastFavsFrag();
            mFM.beginTransaction().add(mFragContainerId, mPodcastFavsFrag).commit();
        }
    }

    protected void setupFragSearch() {
        if (mFragContainer != null) {
            mPodcastSearchFrag = PodcastSearchFrag.newInstance(mCurrQuery);
            mFM.beginTransaction().replace(mFragContainerId, mPodcastSearchFrag).addToBackStack(null).commit();
        }
    }

    /** SEARCH LOGIC **/

    // only if activity launchMode="singleTop"
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    protected void handleIntent(Intent intent) {
        this.mCurrQuery = intent.getStringExtra(SearchManager.QUERY);

        // switch PodcastFavsFrag to PodcastSearchFrag
        setupFragSearch();
    }

}
