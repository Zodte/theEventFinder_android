package com.example.sareenaith.theeventfinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventDetailsActivity extends AppCompatActivity {

    private String eventId;
    private TextView eventNameTw, eventDescrTw, eventTimeTw, eventAgesTw, eventGenRestrictTw, eventAttendeesTw;
    private Button attendBtn, unAttendBtn;
    RequestQueue requestQueue;
    private Config config = new Config();
    private final String URL = config.getUrl();
    private int numAttendees = 0;
    boolean isCreator = false;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        eventNameTw =  (TextView) findViewById(R.id.eventDetails_name);
        eventDescrTw =  (TextView) findViewById(R.id.eventDetails_description);
        eventTimeTw =  (TextView) findViewById(R.id.eventDetails_time);
        //eventAgesTw =  (TextView) findViewById(R.id.eventDetails_age);
        eventGenRestrictTw =  (TextView) findViewById(R.id.eventDetails_genderRestrict);
        eventAttendeesTw =  (TextView) findViewById(R.id.eventDetails_attendees);
        attendBtn = (Button) findViewById(R.id.eventDetails_attendBtn);
        //unAttendBtn = (Button) findViewById(R.id.eventDetails_unAttendBtn);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        // Get the id of the event that we are supposed to show in detail.
        if(extras != null) {
            eventId = (String)extras.get("eventId");
        }

        final String dbid = sharedpreferences.getString("db_id", null);
        Map<String, String> params = new HashMap();
        params.put("eventId", eventId);
        params.put("userId", dbid);
        JSONObject parameters = new JSONObject(params);

        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, URL+"getFullEventInfo", parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            JSONObject event = null;
                            try {
                                event = response;
                                String eventName = event.getString("name");
                                String eventDescr = event.getString("description");
                                //int ageMin = event.getInt("age_min");
                                //int ageMax = event.getInt("age_max");
                                String endDate = event.getString("end_date").substring(0, 16).replace('T', ' ');
                                String startDate = event.getString("start_date").substring(0, 16).replace('T', ' ');
                                Boolean genRestrict = event.getBoolean("gender_restriction");
                                JSONArray attendees = event.getJSONArray("attendees");
                                Boolean isAttending = event.getBoolean("isAttending");
                                Boolean isActive = event.getBoolean("isactive");


                                eventNameTw.setText("Event name: " + eventName);
                                eventDescrTw.setText("Description: " + eventDescr);
                                eventTimeTw.setText("This event happens on " + startDate + " and ends " + endDate);
                                //eventAgesTw.setText("This event is for ages " + ageMin + " to " + ageMax);
                                if(genRestrict) {
                                    eventGenRestrictTw.setText("This is a gender restricted event");
                                } else {
                                    eventGenRestrictTw.setText("This is not a gender restricted event");
                                }

                                // Currently we fetch all attendees and calculate the number of attendees by the
                                // length of the attendee array.  This is a dirty fix that will get changed later,
                                // because there is no need to fetch all the individual attendees any more.
                                numAttendees = attendees.length();
                                eventAttendeesTw.setText(attendees.length() + " joined this event");
                                /*
                                for(int i = 0; i < attendees.length(); i++) {
                                    JSONObject attendee = attendees.getJSONObject(i);
                                    eventAttendeesTw.append(attendee.getString("name")+"\n");
                                }
                                */
                                Boolean isExpired = isEventExpired( startDate );

                                if(isAttending && isActive) {
                                    attendBtn.setText("Unattend Event");
                                }



                                if(Integer.parseInt(dbid) == event.getInt("creator_id") && isActive && !isExpired) {
                                    attendBtn.setText("Delete Event");
                                }
                                if(isExpired && isActive ) {
                                    attendBtn.setText("Expired Event");
                                    attendBtn.setEnabled(false);
                                }

                                if(!isActive) {
                                    attendBtn.setText("Inactive Event");
                                    attendBtn.setEnabled(false);
                                }
                            } catch (JSONException e) {
                                Log.d("myApp", "buhuu"+e);
                            }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("myApp", ""+error);
                    }
                });
        requestQueue.add(jsObjRequest);

        // Click listener for the "Attend" button.
        attendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Dirty dirty code

                if(attendBtn.getText().subSequence(0,1).equals("A")){
                    attendEvent();
                }
                else if(attendBtn.getText().subSequence(0,1).equals("D")) {
                    deleteEvent();
                }
                else if(attendBtn.getText().subSequence(0,1).equals("U")){
                    unAttendEvent();
                }
            }
        });

    }

    private Boolean isEventExpired( String startDate ) {
        //Find the current date
        Date today = new Date();
        //Get the event start date
        Date eventStartDate = convertToDateObject( startDate );

        return (today.compareTo(eventStartDate) > 0);
    }

    private Date convertToDateObject(String date ) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private void deleteEvent() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL+"deactivateEvent", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    // Add the post parameters to the http request.
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("eventId", eventId);
                    params.put("isAndroid", "true");
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        } catch(Exception e) {
            e.printStackTrace();
        }
        // Hide the attending button if it is clicked.
        //attendBtn.setText("Event Deleted");
        //attendBtn.setEnabled(false);

        Toast.makeText(getApplicationContext(), "Event deleted!",
                Toast.LENGTH_SHORT)
                .show();
        Intent intent = new Intent(EventDetailsActivity.this, EventsMapActivity.class);
        startActivity(intent);

        //unAttendBtn.setEnabled(true);


    }

    public void attendEvent() {
        // Fetch the userId
        final String dbid = sharedpreferences.getString("db_id", null);
        // Tell the server the user is going to attend the event.
        eventAttendeesTw.setText("Number of attendees: "+ ++numAttendees);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL+"attendEvent", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    // Add the post parameters to the http request.
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("eventId", eventId);
                    params.put("userId", dbid);
                    params.put("isAndroid", "true");
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        } catch(Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "You are now attending this event!",
                Toast.LENGTH_SHORT)
                .show();
        // Hide the attending button if it is clicked.
        attendBtn.setText("Unattend Event");
        //unAttendBtn.setEnabled(true);

    }

    public void unAttendEvent() {
        final String dbid = sharedpreferences.getString("db_id", null);
        eventAttendeesTw.setText("Number of attendees: "+ --numAttendees);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL+"unAttendEvent", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    // Add the post parameters to the http request.
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("eventId", eventId);
                    params.put("userId", dbid);
                    params.put("isAndroid", "true");
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch(Exception e) {
            e.printStackTrace();
        }

        //unAttendBtn.setEnabled(false);
        attendBtn.setText("Attend event");

    }
}