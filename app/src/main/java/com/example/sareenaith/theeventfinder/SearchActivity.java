package com.example.sareenaith.theeventfinder;

import android.app.DatePickerDialog;
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


    SeekBar fromDay;
    SeekBar fromHour;
    TextView fromSearchRange;
    SeekBar toDay;
    SeekBar toHour;
    TextView searchRange;
    Spinner tags;
    TextView tags_text;
    Button clearTag;
    //NumberPicker available;
    CheckBox genderRestriction;

    private int fromDay_int = 0;
    private int fromHour_int = 0;
    private int toDay_int = 3;
    private int toHour_int = 0;
    private int available_int = 1;
    private ArrayList<String> tags_array = new ArrayList<>();
    private Boolean genderRestrict;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Set
        fromDay = (SeekBar) findViewById(R.id.fromDay_range);
        fromDay.setMax(30);

        fromDay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fromDay_int = progress;
                showFromSearchRangeText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //fromHour
        fromHour = (SeekBar) findViewById(R.id.fromHour_range);
        fromHour.setMax(24);
        fromHour.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fromHour_int = progress;
                showFromSearchRangeText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        fromSearchRange = (TextView) findViewById(R.id.fromSearchRange_text);
        //toDay
        toDay = (SeekBar) findViewById(R.id.toDay_range);
        toDay.setMax(30);
        toDay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                toDay_int = progress;
                showToSearchRangeText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //toHour
        toHour = (SeekBar) findViewById(R.id.toHour_range);
        toHour.setMax(24);
        toHour.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                toHour_int = progress;
                showToSearchRangeText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        searchRange = (TextView) findViewById(R.id.searchRange_text);

        tags = (Spinner) findViewById(R.id.tags_spinner);
        tags.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        tags_text = (TextView) findViewById(R.id.tags_text);

        clearTag = (Button) findViewById(R.id.clear_btn);

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

    //FromDate in milliseconds
    //toDate in milliseconds
    public void setupDateSpins(Long fromDate, Long toDate ){
        Calendar cal = Calendar.getInstance();
        int fDay = (int) Math.floor(((((fromDate - cal.getTimeInMillis())/1000)/60)/60)/24);
        int fHour = (int) Math.floor(((((fromDate - cal.getTimeInMillis())/1000)/60)/60)%24);
        int tDay = (int) Math.floor(((((toDate - cal.getTimeInMillis())/1000)/60)/60)/24);
        int tHour = (int) Math.floor(((((toDate - cal.getTimeInMillis())/1000)/60)/60)%24);
        toDay.setProgress(tDay);
        toHour.setProgress(tHour);
    }

    public void showFromSearchRangeText(){
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        Calendar cal = Calendar.getInstance();

        long startTime = cal.getTimeInMillis() + (fromDay_int * 24 * 60 * 60 * 1000) + (fromHour_int * 60 * 60 * 1000);

        cal.setTimeInMillis(startTime);
        String start = dateFormat1.format(cal.getTime());

        fromSearchRange.setText("From: \n" + start);
    }

    public void showToSearchRangeText(){
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        Calendar cal = Calendar.getInstance();

        long endTime = cal.getTimeInMillis() + (toDay_int * 24 * 60 * 60 * 1000) + (toHour_int * 60 * 60 * 1000);

        cal.setTimeInMillis(endTime);
        String end = dateFormat1.format(cal.getTime());

        searchRange.setText("To: \n " + end);
    }

    public Long getFromDate(){
        Calendar now = Calendar.getInstance();
        Long startDate = now.getTimeInMillis() + (fromDay_int * 24 * 60 * 60 * 1000) + (fromHour_int * 60 * 60 * 1000);
        return startDate;
    }

    public Long getToDate(){
        Calendar now = Calendar.getInstance();
        Long endDate = now.getTimeInMillis() + (toDay_int * 24 * 60 * 60 * 1000) + (toHour_int * 60 * 60 * 1000);
        return endDate;
    }

    public void addTag(String tag){
        if(!tag.equals("Choose")){
            if(tags_array.size() == 0) {
                clearTag.setVisibility(View.VISIBLE);
            }
            if(!tags_array.contains(tag)) {
                tags_array.add(tag);
            }
        }
        displayTagsText();
    }

    //Displays which tags have been choosen by user
    public void displayTagsText() {
        StringBuilder sb = new StringBuilder();
        for (String s : tags_array)
        {
            sb.append(s);
            sb.append("\t");
        }
        tags_text.setText(sb);
    }

    public void clearTag(View view){
        tags_array.clear();
        displayTagsText();
        clearTag.setVisibility(View.GONE);
    }

    //Submits search criteria to map event acticity
    public void acceptIt(View view) {
        Intent intent = new Intent(SearchActivity.this, EventsMapActivity.class);
        intent.putExtra("from_searchDate", getFromDate());
        intent.putExtra("to_searchDate", getToDate());
        //intent.putExtra("spotsAvailable", available_int);
        intent.putExtra("genderRestricted", genderRestrict);
        intent.putExtra("tags", tags_array);
        startActivity(intent);
    }


    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            addTag(parent.getItemAtPosition(pos).toString());
            parent.setSelection(0);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }
}
