package com.cziyeli.podbuddy.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.cziyeli.podbuddy.R;
import com.cziyeli.podbuddy.models.Podcast;

/**
 * Add ViewHolder caching
 * CursorAdapter sits between Cursor (data source from SQLite query) and a ListView
 * 1) which layout to inflate for an item
 * 2) which fields of the cursor to bind to views in the template
 */

public class PodcastSearchAdapter extends CursorAdapter {

    public PodcastSearchAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.podcast_search_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.mPodcastName = (TextView) view.findViewById(R.id.podcast_name);
            holder.mProducerName = (TextView) view.findViewById(R.id.producer_name);
            holder.mFavBtn = (Button) view.findViewById(R.id.favBtn);
            holder.mFavBtn.setOnClickListener(mToggleFavListener);
            view.setTag(holder);
        }

        // Extract properties from cursor
        String podcastName = cursor.getString(cursor.getColumnIndexOrThrow("podcast_name"));
        String producerName = cursor.getString(cursor.getColumnIndexOrThrow("producer_name"));
        boolean isFavorited = cursor.getInt(cursor.getColumnIndexOrThrow("favorited")) == 1 ? true : false;
        long p_id = cursor.getLong(cursor.getColumnIndexOrThrow("podcast_id")); // AA id

        // Populate fields with extracted properties
        holder.mPodcastName.setText(podcastName);
        holder.mProducerName.setText(producerName);
        holder.mFavBtn.setTag(p_id);

        // Switch button styles to toggle fav
        int btntext = isFavorited ? R.string.act_unfav : R.string.act_fav;
        int btnColor = isFavorited ? Color.LTGRAY : view.getResources().getColor(R.color.genius);
        int btnTextColor = isFavorited ? Color.WHITE : Color.BLACK;
        holder.mFavBtn.setText(btntext);
        holder.mFavBtn.setBackgroundColor(btnColor);
        holder.mFavBtn.setTextColor(btnTextColor);
    }

    public View.OnClickListener mToggleFavListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            long p_id = (long) v.getTag();
            Podcast podcast = Podcast.findPodcastByPodcastId(p_id);
            if (podcast != null) {
                podcast.createOrDestroyFav();
            }
        }
    };

    public static class ViewHolder {
        TextView mPodcastName;
        TextView mProducerName;
        Button mFavBtn;
    }
}
