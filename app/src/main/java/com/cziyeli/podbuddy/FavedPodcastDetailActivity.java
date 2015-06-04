package com.cziyeli.podbuddy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.cziyeli.podbuddy.adapters.SmartFragmentStatePagerAdapter;
import com.cziyeli.podbuddy.fragments.FirstFragment;
import com.cziyeli.podbuddy.fragments.SecondFragment;

/**
 * Page through all Faved Podcast detail fragments
 */
public class FavedPodcastDetailActivity extends SearchableActivity {

    private SmartFragmentStatePagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);
        ViewPager vpPager = (ViewPager) findViewById(R.id.pager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        // Attach the page change listener inside the activity
        vpPager.setOnPageChangeListener(mViewPagerListener);
    }

    public ViewPager.OnPageChangeListener mViewPagerListener = new ViewPager.OnPageChangeListener() {

        // This method will be invoked when a new page becomes selected.
        @Override
        public void onPageSelected(int position) {
            Toast.makeText(FavedPodcastDetailActivity.this,
                    "Selected page position: " + position, Toast.LENGTH_SHORT).show();
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

    public static class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return FirstFragment.newInstance(0, "Page # 1");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return FirstFragment.newInstance(1, "Page # 2");
                case 2: // Fragment # 1 - This will show SecondFragment
                    return SecondFragment.newInstance(2, "Page # 3");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

}
