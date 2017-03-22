package com.example.sareenaith.theeventfinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class HostedEventsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Config config = new Config();
    private final String URL = config.getUrl();
    SharedPreferences sharedpreferences;
    RequestQueue requestQueue;

    private ArrayList<Event> events = new ArrayList<Event>();
    private TextView noEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosted_events);
        requestQueue = Volley.newRequestQueue(this);
        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        getEvents();
    }

    public void getEvents() {
        final String dbid = sharedpreferences.getString("db_id", null);

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, URL+"getHostedEvents/"+dbid, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        // TODO Response
                        Log.d("myApp", response.length()+" Response inní HostedEventsActivity -> getEvents :"+ response);

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

                                Event eventObj = new Event(eventID, name, description, ageMin, ageMax, genderRestriction,
                                        lat, lgt, creatorID, startDate, endDate );
                                events.add(eventObj);
                                Log.d("myApp",""+eventObj.getStartDate());
                            } catch (JSONException e) {
                                Log.d("myApp", "error er: "+e);
                            }
                        }
                        showEvents();
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

    public void showEvents(){
        if (events.size() > 0) {
            String[] eventNames = new String[events.size()];
            for(int i = 0; i<events.size(); i++){
                System.out.println("Er inní showEvents og i er: "+i);
                eventNames[i] = events.get(i).getName();
            }

            ArrayAdapter<String> adapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventNames);
            ListView lst = (ListView) findViewById(R.id.eventList);
            lst.setAdapter(adapter);
        } else {
            noEvents = (TextView) findViewById(R.id.noHostedEvents);
            noEvents.setText(R.string.myProfile_no_hosted_events);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO: handle clicks on each item in the list to expand them
    }
}

