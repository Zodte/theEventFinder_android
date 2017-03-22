package com.example.sareenaith.theeventfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Hoai Nam Duc Tran on 30/01/2017.
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
                        Intent intent = new Intent("com.example.sareenaith.theeventfinder.HostedEventsActivity");
                        startActivity(intent);
                    }
                }
        );

    }
}
