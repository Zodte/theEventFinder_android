package com.example.sareenaith.theeventfinder;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

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
 * Created by SareenAith on 28/2/2017.
 */

public class EventService {

    private Config config = new Config();
    private final String URL = config.getUrl();

    RequestQueue requestQueue;
    private ArrayList<Event> events = new ArrayList<Event>();

    public EventService() {
        Log.d("myApp", "init");
    }

    public void getAllEvents() {
        Log.d("myApp", "Fetching events");
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
            (Request.Method.GET, URL+"getAllEvents/2018-04-04", null, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {
                    // TODO Response
                    Log.d("myApp", response.length()+"Response received baaaby!:"+ response);

                    for(int i = 0; i < response.length(); i++){
                        JSONObject event = null;
                        try {
                            event = response.getJSONObject(i);
                            String eventID = event.getString("id");
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

                            Event eventObj = new Event(name, description, ageMin, ageMax, genderRestriction,
                                    lat, lgt, creatorID, startDate, endDate );
                            events.add(eventObj);
                            Log.d("myApp",""+eventObj.getStartDate());
                        } catch (JSONException e) {
                            Log.d("myApp", "buhuu");
                        }

                    }
                      Log.d("myApp", events.get(0).getName());
//                    EventsMapActivity eventsMapActivity = new EventsMapActivity();
//                    eventsMapActivity.setEvents(events);
//                    eventsMapActivity.addEvents();

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    Log.d("myApp", "VILLLAAAA:"+error);
                }
            });

        //requestQueue.add(jsObjRequest);
    }
}
