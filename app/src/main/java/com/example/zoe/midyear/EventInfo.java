package com.example.zoe.midyear;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Zoe on 1/13/18.
 */

public class EventInfo {

    String artist, venue, date, time, link, location;

    public EventInfo(APIRequest a){
        try {
            URL url = new URL(a.APIRequestURL());
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            //connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if (data.getInt("cod") == 404) {
                artist = "Unavailable";
                venue = "Unavailable";
                date = "Unavailable";
                time = "Unavailable";
                link = "Unavailable";
                location = "Unavailable";
            }

            setAttributes(data);
        } catch (Exception e) {
            artist = "Unavailable";
            venue = "Unavailable";
            date = "Unavailable";
            time = "Unavailable";
            link = "Unavailable";
            location = "Unavailable";
        }
    }

    public void setAttributes(JSONObject data){
        try{
            JSONObject details = data.getJSONArray("results").getJSONObject(0);
            JSONObject event = details.getJSONArray("event").getJSONObject(0);
            JSONObject loc = details.getJSONArray("location").getJSONObject(0);
            JSONObject artistInfo = details.getJSONArray("performance").getJSONObject(0).getJSONArray("artist").getJSONObject(0);
            JSONObject start = details.getJSONArray("start").getJSONObject(0);

            artist = artistInfo.getString("displayName");
            venue = event.getJSONArray("venue").getJSONObject(0).getString("displayName");
            location = loc.getString("city");
            date = start.getString("date");
            time = start.getString("time");
            link = event.getString("uri");
        }
        catch(JSONException j){
            artist = "Unavailable";
            venue = "Unavailable";
            date = "Unavailable";
            time = "Unavailable";
            link = "Unavailable";
            location = "Unavailable";
        }

    }

    public String getArtist(){
        return artist;
    }

    public String getVenue(){
        return venue;
    }

    public String getDate(){
        return date;
    }

    public String getTime(){
        return time;
    }

    public String getLink(){
        return link;
    }

    public String getLocation(){
        return location;
    }


}
