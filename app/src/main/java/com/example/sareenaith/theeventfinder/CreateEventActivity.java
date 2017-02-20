package com.example.sareenaith.theeventfinder;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.io.EOFException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.DatePickerDialog;
import android.app.Dialog;

import android.text.InputFilter;
import android.util.Log;
import android.view.View;

import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import utilityclass.InputFilterMinMax;

/**
 * Created by Hoai Nam Duc Tran on 30/01/2017.
 */

public class CreateEventActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private static final String URL = "http://10.0.2.2:3000/login";
    private String check = "no";
    private DatePicker datePicker;
    private Calendar calendarDateFrom, calendarDateTo;
    private TextView dateViewFrom, dateViewTo;
    private int yearFrom, monthFrom, dayFrom;
    private int yearTo, monthTo, dayTo;

    private EditText eventNameTxt;
    private EditText eventDescriptionTxt;
    private ImageButton eventButton;
    private double lat;
    private double lgt;
    private EditText minAge;
    private EditText maxAge;

    private RelativeLayout mainLayout;
    private RelativeLayout mapLayout;

    private CheckBox checkBox;
    private TimePicker timePicker;
    private Calendar calendarTimeFrom, calendarTimeTo;
    private TextView timeViewFrom, timeViewTo;
    //private String format = "";
    private int hourFrom, minFrom;
    private int hourTo, minTo;
    private int idDateFrom = 999;
    private int idDateTo = 899;
    private int idTimeFrom = 900;
    private int idTimeTo = 800;

    private GoogleMap mMap;
    private Event event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        eventNameTxt = (EditText) findViewById(R.id.createEvent_nameId_input);
        eventDescriptionTxt = (EditText) findViewById(R.id.createEvent_descriptionId_input);
        eventButton = (ImageButton) findViewById(R.id.createEvent_submitId_btn);

        checkBox = (CheckBox) findViewById(R.id.createEvent_checkSexId_checkBox);

        minAge = (EditText) findViewById(R.id.createEvent_minAgeId_input);
        maxAge = (EditText) findViewById(R.id.createEvent_maxAgeId_input);

        // From Date
        dateViewFrom = (TextView) findViewById(R.id.createEvent_startDateId_input);
        calendarDateFrom = Calendar.getInstance();

        yearFrom = calendarDateFrom.get(Calendar.YEAR);
        monthFrom = calendarDateFrom.get(Calendar.MONTH);
        dayFrom = calendarDateFrom.get(Calendar.DAY_OF_MONTH);
        showDate(yearFrom, monthFrom+1, dayFrom, idDateFrom);

        timeViewFrom = (TextView) findViewById(R.id.createEvent_startTimeId_input);
        calendarTimeFrom = Calendar.getInstance();

        hourFrom = calendarTimeFrom.get(Calendar.HOUR_OF_DAY);
        minFrom = calendarTimeFrom.get(Calendar.MINUTE);
        showTime(hourFrom, minFrom, idTimeFrom);

        // End Date
        dateViewTo = (TextView) findViewById(R.id.createEvent_endDateId_input);
        calendarDateTo = Calendar.getInstance();
        yearTo = calendarDateTo.get(Calendar.YEAR);
        monthTo = calendarDateTo.get(Calendar.MONTH);
        dayTo = calendarDateTo.get(Calendar.DAY_OF_MONTH);
        showDate(yearTo, monthTo+1, dayTo, idDateTo);

        timeViewTo = (TextView) findViewById(R.id.createEvent_endTimeId_input);
        calendarTimeTo = Calendar.getInstance();

        hourTo = calendarTimeTo.get(Calendar.HOUR_OF_DAY);
        minTo = calendarTimeTo.get(Calendar.MINUTE);
        showTime(hourTo, minTo, idTimeTo);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void setGenderRestriction(View view) {
        if(checkBox.isChecked()) {
            check = "yes";
            Toast.makeText(getApplicationContext(), ""+check, Toast.LENGTH_SHORT).show();
        } else {
            check = "no";
            Toast.makeText(getApplicationContext(), ""+check,Toast.LENGTH_SHORT).show();
        }
    }

    private void setMapClick() {
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {
        lat = point.latitude;
        lgt = point.longitude;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);

        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
        mMap.addMarker(markerOptions);
        markerOptions.title("Event Coordinator");

        Toast.makeText(getApplicationContext(), "lat:"+lat+ " " +"long:"+lgt,Toast.LENGTH_SHORT).show();
    }

    /**
     * naming-convenient change later.
     * Test function for switching activity for now, will be use later for
     * when submitting/creating an event
     */
