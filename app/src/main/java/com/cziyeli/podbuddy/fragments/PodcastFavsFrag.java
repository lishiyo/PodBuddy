package com.cziyeli.podbuddy.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cziyeli.podbuddy.R;

/**
 * Created by connieli on 6/3/15.
 */
public class PodcastFavsFrag extends Fragment {
    public static final int LOADER_ID = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.frag_podcast_favs, container, false);
    }


}
