package com.cziyeli.podbuddy.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cziyeli.podbuddy.Config;
import com.cziyeli.podbuddy.R;

/**
 * Created by connieli on 6/2/15.
 */
public class SecondFragment extends ListFragment {
    // Store instance variables
    private String title;
    private int page;
    int fragNum;
    String arr[] = { "This is", "a Truiton", "Demo", "App", "For", "Showing",
            "FragmentPagerAdapter", "and ViewPager", "Implementation" };

    // newInstance constructor for creating fragment with arguments
    public static FirstFragment newInstance(int page, String title) {
        FirstFragment fragmentFirst = new FirstFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }


    /**
     * The Fragment's UI is a simple text view showing its instance number and
     * an associated list.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_pager_list,
                container, false);
        View tv = layoutView.findViewById(R.id.text);
        ((TextView) tv).setText("Truiton Fragment #" + fragNum);
        return layoutView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, arr));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(Config.DEBUG_TAG, "Item clicked: " + id);
    }
}