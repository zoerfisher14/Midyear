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
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        min_date = df.format(c.getTime());
        max_date = nextMonth(min_date);

        setLocation(zip);

    }

    public APIRequest(String zip, String artist_name, Context context){

        this.artist_name = artist_name;
        this.context = context;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        min_date = df.format(c.getTime());
        max_date = nextMonth(min_date);

        setLocation(zip);

    }

    private String nextMonth(String min_date){
        String year = min_date.substring(0,4);
        String month = min_date.substring(5,7);
        String day = min_date.substring(8);

        if(Integer.parseInt(month)==12){
            day="31";
        }
        else{
            int m = Integer.parseInt(month)+1;
            month = m+"";
        }
        if(month.length()==1){
            month = 0+month;
        }
        return year+"-"+month+"-"+day;
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
