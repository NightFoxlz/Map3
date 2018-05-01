package com.example.liav.map3;


import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.liav.map3.Model.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Iterator;
import java.util.List;

public class MapFragHolder extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    Route route;
    Marker prevMarker;

    public MapFragHolder() {prevMarker = null;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mView = inflater.inflate(R.layout.activity_map_frag_holder, container, false);
        return mView;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) mView.findViewById(R.id.mapFragWidg);
        if (mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (route != null){
            drawRoute();
        }
    }

    public void setRoute(Route route){
        this.route = route;
        if (mGoogleMap != null){
            drawRoute();
        }
    }

    private void drawRoute() {
        List<Location> coordination = this.route.locationList();
        if (coordination.size()>=2){
            Iterator<Location> iter = coordination.iterator();
            Location prv = iter.next();
            LatLng prev_latlng= new LatLng(prv.getLatitude(),prv.getLongitude());
            while (iter.hasNext()) {
                Location curr = iter.next();
                LatLng curr_latlng= new LatLng(curr.getLatitude(),curr.getLongitude());
                mGoogleMap.addPolyline(new PolylineOptions().add(prev_latlng,curr_latlng).width(5).color(Color.BLUE).geodesic(true));
                prev_latlng = curr_latlng;
            }
            //trying to focus map on the track
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(prev_latlng,16));
        }
    }

    public void updateLocation(Location point){
        if (prevMarker != null) prevMarker.remove();
        LatLng p = new LatLng(point.getLatitude(),point.getLongitude());
        prevMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(p)
                .title("User location"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p,16));
    }
}