//    @Override
//    public void onClick(View view) {
//        sendSubmit();
//        Intent intent = new Intent(CreateEventActivity.this, EventsMapActivity.class);
//        startActivity(intent);
//    }
    // check the age
    public void checkAge() {
        String minText = minAge.getText().toString().trim();
        String maxText = maxAge.getText().toString().trim();
        if(minText.equals("")) {
            minAge.setText(R.string.age);
        } else if(maxText.equals("")) {
            maxAge.setText(R.string.age);
        }
        try {
            int minVal = Integer.parseInt(minAge.getText().toString().trim());
            int maxVal = Integer.parseInt(maxAge.getText().toString().trim());
            if(minVal > maxVal) {
                Toast.makeText(getApplicationContext(), "The min age cannot be higher than max age",
                        Toast.LENGTH_SHORT).show();
            }
        } catch(NumberFormatException error) {
            Toast.makeText(getApplicationContext(),"Please enter the required age",Toast.LENGTH_SHORT).show();
        }
    }

    public void setLocation(View view) {
        mainLayout = (RelativeLayout) findViewById(R.id.createEvent_mainLayout);
        ViewGroup.LayoutParams mainLayoutParams = mainLayout.getLayoutParams();
        mainLayoutParams.height = 0;
        mainLayout.setLayoutParams(mainLayoutParams);

        mapLayout = (RelativeLayout) findViewById(R.id.map_fragment);
        ViewGroup.LayoutParams params = mapLayout.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mapLayout.setLayoutParams(params);
        Log.d("myApp", "Called");
    }

    /*
     * TODO - implement (lat,long) for retrieving location
     */
    public void onClickAccept(View view) {
        mainLayout = (RelativeLayout) findViewById(R.id.createEvent_mainLayout);
        ViewGroup.LayoutParams mainLayoutParams = mainLayout.getLayoutParams();
        mainLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mainLayout.setLayoutParams(mainLayoutParams);

        mapLayout = (RelativeLayout) findViewById(R.id.map_fragment);
        ViewGroup.LayoutParams params = mapLayout.getLayoutParams();
        params.height = 0;
        mapLayout.setLayoutParams(params);
        Log.d("myApp", "Location set");
    }

    /**
     * code for Date settings
     * TODO fix so user cant pick the date that happen in the past
     */
    @SuppressWarnings("deprecation")
    public void setDateFrom(View view) {
        showDialog(idDateFrom);
        Toast.makeText(getApplicationContext(), "Choose Start Date",
                Toast.LENGTH_SHORT)
                .show();
    }

    @SuppressWarnings("deprecation")
    public void setDateTo(View view) {
        showDialog(idDateTo);
        Toast.makeText(getApplicationContext(), "Choose End Date",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == idDateFrom) {
            return new DatePickerDialog(this,
                    myDateListenerFrom, yearFrom, monthFrom, dayFrom);
        } else if(id == idDateTo) {
            return new DatePickerDialog(this,
                    myDateListenerTo, yearTo, monthTo, dayTo);
        } else if (id == idTimeFrom) {
            return new TimePickerDialog(this,
                    myTimeListenerFrom, hourFrom, minFrom, true);
        } else if(id == idTimeTo) {
            return new TimePickerDialog(this,
                    myTimeListenerTo, hourTo, minTo, true);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListenerFrom = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3, idDateFrom);
                }
            };

    private DatePickerDialog.OnDateSetListener myDateListenerTo = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3, idDateTo);
                }
            };

    private void showDate(int year, int month, int day, int id) {
        if(id == idDateFrom) {
            dateViewFrom.setText(new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year));
        } else if (id == idDateTo) {
            dateViewTo.setText(new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year));
        }
    }

    /**
     * code for Time settings
     * TODO fix so user cant pick the time that happen in the past
     */
    public void setTimeFrom(View view) {
        showDialog(idTimeFrom);
        Toast.makeText(getApplicationContext(), "Choose Start Time",
                Toast.LENGTH_SHORT)
                .show();
    }

    public void setTimeTo(View view) {
        showDialog(idTimeTo);
        Toast.makeText(getApplicationContext(), "Choose End time",
                Toast.LENGTH_SHORT)
                .show();
    }

    private TimePickerDialog.OnTimeSetListener myTimeListenerFrom = new
            TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker arg0,
                                      int arg1, int arg2) {
                    // TODO Auto-generated method stub
                    // arg1 = hour
                    // arg2 = minute
                    showTime(arg1, arg2, idTimeFrom);
                }
            };

    private TimePickerDialog.OnTimeSetListener myTimeListenerTo = new
            TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker arg0,
                                      int arg1, int arg2) {
                    // TODO Auto-generated method stub
                    // arg1 = hour
                    // arg2 = minute
                    showTime(arg1, arg2, idTimeTo);
                }
            };

    private void showTime(int hour, int min, int id) {
        if(id == idTimeFrom) {
            /*if (hour == 0) {
                hour += 12;
                format = "AM";
            } else if (hour == 12) {
                format = "PM";
            } else if (hour > 12) {
                hour -= 12;
                format = "PM";
            } else {
                format = "AM";
            }*/

            timeViewFrom.setText(new StringBuilder().append(hour).append(" : ").append(min));
        }

        else if (id == idTimeTo) {
            /*if (hour == 0) {
                hour += 12;
                format = "AM";
            } else if (hour == 12) {
                format = "PM";
            } else if (hour > 12) {
                hour -= 12;
                format = "PM";
            } else {
                format = "AM";
            }*/

            timeViewTo.setText(new StringBuilder().append(hour).append(" : ").append(min));
        }
    }

    public void sendEvent(View view) throws JSONException {
        checkAge();
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
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
                    params.put("lgt", String.valueOf(lgt));
                    params.put("genderRestrict", check);
                    params.put("descr", eventDescriptionTxt.getText().toString().trim());
                    params.put("ageMin", minAge.getText().toString().trim());
                    params.put("ageMax", maxAge.getText().toString().trim());
                    params.put("endDate", dateViewTo.getText().toString().trim());
                    params.put("startDate", dateViewFrom.getText().toString().trim());
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMapClick();
    }
}
