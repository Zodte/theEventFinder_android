package com.example.sareenaith.theeventfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {


    String id, userName, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Bundle inBundle = getIntent().getExtras();
        id = inBundle.get("id").toString();
        userName = inBundle.get("userName").toString();
        token = inBundle.get("token").toString();

        TextView nameView = (TextView) findViewById(R.id.userName);
        nameView.setText(userName);

        TextView idView = (TextView) findViewById(R.id.id);
        idView.setText(id);

        TextView tokenView = (TextView) findViewById(R.id.token);
        tokenView.setText(token);

        final TextView mTextView = (TextView) findViewById(R.id.text);

        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                Intent login = new Intent(TestActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        });

    }

    public void goToEvents(View view) {
        Intent intent = new Intent(TestActivity.this, EventsMapActivity.class);
        startActivity(intent);
    }
}