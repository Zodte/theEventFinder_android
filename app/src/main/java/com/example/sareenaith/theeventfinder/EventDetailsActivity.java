package com.example.sareenaith.theeventfinder;

import android.content.Context;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);


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

        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, URL+"getFullEventInfo/"+eventId, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO Response
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


                                Log.d("myApp","EventName: "+eventName);
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

        final Context thisContext = this;
        attendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               /* try {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL+"attendEvent", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Volley", response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", error.toString());
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("eventName", eventNameTxt.getText().toString().trim());
                            params.put("lati", String.valueOf(lat));
                            params.put("long", String.valueOf(lgt));
                            params.put("genderRestrict", check);
                            params.put("descr", eventDescriptionTxt.getText().toString().trim());
                            params.put("ageMin", minAge.getText().toString().trim());
                            params.put("ageMax", maxAge.getText().toString().trim());
                            params.put("startDate", dateViewFrom.getText().toString().trim().concat(" ").concat(timeViewFrom.getText().toString().trim()));
                            params.put("endDate", dateViewTo.getText().toString().trim().concat(" ").concat(timeViewTo.getText().toString().trim()));
                            params.put("isAndroid", "true");
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(thisContext);
                    requestQueue.add(stringRequest);

                } catch(Exception e) {
                    e.printStackTrace();
                } */
            }
        });
    }

}
