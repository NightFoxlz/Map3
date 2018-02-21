package com.example.liav.map3;

import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.liav.map3.Model.Route;
import com.example.liav.map3.Model.Tracking;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;


public class MapTracking extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Route curr_route;

    private String userUID, routUID, email;

    DatabaseReference locations;

    Double lat, lng;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Ref to firebase first
        locations = FirebaseDatabase.getInstance().getReference("Locations");

        //Get Intent
        if (getIntent() != null)
        {
            userUID = getIntent().getStringExtra("userUID");
            routUID = getIntent().getStringExtra("routUID");
        }
        //if (!TextUtils.isEmpty(email))
        //    loadLocationForThisUser(email);



    }

    public void drawRoute(String userID, String routeID){

        DatabaseReference routeRef= FirebaseDatabase.getInstance().getReference().child("Routes").child(userID);
        Query a=routeRef.orderByChild("uid").equalTo(routeID);
        a.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    curr_route = singleSnapshot.getValue(Route.class);
                }
                addLines(curr_route.locationList());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
    private void addLines(List< Location> coordination){
        if (coordination.size()>=2){
            Iterator<Location> iter = coordination.iterator();
            Location prv = iter.next();
            LatLng prev_latlng= new LatLng(prv.getLatitude(),prv.getLongitude());
            while (iter.hasNext()) {
                Location curr = iter.next();
                LatLng curr_latlng= new LatLng(curr.getLatitude(),curr.getLongitude());
                mMap.addPolyline(new PolylineOptions().add(prev_latlng,curr_latlng).width(5).color(Color.BLUE).geodesic(true));
                prev_latlng = curr_latlng;
            }
            //trying to focus map on the track
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(prev_latlng,13));
        }


    }


    private void loadLocationForThisUser(String email) {
        Query user_location = locations.orderByChild("email").equalTo(email);

        user_location.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot:dataSnapshot.getChildren()) {
                    Tracking tracking = postSnapShot.getValue(Tracking.class);

                    //Add marker for friend location
                    LatLng friendLocation = new LatLng(Double.parseDouble(tracking.getLat()),
                            Double.parseDouble(tracking.getLng()));
                    //creat location from user coordinates
                    Location currUser = new Location("");
                    currUser.setLatitude(lat);
                    currUser.setLongitude(lng);

                    //Create location from friend coordinates
                    Location friend = new Location("");
                    friend.setLatitude(Double.parseDouble(tracking.getLat()));
                    friend.setLongitude(Double.parseDouble(tracking.getLng()));

                    // clear all old markers
                    //mMap.clear();

                    //Add Friend marker on Map
                    mMap.addMarker(new MarkerOptions()
                            .position(friendLocation)
                            .title(tracking.getEmail())
                            .snippet("Distance "+new DecimalFormat("#.#").format((currUser.distanceTo(friend))/ 1000)+" km")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),12.0f));

                }
                //Create marker for current user
                LatLng current = new LatLng(lat,lng);
                mMap.addMarker(new MarkerOptions().position(current).title(FirebaseAuth.getInstance().getCurrentUser().getEmail()));

                //for debuging

                //    list.add(new LatLng(34,32));
                //  list.add(new LatLng(33 , 35));
                //addLines(list);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private double distance(Location currUser, Location friend) {
        double theta = currUser.getLongitude() - friend.getLongitude();
        double dist = Math.sin(deg2rad(currUser.getLatitude()))
                * Math.sin(deg2rad(friend.getLatitude()))
                * Math.cos(deg2rad(currUser.getLatitude()))
                * Math.cos(deg2rad(friend.getLatitude()))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist) ;
    }

    private double rad2deg(double rad) {
        return (rad*180/Math.PI);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI/180.0);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        drawRoute(userUID,routUID);

    }
}