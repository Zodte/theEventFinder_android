package com.example.sareenaith.theeventfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Hoai Nam Duc Tran on 30/01/2017.
 * Basic stuff. Two buttons which either get my hosted events or my attended events.
 */

public class MyProfileActivity extends AppCompatActivity {

    Button hostedButton;
    Button attendedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        addListenerOnHostedEventsButton();
    }

    public void addListenerOnHostedEventsButton() {
        hostedButton = (Button) findViewById(R.id.myprofile_hosted_button);
        hostedButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MyProfileActivity.this, MyEventsListActivity.class);
                        intent.putExtra("source", "getHostedEvents/");
                        startActivity(intent);
                    }
                }
        );

        attendedButton = (Button) findViewById(R.id.myprofile_attended_button);
        attendedButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MyProfileActivity.this, MyEventsListActivity.class);
                        intent.putExtra("source", "getAttendedEvents/");
                        startActivity(intent);
                    }
                }
        );

    }
}
