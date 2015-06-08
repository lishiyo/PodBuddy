package com.cziyeli.podbuddy.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cziyeli.podbuddy.R;

/**
 * Created by connieli on 6/2/15.
 */
public class TEST_ImageFragment extends Fragment {
    int fragVal;

    public static TEST_ImageFragment init(int val) {
        TEST_ImageFragment truitonFrag = new TEST_ImageFragment();
        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putInt("val", val);
        truitonFrag.setArguments(args);
        return truitonFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_image, container,
                false);
        View tv = layoutView.findViewById(R.id.text);
        ((TextView) tv).setText("Truiton Fragment #" + fragVal);
        return layoutView;
    }
}