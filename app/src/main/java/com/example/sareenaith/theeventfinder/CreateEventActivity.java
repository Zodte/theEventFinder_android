package com.example.sareenaith.theeventfinder;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;

import android.os.Bundle;

import android.view.Menu;
import android.view.View;

import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Created by Hoai Nam Duc Tran on 30/01/2017.
 */

public class CreateEventActivity extends Activity {

    private DatePicker datePicker;
    private Calendar calendarDate;
    private TextView dateView;
    private int year, month, day;

    private TimePicker timePicker;
    private Calendar calendarTime;
    private TextView timeView;
    private String format = "";
    private int hour, min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        dateView = (TextView) findViewById(R.id.createEvent_startDateId_input);
        calendarDate = Calendar.getInstance();
        year = calendarDate.get(Calendar.YEAR);

        month = calendarDate.get(Calendar.MONTH);
        day = calendarDate.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        timeView = (TextView) findViewById(R.id.createEvent_startTimeId_input);
        calendarTime = Calendar.getInstance();

        hour = calendarTime.get(Calendar.HOUR_OF_DAY);
        min = calendarTime.get(Calendar.MINUTE);
        showTime(hour, min);
    }
    /**
     * code for Date settings
     * TODO fix so user cant pick the date that happen in the past
     */
    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "Choose Date",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        } else if (id == 900) {
            return new TimePickerDialog(this,
                    myTimeListener, hour, min, true);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    /**
     * code for Time settings
     * TODO fix so user cant pick the time that happen in the past
     */
    public void setTime(View view) {
        showDialog(900);
        Toast.makeText(getApplicationContext(), "Choose Time",
                Toast.LENGTH_SHORT)
                .show();
    }

    private TimePickerDialog.OnTimeSetListener myTimeListener = new
            TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker arg0,
                                      int arg1, int arg2) {
                    // TODO Auto-generated method stub
                    // arg1 = hour
                    // arg2 = minute
                    showTime(arg1, arg2);
                }
            };

    private void showTime(int hour, int min) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        timeView.setText(new StringBuilder().append(hour).append(" : ").append(min)
                .append(" ").append(format));
    }
}
