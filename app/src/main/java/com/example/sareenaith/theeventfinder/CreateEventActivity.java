package com.example.sareenaith.theeventfinder;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.DatePickerDialog;
import android.app.Dialog;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import android.view.ViewGroup;
import android.widget.CheckBox;
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
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import static android.view.View.GONE;
import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import static java.lang.Integer.parseInt;

public class CreateEventActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private Config config = new Config();
    private final String URL = config.getUrl();
    private String check = "no";
    private TextView dateViewFrom, dateViewTo;
    private int yearFrom, monthFrom, dayFrom;
    private int yearTo, monthTo, dayTo;

    private EditText eventNameTxt;
    private EditText eventDescriptionTxt;
    private double lat;
    private double lgt;
    private EditText minAge;
    private EditText maxAge;

    private RelativeLayout mainLayout;
    private RelativeLayout mapLayout;

    private CheckBox checkBox;
    private TextView timeViewFrom, timeViewTo;
    private int hourFrom, minFrom;
    private int hourTo, minTo;
    private int idDateFrom = 999;
    private int idDateTo = 899;
    private int idTimeFrom = 900;
    private int idTimeTo = 800;

    private String nameCharsMax = "40"; // max must equal the text in the xml file. Otherwise it will look silly.
    private String descCharsMax = "100";

    private String eventNameBeforeChange;
    private String eventDescBeforeChange;

    private EditText validateLocation;

    SharedPreferences sharedpreferences;

    AwesomeValidation mAwesomeValidation;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mAwesomeValidation = new AwesomeValidation(BASIC);

        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        eventNameTxt = (EditText) findViewById(R.id.createEvent_nameId_input);
        eventDescriptionTxt = (EditText) findViewById(R.id.createEvent_descriptionId_input);
        validateLocation = (EditText) findViewById(R.id.createEvent_hiddenPosText);


        // validating the 2 fields above. The regex as the 3rd param is the string pattern that is accepted.
        mAwesomeValidation.addValidation(this, R.id.createEvent_nameId_input, RegexTemplate.NOT_EMPTY, R.string.noEmptyFields);
        mAwesomeValidation.addValidation(this, R.id.createEvent_descriptionId_input, RegexTemplate.NOT_EMPTY, R.string.noEmptyFields);
        // adding a hidden textfield so we can validate the lat and lng coordinates.
        mAwesomeValidation.addValidation(this, R.id.createEvent_hiddenPosText, RegexTemplate.NOT_EMPTY, R.string.noLocation);

        checkBox = (CheckBox) findViewById(R.id.createEvent_checkSexId_checkBox);

        //minAge = (EditText) findViewById(R.id.createEvent_minAgeId_input);
        //maxAge = (EditText) findViewById(R.id.createEvent_maxAgeId_input);
        // From Date
        dateViewFrom = (TextView) findViewById(R.id.createEvent_startDateId_input);
        Calendar calendarDateFrom = Calendar.getInstance();

        yearFrom = calendarDateFrom.get(Calendar.YEAR);
        monthFrom = calendarDateFrom.get(Calendar.MONTH);
        dayFrom = calendarDateFrom.get(Calendar.DAY_OF_MONTH);
        showDate(yearFrom, monthFrom+1, dayFrom, idDateFrom);

        timeViewFrom = (TextView) findViewById(R.id.createEvent_startTimeId_input);
        Calendar calendarTimeFrom = Calendar.getInstance();

        hourFrom = calendarTimeFrom.get(Calendar.HOUR_OF_DAY);
        minFrom = calendarTimeFrom.get(Calendar.MINUTE);
        showTime(hourFrom, minFrom, idTimeFrom);

        // End Date
        dateViewTo = (TextView) findViewById(R.id.createEvent_endDateId_input);
        Calendar calendarDateTo = Calendar.getInstance();
        yearTo = calendarDateTo.get(Calendar.YEAR);
        monthTo = calendarDateTo.get(Calendar.MONTH);
        dayTo = calendarDateTo.get(Calendar.DAY_OF_MONTH);
        showDate(yearTo, monthTo+1, dayTo, idDateTo);

        timeViewTo = (TextView) findViewById(R.id.createEvent_endTimeId_input);
        Calendar calendarTimeTo = Calendar.getInstance();

        hourTo = calendarTimeTo.get(Calendar.HOUR_OF_DAY);
        minTo = calendarTimeTo.get(Calendar.MINUTE);
        showTime(hourTo, minTo, idTimeTo);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        addTextListeners();
    }

    public void addTextListeners() {

        eventNameTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                EditText etEventNameBeforeChange = (EditText) findViewById(R.id.createEvent_nameId_input);
                eventNameBeforeChange = etEventNameBeforeChange.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView charactersLeft = (TextView) findViewById(R.id.createEvent_nameLength);
                String text = s.toString();
                Integer newValue = parseInt(nameCharsMax) - text.length();
                System.out.println(newValue);
                if (newValue < 0) {
                    eventNameTxt.setText(eventNameBeforeChange);
                    return;
                }
                charactersLeft.setText((newValue.toString()));

            }
        });

        eventDescriptionTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                EditText etEventDescrBeforeChange = (EditText) findViewById(R.id.createEvent_descriptionId_input);
                eventDescBeforeChange = etEventDescrBeforeChange.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

                TextView charactersLeft = (TextView) findViewById(R.id.createEvent_descrLength);
                String text = s.toString();
                Integer newValue = parseInt(descCharsMax) - text.length();
                if (newValue < 0) {
                    eventDescriptionTxt.setText(eventDescBeforeChange);
                    return;
                }
                charactersLeft.setText((newValue.toString()));

            }
        });

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

        validateLocation.setText(Double.toString(lat));
        validateLocation.setVisibility(View.GONE);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);

        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
        mMap.addMarker(markerOptions);
        markerOptions.title("Event Coordinator");

        Toast.makeText(getApplicationContext(), "lat:"+lat+ " " +"long:"+lgt,Toast.LENGTH_SHORT).show();
    }
    /*
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
            int minVal = parseInt(minAge.getText().toString().trim());
            int maxVal = parseInt(maxAge.getText().toString().trim());
            if(minVal > maxVal) {
                Toast.makeText(getApplicationContext(), "The min age cannot be higher than max age",
                        Toast.LENGTH_SHORT).show();
            }
        } catch(NumberFormatException error) {
            Toast.makeText(getApplicationContext(),"Please enter the required age",Toast.LENGTH_SHORT).show();
        }
    }
*/
    // show layout "visible" and "invisible" when choosing location.
    public void setLocation(View view) {
        mAwesomeValidation.clear();

        mainLayout = (RelativeLayout) findViewById(R.id.createEvent_mainLayout);
        ViewGroup.LayoutParams mainLayoutParams = mainLayout.getLayoutParams();
        mainLayoutParams.height = 0;
        mainLayout.setLayoutParams(mainLayoutParams);

        mapLayout = (RelativeLayout) findViewById(R.id.map_fragment);
        ViewGroup.LayoutParams params = mapLayout.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mapLayout.setLayoutParams(params);
    }

    // show layout "visible" and "invisible" when accepting location.
    public void onClickAccept(View view) {
        mainLayout = (RelativeLayout) findViewById(R.id.createEvent_mainLayout);
        ViewGroup.LayoutParams mainLayoutParams = mainLayout.getLayoutParams();
        mainLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mainLayout.setLayoutParams(mainLayoutParams);

        mapLayout = (RelativeLayout) findViewById(R.id.map_fragment);
        ViewGroup.LayoutParams params = mapLayout.getLayoutParams();
        params.height = 0;
        mapLayout.setLayoutParams(params);
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
            dateViewFrom.setText(new StringBuilder().append(year).append("-")
                    .append(month).append("-").append(day));
        } else if (id == idDateTo) {
            dateViewTo.setText(new StringBuilder().append(year).append("-")
                    .append(month).append("-").append(day));
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
            timeViewFrom.setText(new StringBuilder().append(hour).append(":").append(min));
        }

        else if (id == idTimeTo) {
            timeViewTo.setText(new StringBuilder().append(hour).append(":").append(min));
        }
    }

    // this is the "onClick" method of the (+) button on the create event page
    public void sendEvent(View view) throws JSONException {
        System.out.println("validate location er: "+validateLocation.getText().toString());

        if (mAwesomeValidation.validate()) {
            Toast.makeText(this, "Validation Successful", Toast.LENGTH_LONG).show();
            //checkAge();
            final String dbid = sharedpreferences.getString("db_id", null);
            System.out.println("inní sendEvent og dbid er: "+dbid);
            try {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL+"createEvent", new Response.Listener<String>() {
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
                        params.put("ageMin", "18"); //minAge.getText().toString().trim());
                        params.put("ageMax", "100"); //maxAge.getText().toString().trim());
                        params.put("startDate", dateViewFrom.getText().toString().trim().concat(" ").concat(timeViewFrom.getText().toString().trim()));
                        params.put("endDate", dateViewTo.getText().toString().trim().concat(" ").concat(timeViewTo.getText().toString().trim()));
                        params.put("isAndroid", "true");
                        params.put("db_id", dbid);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);

                Intent intent = new Intent(CreateEventActivity.this, EventsMapActivity.class);
                startActivity(intent);

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMapClick();
    }
}
