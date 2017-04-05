package com.example.sareenaith.theeventfinder;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

        // Get active element
        TextView activeTextView =
                (TextView) rowView.findViewById(R.id.event_list_active);

        Event event = (Event) getItem(position);

        titleTextView.setText(event.getName());
        subtitleTextView.setText(event.getDescription());
        detailTextView.setText(event.getStartDate().toString().substring(0, 10));
        if(!event.isActive()) {
            activeTextView.setText("Inactive Event");
            rowView.setBackgroundColor(Color.RED);
        }
        else if(isEventExpired( event )) {
            activeTextView.setText("Expired Event");
            rowView.setBackgroundColor(Color.YELLOW);
        }
        else {
            activeTextView.setText("Upcoming Event");
        }


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
            case "Music":
                thumbnailImageView.setImageResource(R.mipmap.guitar);
                break;
        }

        return rowView;
    }

    // Returns true if event is already started, else returns false.
    private Boolean isEventExpired( Event event ) {
        //Find the current date
        Date today = new Date();
        //Get the event start date
        Date eventStartDate = convertToDateObject( event.getStartDate().toString().substring(0, 16).replace('T', ' ') );

        return (today.compareTo(eventStartDate) > 0 && event.isActive());

    }

    private Date convertToDateObject( String date ) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
}
