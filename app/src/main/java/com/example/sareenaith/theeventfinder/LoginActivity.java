package com.example.sareenaith.theeventfinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Notandi on 14/03/2017.
 */

public class LoginActivity extends AppCompatActivity {
    private Config config = new Config();
    private final String URL = config.getUrl();
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private AccessToken accessToken;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    //Facebook login button
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();

            nextActivity(profile);
        }
        @Override
        public void onCancel() {        }
        @Override
        public void onError(FacebookException e) {
            Log.d("yoyo", e.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                nextActivity(newProfile);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object,GraphResponse response) {

                        JSONObject json = response.getJSONObject();
                        System.out.println("onCompleted response er: "+json);
                        try {
                            if(json != null){
                                String id = json.getString("id");
                                String email = json.getString("email");
                                String name = json.getString("name");
                                String link = json.getString("link");
                                String picture = json.getString("picture");
                                String gender = json.getString("gender");

                                editor.putString("fb_id", id); // Storing fbID
                                editor.putString("email", email);
                                editor.putString("name", name);
                                editor.putString("gender", gender);
                                editor.putString("link", link);
                                editor.putString("picture", picture);

                                editor.apply(); // commit changes
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,picture,gender");
                request.setParameters(parameters);
                request.executeAsync();

                accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();

                nextActivity(profile);
                Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        };
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Facebook login
        Profile profile = Profile.getCurrentProfile();
        nextActivity(profile);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // stop tracking profile and token.
    protected void onStop() {
        super.onStop();
        //Facebook login
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        //Facebook login
        callbackManager.onActivityResult(requestCode, responseCode, intent);

    }

    // proceed to the next activity and also send the user profile.
    private void nextActivity(Profile profile){
        if(profile != null){
            Intent main = new Intent(LoginActivity.this, EventsMapActivity.class);
            //main.putExtra("id", profile.getId());
            //main.putExtra("userName", profile.getName());
            //main.putExtra("token", sendToken(accessToken));
            sendUser(profile);
            startActivity(main);
            finish();
        }
    }

    private void sendUser(Profile profile)  {
        final Profile user = profile;
        final String fbid = sharedpreferences.getString("fb_id", null);
        final String name = sharedpreferences.getString("name", null);
        final String email = sharedpreferences.getString("email", null);
        final String gender = sharedpreferences.getString("gender", null);

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL+"check", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Volley", response);

                    editor.putString("db_id", response); // Storing our db ID of the new user
                    editor.apply();
                    final String check = sharedpreferences.getString("db_id", null);


                    // We really don't have to do any checks below, since we'll always receive the
                    // dbid of the user.

                    /*if(response.equals("true")) {
                        // TODO something else we want to do when the user already exists? Maybe welcome him back? :)
                        Log.d("Volley", "Fékk response frá /check og það var true => notandi var nú þegar til");
                        editor.putString("db_id", dbid); // Storing our db ID of the new user
                        editor.apply();
                    } else {
                        editor.putString("db_id", response); // Storing our db ID of the new user
                        editor.apply();
                    }*/

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
                    params.put("fullName", name);
                    params.put("fbid", fbid);
                    params.put("email", email);
                    params.put("gender", gender);
                    params.put("isAndroid", "true");
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
