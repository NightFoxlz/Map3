package com.example.liav.map3.Model;

/**
 * Created by Liav on 11/22/2017.
 */

public class Tracking {
    private String email,uid,lat,lng;

    public Tracking() {

    }

    public Tracking ( String email, String uid, String lat, String lng)
    {
        this.email = email;
        this.uid = uid;
        this.lat = lat;
        this.lng = lng;
    }

    public String getEmail() {
        return email;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getUid() {
        return uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
