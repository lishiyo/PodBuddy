package com.cziyeli.podbuddy.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.cziyeli.podbuddy.R;

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
            holder.mCollectionName = (TextView) view.findViewById(R.id.collectionName);
            holder.mProducerName = (TextView) view.findViewById(R.id.producerName);
            view.setTag(holder);
        }

        // Extract properties from cursor

        // Populate fields with extracted properties
    }

    public static class ViewHolder {
        TextView mCollectionName;
        TextView mProducerName;
    }
}
