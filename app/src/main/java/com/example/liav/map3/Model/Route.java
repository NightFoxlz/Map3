package com.example.liav.map3.Model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Liav on 1/10/2018.
 */

public class Route {
    private String uid;
    private long routeTime;
    private float distance;
    private List<Double> latitudeList;
    private List<Double> longitudeList;
    private Date date;

    public Route(){}

    public Route (String id ,long time, float distance, List<Double> lat,List<Double> lon, Date date){
        this.uid=id;
        this.routeTime=time;
        this.distance=distance;
        this.latitudeList=lat;
        this.longitudeList=lon;
        this.date = date;
    }

    public Date getDate() { return date; }

    public void setDate (Date date) { this.date = date; }

    public String getUid(){
        return uid;
    }

    public void setUid(String uid) { this.uid = uid; }

    public long getRouteTime(){
        return routeTime;
    }

    public void setRouteTime( long routeTime) { this.routeTime = routeTime; }

    public float getDistance(){
        return distance;
    }

    public void setDistance( float distance) { this.distance = distance; }

    public List<Double> getLatitudeList() {return latitudeList; }

    public void setLatitudeList( List<Double> latitudeList) { this.latitudeList = latitudeList; }

    public List<Double> getLongitudeList() {return longitudeList; }

    public void setLongitudeList( List<Double> longitudeList) { this.longitudeList = longitudeList; }

    public List<Location> locationList(){
        List<Location> points = new ArrayList<Location>();
        for (int i=0 ;i < this.longitudeList.size() ;i++){
            Location temp = new Location("");
            temp.setLongitude(this.longitudeList.get(i));
            temp.setLatitude(this.latitudeList.get(i));
            points.add(temp);
        }
        return points;
    }

    public Location firstPoint(){
        Location l = new Location("");
        l.setLatitude(latitudeList.get(0));
        l.setLongitude(longitudeList.get(0));
        return l;
    }

    public Location lastPoint(){
        Location l = new Location("");
        l.setLatitude(latitudeList.get(latitudeList.size()-1));
        l.setLongitude(longitudeList.get(longitudeList.size()-1));
        return l;
    }

}


//public float getDistance(List<LatLng> cordi){
//    float distance=0;
//    LatLng next=null;
//    LatLng prv=cordi.get(0);

//    for(LatLng element: cordi){
//        next=element;




  //  }

 //   return distance;
//}