package com.example.liav.map3;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liav.map3.Model.Challange;
import com.example.liav.map3.Model.Route;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

import static java.lang.System.exit;

public class Improve extends AppCompatActivity {
    TextView challanger , challangee, dist, time, winner, distError;
    Button showMap, navigate, startRun, stopRun;
    Route route;

    private boolean mRequestingLocationUpdates, currentlyRunning, distOk;

    private FusedLocationProviderClient mFusedLocationClient; //Provides access to the Fused Location Provider API.
    private SettingsClient mSettingsClient; // Provides access to the Location Settings API.
    private LocationRequest mLocationRequest; // Stores parameters for requests to the FusedLocationProviderApi.
    //Stores the types of location services the client is interested in using. Used for checking
    //settings to determine if the device has optimal location settings.
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback; // Callback for Location events.
    private Location mCurrentLocation; //Represents a geographical location.

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int MY_PREMMISION_REQUEST_CODE = 7171;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final String TAG = MainActivity.class.getSimpleName();

    private Challange c;

    private Location startingPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_improve);
        if (getIntent() == null)
        {
            Toast.makeText(this,"Error", Toast.LENGTH_SHORT).show();
            return;
        }
        c = (Challange) getIntent().getSerializableExtra("challange");
        initializeView();
        getRoute();
        setShowMap();
        setNavButton();
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[] {
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            } , MY_PREMMISION_REQUEST_CODE);
        }
        mRequestingLocationUpdates = true;
        currentlyRunning = false;
        distOk = false;
        startRun.setEnabled(false);
        startLocationUpdates();
    }

    private void setShowMap() {
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent map = new Intent ( Improve.this,MapTracking.class);
                map.putExtra("userUID", c.getChallengerUID());
                map.putExtra("routUID",c.getRouteUID());
                stopLocationUpdates();
                startActivity(map);
            }
        });
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        if(ActivityCompat.checkSelfPermission(Improve.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(Improve.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(Improve.this,new String[] {
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            } , MY_PREMMISION_REQUEST_CODE);
                            Toast.makeText(Improve.this,"Request Again", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                    mLocationCallback, Looper.myLooper());
                            Toast.makeText(Improve.this,"Request Update", Toast.LENGTH_SHORT).show();
                        }
                    }
                })

                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(Improve.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(Improve.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(Improve.this,"deleted", Toast.LENGTH_SHORT).show();
                    }
                });
        finish();
        return;
    }

    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = true;
                    }
                });
    }

    private void getRoute() {
        DatabaseReference routeRef= FirebaseDatabase.getInstance().getReference().child("Routes").child(c.getChallengerUID());
        Query a=routeRef.orderByChild("uid").equalTo(c.getRouteUID());
        a.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    route = singleSnapshot.getValue(Route.class);
                    startingPoint = route.firstPoint();
                }
                if (route == null){
                    Toast.makeText(Improve.this,"Error", Toast.LENGTH_SHORT).show();
                    exit(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setNavButton() {
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.google.com/maps/dir/?api=1" + "&destination=" + startingPoint.getLatitude()
                        + "," + startingPoint.getLongitude();
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                stopLocationUpdates();
                startActivity(intent);
            }
        });
    }

    private void initializeView() {
        challangee = findViewById(R.id.ChallengeeImprove);
        challanger = findViewById(R.id.ChallangerImprove);
        dist = findViewById(R.id.DistanceImprove);
        time = findViewById(R.id.TimeImprove);
        winner = findViewById(R.id.WinnerImprove);
        showMap = findViewById(R.id.ShowMapImprove);
        navigate = findViewById(R.id.NavStartImprove);
        startRun = findViewById(R.id.runButtonImprove);
        stopRun = findViewById(R.id.stopButtonImprove);
        distError = findViewById(R.id.DistError);
        challanger.setText("Challenger: " + c.getChallengerMail());
        challangee.setText("Challengee: " + c.getChallengeMail());
        dist.setText("Distance: " + Float.toString(c.getDistance()) + 'm');
        long secLong = c.getTime();
        int min = (int)secLong/60;
        int sec = (int)secLong - min*60;
        time.setText("Time: " + min+ " min " +sec + " sec");
        if (c.getWinner() == 0){
            winner.setText("Winner: " + c.getChallengerMail());
        }
        else winner.setText("Winner: " + c.getChallengeMail());
        stopRun.setVisibility(View.INVISIBLE);
        stopRun.setEnabled(false);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
    }

    private void createLocationCallback() {

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                Toast.makeText(Improve.this,"Location Callback ", Toast.LENGTH_SHORT).show();
                if (!currentlyRunning){
                    if (startingPoint == null) {
                        Toast.makeText(Improve.this,"P is null ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!distOk && startingPoint.distanceTo(mCurrentLocation) <= 10){
                        distOk = true;
                        startRun.setEnabled(true);
                        startRun.setBackgroundResource(R.drawable.startrunshape);
                        distError.setVisibility(View.INVISIBLE);
                    }
                    else if (distOk && startingPoint.distanceTo(mCurrentLocation) > 10){
                        distOk = false;
                        startRun.setEnabled(false);
                        startRun.setBackgroundResource(R.drawable.startrungrayed);
                        distError.setVisibility(View.VISIBLE);
                    }
                }
/*
                if (locationsList.isEmpty()){
                    startDate = new Date();
                    endDate = new Date();
                    prev = new Location("");
                    prev.setLatitude(mCurrentLocation.getLatitude());
                    prev.setLongitude(mCurrentLocation.getLongitude());
                }
                else endDate = new Date();

                curr = new Location("");
                curr.setLatitude(mCurrentLocation.getLatitude());
                curr.setLongitude(mCurrentLocation.getLongitude());
                if (point1 == null){
                    point1 = curr;
                    locationsList.add(curr);
                    Toast.makeText(ChooseRun.this,"Location Callback point 1 is null " + count, Toast.LENGTH_SHORT).show();
                }
                else{
                    if (point2 == null){
                        point2 = curr;
                        Toast.makeText(ChooseRun.this,"Location Callback point 2 is null " + count, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        point3 = curr;
                        if (calc_degre() < 160){
                            locationsList.add(point2);
                            point1 = point2;
                            point2 = point3;
                            point3 = null;
                            Toast.makeText(ChooseRun.this,"Location Callback " + count, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            point2 = point3;
                            point3 = null;
                            skiped++;
                            Toast.makeText(ChooseRun.this,"Location Callback Skipped " + skiped, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                count++;
                distance += prev.distanceTo(curr);
                prev = curr;
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());*/
            }
        };
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(Improve.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(Improve.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }
}
