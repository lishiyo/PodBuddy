package com.cziyeli.podbuddy.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.cziyeli.podbuddy.Config;

/**
   Extension of FragmentStatePagerAdapter which intelligently caches
   all active fragments and manages the fragment lifecycles.
*/

public abstract class SmartFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    // Sparse array to keep track of registered fragments in memory
    public SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public SmartFragmentStatePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        Log.d(Config.DEBUG_TAG, "SmartFragment adapter INSTANTIATE item: " + String.valueOf(position) + " with registeredFragments length: " + String.valueOf(registeredFragments.size()));
        return fragment;
    }

    // Unregister when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    // Returns the fragment for the mStartPos (if instantiated)
    public Fragment getRegisteredFragment(int position) {
        Log.d(Config.DEBUG_TAG, "++++ getRegisteredFragment: " + String.valueOf(position));

        return registeredFragments.get(position);
    }

    public void loadFragmentData() {}
}