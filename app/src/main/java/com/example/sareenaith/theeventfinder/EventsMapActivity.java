package com.example.sareenaith.theeventfinder;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *  This class is responsible to check permission for Location Services, connect to Google Client API
 *  and initialize Google Map, show events etc. basically it is a main activity.
 */
public class EventsMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapClickListener {
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private Config config = new Config();
    private final String URL = config.getUrl();
    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Marker mCurrLocationMarker;
    private Button eventDetailsBtn;
    private ImageButton eventDetailsCloseBtn;
    private TextView eventDetailsNameTw, eventDetailsDescrTw, eventDetailsStartTime;
    RelativeLayout eventInfo;
    ImageButton imgBtn;
    RequestQueue requestQueue;
    private ArrayList<Event> events = new ArrayList<Event>();
    String from_searchDate;
    String to_searchDate;
    int spotsAvailable = 1;
    Boolean genderRestricted = false;
    String tag;
    LatLng reykjavikPos = new LatLng(64.14139101702763, -21.955103874206543);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_map);
        requestQueue = Volley.newRequestQueue(this);

        Bundle inBundle = getIntent().getExtras();
        if(inBundle != null) {
            from_searchDate = inBundle.getString("from_searchDate");
            to_searchDate = inBundle.getString("to_searchDate");
            //spotsAvailable = inBundle.getInt("spotsAvailable");
            genderRestricted = inBundle.getBoolean("genderRestricted");
            tag = inBundle.getString("tag");
        }else{
            resetSearchValues();
        }

        // Event details
        eventDetailsBtn = (Button) findViewById(R.id.eventMap_details_btn);
        eventDetailsNameTw = (TextView) findViewById(R.id.eventMap_details_name);
        eventDetailsDescrTw = (TextView) findViewById(R.id.eventMap_details_descr);
        eventDetailsStartTime = (TextView) findViewById((R.id.eventMap_details_starts));

        eventInfo = (RelativeLayout) findViewById(R.id.eventInfo);
        eventInfo.setVisibility(View.GONE);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // code for the option items from settings
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
                                LoginManager.getInstance().logOut();
                                intent = new Intent(EventsMapActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
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

    private void setMapClick() {
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {
        eventInfo.setVisibility(View.INVISIBLE);
    }

    public void changeToCreateEventActivity(View view) {
        Intent intent = new Intent(EventsMapActivity.this, CreateEventActivity.class);
        startActivity(intent);
    }

    //Sets search dates to default value
    public void resetSearchValues(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar now = Calendar.getInstance();
        from_searchDate = dateFormat.format(now.getTime());
        now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH)+3, now.get(Calendar.DAY_OF_MONTH));
        to_searchDate = dateFormat.format(now.getTime());
        genderRestricted = false;
        tag = "any";
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        setMapClick();

        // Zoom the map to Reykjavík
        mMap.moveCamera(CameraUpdateFactory.zoomTo(9));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(reykjavikPos));

        // Handle marker click
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                // Show the overlay when a marker is clicked.
                eventInfo.setVisibility(View.VISIBLE);
                // The index is stored in the title of the marker,  this title is not shown to the user.
                final int clickedEventIndex = Integer.parseInt(marker.getTitle());
                eventDetailsNameTw.setText(events.get(clickedEventIndex).getName());
                eventDetailsDescrTw.setText(events.get(clickedEventIndex).getDescription());
                eventDetailsStartTime.setText("Starts: "+ events.get(clickedEventIndex).getStartDate().toString().substring(0, 16));
                eventDetailsCloseBtn = (ImageButton) findViewById(R.id.eventMap_details_close_btn);

                // get the element which will hold the icon for the event...
                ImageView categoryIconImageView =
                        (ImageView) findViewById(R.id.eventMap_details_category_icon);
                // ... and set it
                switch(events.get(clickedEventIndex).getCategory()){
                    case "Sports":
                        categoryIconImageView.setImageResource(R.mipmap.football);
                        break;
                    case "Outdoors":
                        categoryIconImageView.setImageResource(R.mipmap.outdoors);
                        break;
                    case "Party":
                        categoryIconImageView.setImageResource(R.mipmap.beerbottle);
                        break;
                    case "Music":
                        categoryIconImageView.setImageResource(R.mipmap.guitar);
                        break;
                }

                // Move the camera to the clicked marker.
                LatLng pos = new LatLng(events.get(clickedEventIndex).getLat(), events.get(clickedEventIndex).getLgt());
                mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));

                // Click listener for "show event details" button on the overlay that appears when a marker
                // is clicked.
                eventDetailsBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(EventsMapActivity.this, EventDetailsActivity.class);
                        intent.putExtra("eventId", events.get(clickedEventIndex).getId()+"");
                        startActivity(intent);
                    }
                });

                // Click listener for "Hide this window" button on the overlay that appears when
                // a marker is clicked.
                eventDetailsCloseBtn.setOnClickListener(new View.OnClickListener() {
                    public  void onClick(View v) {
                        eventInfo.setVisibility(View.INVISIBLE);
                    }
                });
                return true;
            }
        });

        //Initialize Google Play Services and check whether access granted for using GPS
        // also checking the sdk version
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        // sending request to the server to get all events before a specific date.
        Toast.makeText(getApplicationContext(), from_searchDate + "   " + to_searchDate,
                Toast.LENGTH_LONG)
                .show();
        String uri = String.format(URL+"getEventsFromTo/"+ from_searchDate + "/" + to_searchDate + "?genderRestriction=%1$s&tag=%2$s", genderRestricted, tag);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, uri, null, new Response.Listener<JSONArray>() {
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

                                Event eventObj = new Event(eventID, name, description, ageMin, ageMax, genderRestriction,
                                        lat, lgt, creatorID, startDate, endDate, category );
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

    // add all events when map is ready
    public void addEvents(){
        // Default to football so it will render a football marker if it fails to fetch the category.
        int iconId = R.mipmap.football;
        for(int i = 0; i<events.size(); i++){
            switch(events.get(i).getCategory()){
                case "Sports":
                    iconId = R.mipmap.football;
                    break;
                case "Outdoors":
                    iconId = R.mipmap.outdoors;
                    break;
                case "Party":
                    iconId = R.mipmap.beerbottle;
                    break;
                case "Music":
                    iconId = R.mipmap.guitar;
                    break;
            }
            LatLng pos = new LatLng(events.get(i).getLat(), events.get(i).getLgt());
            mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(i+"")
                    .icon(BitmapDescriptorFactory.fromResource(iconId)));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        }
    }


    public void getMyLocation(View view) {
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

    }

    // below are code to connect to GoogleClientApi for GPS
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    // what happens when we successfull connected to the API. In this case,
    // we request the "balanced power accuracy" and update location when the user
    // is moving and enable Location service when user granted the app permission.
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final com.google.android.gms.common.api.Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try
                        {
                            status.startResolutionForResult(EventsMapActivity.this,REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {}
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    // when the connection to Google Client API is failed we will try to
    // find the solution and fix it.
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("myApp", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    // when the location change, remove the marker and stop the
    // location update.
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    // check whether Location service permission is granted.
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    // what we will do after received the permission.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    public void searchActivity(View view) {
        Intent intent = new Intent(EventsMapActivity.this, SearchActivity.class);
        intent.putExtra("from_searchDate", from_searchDate);
        intent.putExtra("to_searchDate", to_searchDate);
        startActivity(intent);
    }

}
