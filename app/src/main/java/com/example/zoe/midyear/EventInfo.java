package com.example.zoe.midyear;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.EventLog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

/**
 * Created by Zoe on 1/13/18.
 */

public class EventInfo {

    private JSONObject parentObject;
    private String displayName, link;

    public EventInfo(String s) {
        try{
            parentObject = new JSONObject(s);
            JSONArray events = parentObject.getJSONObject("resultsPage").getJSONObject("results").getJSONArray("event");
            Random r = new Random();
            int i = r.nextInt(events.length());
            link = events.getJSONObject(i).getString("uri");
            displayName = events.getJSONObject(i).getString("displayName");
        } catch(JSONException er){
            er.printStackTrace();
            link = "unavailable";
            displayName = "unavailable";
        }


    }

    public String getLink(){
        return link;
    }

    public String getDisplayName(){ return displayName; }

}




