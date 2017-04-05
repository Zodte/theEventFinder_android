package com.example.sareenaith.theeventfinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Hoai Nam Duc Tran on 30/01/2017.
 * Basic stuff. Two buttons which either get my hosted events or my attended events.
 */

public class MyProfileActivity extends AppCompatActivity {
    private Config config = new Config();
    private final String URL = config.getUrl();
    SharedPreferences sharedpreferences;
    RequestQueue requestQueue;

    private String path;

    Button hostedButton;
    Button attendedButton;

    private ArrayList<Event> events = new ArrayList<Event>();
    private ArrayList<Event> emptyEvents = new ArrayList<Event>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        addListenerOnHostedEventsButton();
        requestQueue = Volley.newRequestQueue(this);
        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        path = "getAttendedEvents/";
        getEvents();
    }

    public void addListenerOnHostedEventsButton() {
        hostedButton = (Button) findViewById(R.id.myprofile_hosted_button);
        hostedButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        path = "getHostedEvents/";
                        getEvents();
                    }
                }
        );

        attendedButton = (Button) findViewById(R.id.myprofile_attended_button);
        attendedButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        path = "getAttendedEvents/";
                        getEvents();
                    }
                }
        );
    }

    public void getEvents() {
        final String dbid = sharedpreferences.getString("db_id", null);
        events.clear(); // clearing so the array will be repopulated each time we call this method

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, URL+path+dbid, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        for(int i = 0; i < response.length(); i++){
                            JSONObject event = null;
                            try {
                                event = response.getJSONObject(i);
                                int eventID = event.getInt("id");
                                String name = event.getString("name");
                                String description = event.getString("description");
                                int ageMin = event.getInt("age_min");
                                int ageMax = event.getInt("age_max");
                                int creatorID = event.getInt("creator_id");
                                boolean genderRestriction = event.getBoolean("gender_restriction");
                                String startDateString = event.getString("start_date").replace("T"," ").substring(0,23);
                                Timestamp startDate = Timestamp.valueOf(startDateString);
                                String endDateString = event.getString("end_date").replace("T", " ").substring(0,23);
                                Timestamp endDate = Timestamp.valueOf(endDateString);
                                float lat = (float)event.getDouble("lat");
                                float lgt = (float)event.getDouble("lgt");
                                String category = event.getString("category");
                                String creatorGender = event.getString("creator_gender");
                                Boolean isActive = event.getBoolean("isactive");

                                Event eventObj = new Event(eventID, name, description, ageMin, ageMax, genderRestriction,
                                        lat, lgt, creatorID, startDate, endDate, category, creatorGender, isActive);
                                events.add(eventObj);

                            } catch (JSONException e) {
                                Log.d("myApp", "MyProfileActivity -> getEvents -> onResponse error: "+e);
                            }
                        }
                        showEvents();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("myApp", ""+error);
                    }
                });
        requestQueue.add(jsObjRequest);
    }

    public void showEvents(){
        if (events.size() > 0) {

            ListView lst = (ListView) findViewById(R.id.myEventList);
            Event_ListAdapter adapter = new Event_ListAdapter(this, events);
            lst.setAdapter(adapter);
            lst.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(MyProfileActivity.this, EventDetailsActivity.class);
                            intent.putExtra("eventId", events.get(position).getId()+"");

                            startActivity(intent);
                        }
                    }
            );
        } else {
            TextView noEvents = (TextView) findViewById(R.id.noHostedEvents);
            noEvents.setText(R.string.myProfile_no_events);
        }
    }
}
