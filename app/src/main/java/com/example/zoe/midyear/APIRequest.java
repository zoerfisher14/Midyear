package com.example.zoe.midyear;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Zoe on 1/11/18.
 */

public class APIRequest {

    private String min_date, max_date, lat, lon, artist_name;
    private Context context;

    public APIRequest(String zip, Context context){
        this.context = context;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        min_date = df.format(c.getTime());
        max_date = nextMonth(min_date);

        setLocation(zip);

    }

    public APIRequest(String zip, String artist_name){

        this.artist_name = artist_name;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        min_date = df.format(c.getTime());
        max_date = nextMonth(min_date);

        setLocation(zip);

    }

    private String nextMonth(String min_date){
        int month = Integer.parseInt(min_date.substring(5,7));
        if(month == 12){
            month = 0;
            int year = Integer.parseInt(min_date.substring(0,4))+1;
            min_date = year + min_date.substring(4);
        }
        month++;
        String m = month+"";
        if(m.length()==1){
            m = "0"+m;
        }
        return min_date.substring(0,5)+m+min_date.substring(8);
    }

    private void setLocation(String z){
        final Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocationName(z, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                lat = "" + address.getLatitude();
                lon = "" + address.getLongitude();
            }
        } catch (IOException e) {
            lat = ""+0;
            lon = ""+0;
        }
    }

    public String APIRequestURL(){
        if(artist_name!=null){
            return context.getString(R.string.apiRequestArtist, artist_name, lat, lon, min_date, max_date);
        }
        else{
            return context.getString(R.string.apiRequest, lat, lon, min_date, max_date);
        }
    }

}
