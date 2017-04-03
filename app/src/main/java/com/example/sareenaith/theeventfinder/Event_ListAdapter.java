package com.example.sareenaith.theeventfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This adapter class allows us to display the basic details of events in the list view under 'My
 * Profile' (both hosted and attended).
 */

class Event_ListAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private ArrayList<Event> mDataSource;

    Event_ListAdapter(Context context, ArrayList<Event> items) {
        mDataSource = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.list_item_event, parent, false);

        // Get title element
        TextView titleTextView =
                (TextView) rowView.findViewById(R.id.event_list_title);

        // Get subtitle element
        TextView subtitleTextView =
                (TextView) rowView.findViewById(R.id.event_list_subtitle);

        // Get detail element
        TextView detailTextView =
                (TextView) rowView.findViewById(R.id.event_list_detail);

        // Get thumbnail element
        ImageView thumbnailImageView =
                (ImageView) rowView.findViewById(R.id.event_list_thumbnail);

        Event event = (Event) getItem(position);

        titleTextView.setText(event.getName());
        subtitleTextView.setText(event.getDescription());
        detailTextView.setText(event.getStartDate().toString().substring(0, 10));

        switch(event.getCategory()){
            case "Sports":
                thumbnailImageView.setImageResource(R.mipmap.football);
                break;
            case "Outdoors":
                thumbnailImageView.setImageResource(R.mipmap.outdoors);
                break;
            case "Party":
                thumbnailImageView.setImageResource(R.mipmap.beerbottle);
                break;
        }
        // If we want to add a thumbnail with an event photo or something, we could use this:
        //Picasso.with(mContext).load(event.imageUrl).placeholder(R.mipmap.ic_launcher).into(thumbnailImageView);

        return rowView;
    }
}
