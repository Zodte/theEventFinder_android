package com.example.sareenaith.theeventfinder;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventsMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private Config config = new Config();
    private final String URL = config.getUrl();
    private GoogleMap mMap;
    ImageButton imgBtn;
    RequestQueue requestQueue;
    private ArrayList<Event> events = new ArrayList<Event>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_map);
        requestQueue = Volley.newRequestQueue(this);

        imgBtn = (ImageButton) findViewById(R.id.eventMap_settings_icon);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(EventsMapActivity.this, imgBtn);
                popup.getMenuInflater().inflate(R.menu.create_drop_list, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent;
                        switch (item.getItemId()) {
                            case R.id.item_one:
                                intent = new Intent(EventsMapActivity.this, MyProfileActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.item_two:
                                intent = new Intent(EventsMapActivity.this, MyProfileActivity.class);
                                startActivity(intent);
                                return true;
                            default:
                                return true;
                        }
                    }
                });
                Object menuHelper;
                Class[] argTypes;
                try {
                    Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                    fMenuHelper.setAccessible(true);
                    menuHelper = fMenuHelper.get(popup);
                    argTypes = new Class[]{boolean.class};
                    menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                } catch (Exception e) {
                    // Possible exceptions are NoSuchMethodError and NoSuchFieldError
                    //
                    // In either case, an exception indicates something is wrong with the reflection code, or the
                    // structure of the PopupMenu class or its dependencies has changed.
                    //
                    // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
                    // but in the case that they do, we simply can't force icons to display, so log the error and
                    // show the menu normally.
                }
                popup.show();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /*
    public void changeToProfileActivity(View view) {
        Intent intent = new Intent(EventsMapActivity.this, MyProfileActivity.class);
        startActivity(intent);
    }
    */

    public void changeToCreateEventActivity(View view) {
        Intent intent = new Intent(EventsMapActivity.this, CreateEventActivity.class);
        startActivity(intent);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, URL+"getallevents/2017-12-12", null, new Response.Listener<JSONArray>() {

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
                        addEvents();
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

    //Always reset all events
    //Might be better to add them on top of others
//    public void setEvents(ArrayList<Event> tmpEvents){
//        events = new ArrayList<Event>(tmpEvents);
//    }
//
    public void addEvents(){
        for(int i = 0; i<events.size(); i++){
            LatLng pos = new LatLng(events.get(i).getLat(), events.get(i).getLgt());
            mMap.addMarker(new MarkerOptions().position(pos).title(events.get(i).getName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        }
   }

}
