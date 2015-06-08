package com.cziyeli.podbuddy.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cziyeli.podbuddy.Config;
import com.cziyeli.podbuddy.R;
import com.cziyeli.podbuddy.services.ListenLatestService;

/**
 * Created by connieli on 6/4/15.
 */
public class PodcastFavsAdapter extends CursorAdapter {

    public PodcastFavsAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.podcast_fav_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.mPodcastName = (TextView) view.findViewById(R.id.podcast_name);
            holder.mProducerName = (TextView) view.findViewById(R.id.producer_name);
            holder.mListenBtn = (Button) view.findViewById(R.id.listen_btn);
            holder.mListenBtn.setOnClickListener(mListenListener);
            view.setTag(holder);
        }

        // Extract properties from cursor
        String podcastName = cursor.getString(cursor.getColumnIndexOrThrow("podcast_name"));
        String producerName = cursor.getString(cursor.getColumnIndexOrThrow("producer_name"));
        long podcastId = cursor.getLong(cursor.getColumnIndexOrThrow("podcast_id")); // AA id

        // Populate fields with extracted properties
        holder.mPodcastName.setText(podcastName);
        holder.mProducerName.setText(producerName);
        holder.mListenBtn.setTag(podcastId);
    }


    /** CLICK EVENTS **/

    public View.OnClickListener mListenListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Long podcast_id = (long) v.getTag();
            startListenService(podcast_id, v);
        }
    };

    public void startListenService(Long podcast_id, View v) {
        Context ctx = v.getContext();
        Intent intent = new Intent(ctx, ListenLatestService.class);
        intent.putExtra(Config.LISTEN_IN, podcast_id);
        ctx.startService(intent);
    }

    public static class ViewHolder {
        TextView mPodcastName;
        TextView mProducerName;
        Button mListenBtn;
    }
}
