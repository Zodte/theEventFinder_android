package com.example.sareenaith.theeventfinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class EventDetailsActivity extends AppCompatActivity {

    private String eventId;
    private TextView eventNameTw, eventDescrTw, eventTimeTw, eventAgesTw, eventGenRestrictTw, eventAttendeesTw;
    private Button attendBtn;
    RequestQueue requestQueue;
    private Config config = new Config();
    private final String URL = config.getUrl();
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        eventNameTw =  (TextView) findViewById(R.id.eventDetails_name);
        eventDescrTw =  (TextView) findViewById(R.id.eventDetails_description);
        eventTimeTw =  (TextView) findViewById(R.id.eventDetails_time);
        eventAgesTw =  (TextView) findViewById(R.id.eventDetails_age);
        eventGenRestrictTw =  (TextView) findViewById(R.id.eventDetails_genderRestrict);
        eventAttendeesTw =  (TextView) findViewById(R.id.eventDetails_attendees);
        attendBtn = (Button) findViewById(R.id.eventDetails_attendBtn);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

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
                                int ageMin = event.getInt("age_min");
                                int ageMax = event.getInt("age_max");
                                String endDate = event.getString("end_date").substring(0,10);
                                String startDate = event.getString("start_date").substring(0,10);
                                Boolean genRestrict = event.getBoolean("gender_restriction");
                                JSONArray attendees = event.getJSONArray("attendees");
                                Boolean isAttending = event.getBoolean("isAttending");

                                eventNameTw.setText("Event name: " + eventName);
                                eventDescrTw.setText("Description: " + eventDescr);
                                eventTimeTw.setText("This event is starts " + startDate + " and ends " + endDate);
                                eventAgesTw.setText("This event is for ages " + ageMin + " to " + ageMax);
                                if(genRestrict) {
                                    eventGenRestrictTw.setText("This is a gender restricted event");
                                } else {
                                    eventGenRestrictTw.setText("This is not a gender restricted event");
                                }

                                eventAttendeesTw.setText("Attendees:\n");
                                for(int i = 0; i < attendees.length(); i++) {
                                    JSONObject attendee = attendees.getJSONObject(i);
                                    eventAttendeesTw.append(attendee.getString("name")+"\n");
                                }

                                if(isAttending) {
                                    attendBtn.setVisibility(View.GONE);
                                }

                                Log.d("myApp","EventName: "+isAttending);
                            } catch (JSONException e) {
                                Log.d("myApp", "buhuu");
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

        attendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attendEvent();
            }
        });
    }

    public void attendEvent() {
        final String dbid = sharedpreferences.getString("db_id", null);
        Toast.makeText(getApplicationContext(), "id er"+dbid,
                Toast.LENGTH_SHORT)
                .show();
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL+"attendEvent", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Volley", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() {
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



    }




}
