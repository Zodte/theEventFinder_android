package com.example.sareenaith.theeventfinder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Map;

public class MyProfileEventDetailsActivity extends AppCompatActivity {

    private int eventId;
    private TextView twEventName, twEventDescription, twEventStart, twEventEnd, twEventAttendees;
    Bundle extras;

    RequestQueue requestQueue;
    private Config config = new Config();
    private final String URL = config.getUrl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_event_details);

        twEventName =  (TextView) findViewById(R.id.hostedEventName);
        twEventDescription =  (TextView) findViewById(R.id.hostedEventDescription);
        twEventStart =  (TextView) findViewById(R.id.hostedEventStartTime);
        twEventEnd =  (TextView) findViewById(R.id.hostedEventEndTime);
        twEventAttendees =  (TextView) findViewById(R.id.hostedEventAttendees);

        Intent intent = getIntent();
        extras = intent.getExtras();

        if(extras != null) {
            eventId = (Integer)extras.get("eventId");
        }

        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, URL+"getFullEventInfo/"+eventId, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("myApp", response.length()+"Response received baaaby!:"+ response);
                        JSONObject event = null;
                        try {
                            event = response;
                            String eventName = event.getString("name");
                            String eventDescr = event.getString("description");
                            int ageMin = event.getInt("age_min");
                            int ageMax = event.getInt("age_max");
                            String endDate = event.getString("end_date").substring(0,10);
                            String startDate = event.getString("start_date").substring(0,10);
                            Boolean genRestrict = event.getBoolean("gender_restriction");
                            JSONArray attendees = event.getJSONArray("attendees");

                            twEventName.setText(eventName);
                            twEventDescription.setText(eventDescr);
                            twEventStart.setText(startDate);
                            twEventEnd.setText(endDate);
                            if (attendees.length() > 0) {
                                twEventAttendees.setText(attendees.getJSONObject(0).getString("name"));
                                for(int i = 1; i < attendees.length(); i++) {
                                    JSONObject attendee = attendees.getJSONObject(i);
                                    twEventAttendees.append(", "+attendee.getString("name")+"\n");
                                }
                            }

                           /* if(genRestrict) {
                                eventGenRestrictTw.setText("This is a gender restricted event");
                            } else {
                                eventGenRestrictTw.setText("This is not a gender restricted event");
                            }*/

                           // eventAttendeesTw.setText("Attendees:\n");


                            Log.d("myApp","EventName: "+eventName);
                        } catch (JSONException e) {
                            Log.d("myApp", "MyProfileEventDetailsActivity -> onResponse error: "+e);
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

    }
}
