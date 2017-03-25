package com.example.sareenaith.theeventfinder;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

// NOT USING THIS ONE ATM. USING MyProfileEventDetailsActivity
public class HostedEventsMoreDetails extends AppCompatActivity {
    TextView twEventName, twEventDescription, twEventStart, twEventEnd;
    String eventName, eventDescription, eventStartTime, eventEndTime;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosted_events_more_details);

        //Skilgreinir textview breyturnar.
        twEventName = (TextView) findViewById(R.id.hostedEventName);
        twEventDescription = (TextView) findViewById(R.id.hostedEventDescription);
        twEventStart = (TextView) findViewById(R.id.hostedEventStartTime);
        twEventEnd = (TextView) findViewById(R.id.hostedEventEndTime);
       // twEventAttendees = (TextView) findViewById(R.id.hostedEventAttendees);

        extras = getIntent().getExtras();
        final ArrayList<Event> events = (ArrayList<Event>) extras.getSerializable("events");
        int i = extras.getInt("eventIndex");

        eventName = events.get(i).getName();
        eventDescription = events.get(i).getDescription();
        eventStartTime = events.get(i).getStartDate().toString();
        eventEndTime = events.get(i).getEndDate().toString();
       // eventAttendees =

        twEventName.setText(eventName);
        twEventDescription.setText(eventDescription);
        twEventStart.setText(eventStartTime);
        twEventEnd.setText(eventEndTime);

/*        twEventAttendees.setText("Attendees:\n");
        for(int i = 0; i < attendees.length(); i++) {
            JSONObject attendee = attendees.getJSONObject(i);
            eventAttendeesTw.append(attendee.getString("name")+"\n");
        }*/

    }

}
