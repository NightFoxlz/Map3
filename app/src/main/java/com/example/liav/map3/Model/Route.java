package com.example.liav.map3.Model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liav on 1/10/2018.
 */

public class Route {
    private String uniqueId;
    private long routeTime;
    private float distance;
    private List<Location> points;

    public Route (String id ,long time, float distance, List<Location> cord){
        this.uniqueId=id;
        this.routeTime=time;
        this.distance=distance;
        this.points=new ArrayList<Location>(cord) ;
    }

    public String getUid(){
        return uniqueId;
    }

    public long getTime(){
        return routeTime;
    }

    public float getDist(){
        return distance;
    }

    public List<Location> getPoints(){
        return points;
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