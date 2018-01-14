package com.example.zoe.midyear;

/**
 * Created by Zoe on 12/7/17.
 */

public class UserInfo {

    public String name, phone, location, artist;

    public UserInfo(){
        name = "John Doe";
        phone = "555-555-555";
        location = "North Pole";
    }

    public UserInfo(String n, String p, String l){
        name = n;
        phone = p;
        location = l;
    }
    public UserInfo(String n, String p, String l, String a){
        name = n;
        phone = p;
        location = l;
        artist = a;
    }

}
