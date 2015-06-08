package com.cziyeli.podbuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.activeandroid.Model;
import com.bdenney.itunessearch.PodcastEpisode;
import com.cziyeli.podbuddy.adapters.SmartFragmentStatePagerAdapter;
import com.cziyeli.podbuddy.fragments.DetailFavFragment;
import com.cziyeli.podbuddy.models.PodcastFav;
import com.cziyeli.podbuddy.services.ListenLatestService;
import com.cziyeli.podbuddy.services.MediaPlayerService;
import com.google.gson.Gson;

import java.util.List;

/**
 * Page through all Faved Podcast detail fragments
 */
public class PodcastFavDetailActivity extends SearchableActivity {
    public static final int PODCAST_TYPE = 1;
    private SmartFragmentStatePagerAdapter mPagerAdapter;
    public ViewPager mViewPager;
    public List<PodcastFav> mPodcastFavs;
    public int mStartPos;
    public ListenLatestReceiver mListenReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_favs);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        AsyncFetchAllPodcasts fetcher = new AsyncFetchAllPodcasts();
        fetcher.execute();
    }

    // only if activity launchMode="singleTop" or "singleTask"
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    protected void handleIntent(Intent intent) {
        mStartPos = intent.getIntExtra(Config.DETAIL_IN, 0);
        mViewPager.setCurrentItem(mStartPos);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register Listen Latest Receiver
        mListenReceiver = new ListenLatestReceiver();
        IntentFilter intentFilter = new IntentFilter(ListenLatestService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        this.registerReceiver(mListenReceiver, intentFilter);
    }

    @Override
    public void onPause(){
        super.onPause();
        if (mListenReceiver != null) {
            this.unregisterReceiver(mListenReceiver);
        }
    }

    /** ASYNC TASKS - Database queries **/

    public class AsyncFetchAllPodcasts extends AsyncTask<String, List<Model>, List<PodcastFav>> {

        @Override
        protected List<PodcastFav> doInBackground(String... params) {
            return PodcastFav.getAllOrdered();
        }

        @Override
        protected void onPostExecute(List<PodcastFav> podcasts) {
            mPodcastFavs = podcasts;

            // Setup adapter after getting proper length
            mPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mPagerAdapter);

            // Attach the page change listener inside the activity
            mViewPager.setOnPageChangeListener(mViewPagerListener);

            // set ViewPager to starting position
            handleIntent(getIntent());
        }
    }

    /** ViewPager listeners **/

    public ViewPager.OnPageChangeListener mViewPagerListener = new ViewPager.OnPageChangeListener() {

        // This method will be invoked when a new page becomes selected BEFORE instantiating it
        @Override
        public void onPageSelected(int position) {
        }

        // This method will be invoked when the current page is scrolled
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Code goes here
        }

        // Called when the scroll state changes:
        // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
        @Override
        public void onPageScrollStateChanged(int state) {
            // Code goes here
        }
    };

    /** ViewPager adapter - instantiates one frag left and right **/

    public class DetailPagerAdapter extends SmartFragmentStatePagerAdapter {

        public DetailPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return PodcastFavDetailActivity.this.mPodcastFavs.size();
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            // Instantiate DetailFrag with PodcastFav model
            PodcastFav fav = mPodcastFavs.get(position);
            Log.d(Config.DEBUG_TAG, "+++ mPagerAdapter getItem at: " + String.valueOf(position));
            return DetailFavFragment.newInstance(fav.podcast_id);
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }

    /** LISTEN LOGIC **/

    public class ListenLatestReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String jsonMyObject = extras.getString(Config.LISTEN_OUT);
            PodcastEpisode episode = new Gson().fromJson(jsonMyObject, PodcastEpisode.class);

            Intent playIntent = new Intent( PodcastFavDetailActivity.this, MediaPlayerService.class );
            playIntent.setAction(MediaPlayerService.ACTION_START_NEW);

            // TODO: Instead of passing url, pass String[] mMediaUrls and mStartPos
            playIntent.putExtra(Config.MEDIA_URL, episode.getMediaUrl());

            PodcastFavDetailActivity.this.startService(playIntent);
        }

    }

}
