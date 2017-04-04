package com.example.sareenaith.theeventfinder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Hoai Nam Duc Tran on 30/01/2017.
 */
public class SearchActivity extends AppCompatActivity {


    private int fromDateID = 999;
    private DatePicker fromDatePicker;
    private Calendar fromCalendar;
    private TextView fromDateView;
    private int fromYear, fromMonth, fromDay;

    private int toDateID = 888;
    private DatePicker toDatePicker;
    private Calendar toCalendar;
    private TextView toDateView;
    private int toYear, toMonth, toDay;

    Spinner tags;
    //NumberPicker available;
    CheckBox genderRestriction;

    private int fromDay_int = 0;
    private int fromHour_int = 0;
    private int toDay_int = 3;
    private int toHour_int = 0;
    //private int available_int = 1;
    private String tag_string = "any";
    private Boolean genderRestrict;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //From date setup
        fromDateView = (TextView) findViewById(R.id.fromDateView);
        fromCalendar = Calendar.getInstance();
        //fromDatePicker.setMinDate(fromCalendar.getTimeInMillis()-(24*60*60*1000));
        fromYear = fromCalendar.get(Calendar.YEAR);
        fromMonth = fromCalendar.get(Calendar.MONTH);
        fromDay = fromCalendar.get(Calendar.DAY_OF_MONTH);
        showDate(fromYear, fromMonth+1, fromDay, fromDateID);

        //To date setup
        toDateView = (TextView) findViewById(R.id.toDateView);
        toCalendar = Calendar.getInstance();
        toYear = toCalendar.get(Calendar.YEAR);
        toMonth = toCalendar.get(Calendar.MONTH );
        toDay = toCalendar.get(Calendar.DAY_OF_MONTH);
        showDate(toYear, toMonth+1, toDay+3, toDateID);

        tags = (Spinner) findViewById(R.id.tags_spinner);
        tags.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        //Works but server side needs to be implemented, will be implemented later
//        available = (NumberPicker) findViewById(R.id.available_numberPicker);
//        available.setMinValue(0);
//        available.setMaxValue(20);
//        available.setValue(1);
//        available.setWrapSelectorWheel(true);
//        available.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                available_int = newVal;
//            }
//        });

        genderRestriction = (CheckBox) findViewById(R.id.genderRestrict_checkbox);
        genderRestriction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                genderRestrict = isChecked;
            }
        });

        //Retrieves variables from map event acivity
        Bundle inBundle = getIntent().getExtras();
        if(inBundle != null) {
            Long from_searchDate = inBundle.getLong("from_searchDate");
            Long to_searchDate = inBundle.getLong("to_searchDate");
            //setupDateSpins(from_searchDate, to_searchDate);
        }

    }

    @SuppressWarnings("deprecation")
    public void setFromDate(View view) {
        showDialog(fromDateID);
    }

    @SuppressWarnings("deprecation")
    public void setToDate(View view) {
        showDialog(toDateID);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListenerFrom, fromYear, fromMonth, fromDay);
        }else if(id == 888){
            return new DatePickerDialog(this,
                    myDateListenerTo, toYear, toMonth, toDay);
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
                    setFromDate(arg1, arg2+1, arg3);
                    showDate(arg1, arg2+1, arg3, fromDateID);
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
                    setToDate(arg1, arg2+1, arg3);
                    showDate(arg1, arg2+1, arg3, toDateID);
                }
            };

    private void showDate(int year, int month, int day, int id) {
        if(id == 999) {
            fromDateView.setText(new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year));
        }else if(id == 888){
            toDateView.setText(new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year));
        }
    }

    public void setFromDate(int year, int month, int day){
        fromYear = year;
        fromMonth = month;
        fromDay = day;
    }

    public String getFromDate(){
        return (new StringBuilder().append(toYear).append("-")
                .append(fromMonth).append("-").append(fromDay)).toString();
    }

    public void setToDate(int year, int month, int day){
        toYear = year;
        toMonth = month;
        toDay = day;
    }

    public String getToDate(){
        return (new StringBuilder().append(toYear).append("-")
                .append(toMonth).append("-").append(toDay)).toString();
    }

    //Adds new tag to array of all tags
    public void addTag(String tag){
        if(!tag.equals("choose")){
            tag_string = tag;
        }else{
            tag_string = "any";
        }
    }


    //Submits search criteria to map event acticity
    public void acceptIt(View view) {
        Intent intent = new Intent(SearchActivity.this, EventsMapActivity.class);
        intent.putExtra("from_searchDate", getFromDate());
        intent.putExtra("to_searchDate", getToDate());
        //intent.putExtra("spotsAvailable", available_int);
        intent.putExtra("genderRestricted", genderRestrict);
        intent.putExtra("tag", tag_string);
        startActivity(intent);
    }


    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            addTag(parent.getItemAtPosition(pos).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }
}
