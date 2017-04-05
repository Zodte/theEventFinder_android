package com.example.sareenaith.theeventfinder;

/**
 * Server is hosted on Heroku so if you want to connect to live data you use that URL, else you
 * need to figure out your local IP if you wish to develop against a local server instead.
 */

public class Config {
    // The url used for all our http requests.
    //private String url = "https://eventure2.herokuapp.com/";
    private String url = "http://192.168.0.101:3000/";
    public String getUrl() {
        return url;
    }
}
