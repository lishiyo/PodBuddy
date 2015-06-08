package com.cziyeli.podbuddy.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.Model;
import com.cziyeli.podbuddy.Config;
import com.cziyeli.podbuddy.R;
import com.cziyeli.podbuddy.models.PodcastFav;
import com.cziyeli.podbuddy.services.ListenLatestService;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by connieli on 6/7/15.
 */
public class DetailFavFragment extends Fragment {
    public static final String POSITION = "POSITION";
    public static final String PODCAST_FAV = "PODCAST_FAV";
    public Activity mActivity;

//    public int mStartPos;
    public PodcastFav mPodcast;
    public TextView mTitleView;
    public TextView mProducerView;
    public ImageView mArtwork;
    public Button mListenBtn;

    public static DetailFavFragment newInstance(int position, long podcast_id) {
        DetailFavFragment frag = new DetailFavFragment();
        Bundle args = new Bundle();
//        args.putInt(POSITION, position);
        args.putLong(PODCAST_FAV, podcast_id);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_fav_detail, container, false);
        mTitleView = (TextView) view.findViewById(R.id.detail_title);
        mProducerView = (TextView) view.findViewById(R.id.detail_producer);
        mArtwork = (ImageView) view.findViewById(R.id.detail_artwork);
        mListenBtn = (Button) view.findViewById(R.id.detail_listen_btn);
        mListenBtn.setOnClickListener(mListenListener);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Need to store activity for AsyncTask or getActivity() returns null
        mActivity = activity;
        setPodcastData();
    }


    public void setPodcastData(){
        Bundle args = getArguments();
        AsyncFetchPodcast fetchPodcast = new AsyncFetchPodcast();
        fetchPodcast.execute(args.getLong(PODCAST_FAV));
    }

    /** ASYNC TASKS - Database queries **/

    public class AsyncFetchPodcast extends AsyncTask<Long, List<Model>, PodcastFav> {

        @Override
        protected PodcastFav doInBackground(Long... params) {
            return PodcastFav.findFavByPodcastId(params[0]);
        }

        @Override
        protected void onPostExecute(PodcastFav fav) {
            mPodcast = fav;
            mTitleView.setText(mPodcast.podcast_name);
            mProducerView.setText(mPodcast.producer_name);
            Picasso.with(mActivity)
                    .load(mPodcast.artwork_url)
                    .placeholder(Config.PLACEHOLDER_IMG)
                    .into(mArtwork);
        }
    }

    public View.OnClickListener mListenListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startListenService(mPodcast.podcast_id, v);
        }
    };

    public void startListenService(Long podcast_id, View v) {
        Context ctx = v.getContext();
        Intent intent = new Intent(ctx, ListenLatestService.class);
        intent.putExtra(Config.LISTEN_IN, podcast_id);
        ctx.startService(intent);
    }

}
