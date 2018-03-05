package com.example.liav.map3;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liav.map3.Model.Route;
import com.example.liav.map3.Model.User;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.lang.Math;

public class ChooseRun extends AppCompatActivity {

    private Button runingButton , stopButton , showRoutes, addFriend;

    private  List<Location> locationsList;

    private DatabaseReference Routes, currUserRoutes, currRoute;

    private String routeUID;

    private Date startDate, endDate;

    private float distance;

    private Location curr, prev;

    private Location point1, point2,point3;

    private Boolean mRequestingLocationUpdates; //////////////////////////////////////////////////

    private FusedLocationProviderClient mFusedLocationClient; //Provides access to the Fused Location Provider API.
    private SettingsClient mSettingsClient; // Provides access to the Location Settings API.
    private LocationRequest mLocationRequest; // Stores parameters for requests to the FusedLocationProviderApi.
    //Stores the types of location services the client is interested in using. Used for checking
    //settings to determine if the device has optimal location settings.
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback; // Callback for Location events.
    private Location mCurrentLocation; //Represents a geographical location.

    private String mLastUpdateTime;


    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int MY_PREMMISION_REQUEST_CODE = 7171;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final String TAG = MainActivity.class.getSimpleName();

    private int count, skiped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_run);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
        //    @Override
         //   public void onClick(View view) {
         //       Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
         //               .setAction("Action", null).show();
         //   }
        //});
        setTitle ("Main Area");
        runingButton = findViewById(R.id.runButton);
        stopButton = findViewById(R.id.stopButton);
        showRoutes = findViewById(R.id.ShowRoutes);
        addFriend = findViewById(R.id.AddFriend);

        setPopUp();
        setUser();

        mRequestingLocationUpdates = false;

        stopButton.setVisibility(View.INVISIBLE);
        stopButton.setEnabled(false);

        Routes = FirebaseDatabase.getInstance().getReference("Routes");

        //String myEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        //FirebaseDatabase.getInstance().getReference("Users").child(myEmail.replace(".",",")).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        locationsList = new ArrayList<Location>();

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            } , MY_PREMMISION_REQUEST_CODE);
        }

        runingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(ChooseRun.this,ListOnline.class);
                //startActivity(intent);
                count =1; skiped = 0;
                point1 = null; point2 = null; point3 = null;
                locationsList.clear();
                currUserRoutes = Routes.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                currRoute = currUserRoutes.push();
                routeUID = currRoute.getKey();
                distance = 0;
                stopButton.setVisibility(View.VISIBLE);
                //findViewById(R.id.enable_run_button).setVisibility(View.GONE);
                //findViewById(R.id.enable_stop_button).setVisibility(View.VISIBLE);
                stopButton.setEnabled(true);
                runingButton.setVisibility(View.INVISIBLE);
                runingButton.setEnabled(false);
                mRequestingLocationUpdates = true;


                startLocationUpdates();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runingButton.setVisibility(View.VISIBLE);
                //findViewById(R.id.enable_run_button).setVisibility(View.VISIBLE);
                //findViewById(R.id.enable_stop_button).setVisibility(View.GONE);
                runingButton.setEnabled(true);
                stopButton.setVisibility(View.INVISIBLE);
                stopButton.setEnabled(false);
                stopLocationUpdates();
                if (point2 != null) locationsList.add(point2);
                Toast.makeText(ChooseRun.this,"Skipped " + skiped + " out of " + count, Toast.LENGTH_SHORT).show();
                List<Double> lat = new ArrayList<Double>();
                List<Double> lon = new ArrayList<Double>();
                for (int i=0; i<locationsList.size(); i++){ //locationsList is a list of Location
                    lat.add(locationsList.get(i).getLatitude());
                    lon.add(locationsList.get(i).getLongitude());
                }
                Route temp = new Route(routeUID,(endDate.getTime()-startDate.getTime())/1000,distance,lat,lon, new Date());
                currRoute.setValue(temp); //currRoute is a database reference
                mRequestingLocationUpdates = false;
                Intent map = new Intent ( ChooseRun.this,MapTracking.class);
                map.putExtra("userUID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                map.putExtra("routUID",routeUID);
                startActivity(map);
            }
        });

        showRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showRout = new Intent ( ChooseRun.this,ShowMyRouts.class);
                startActivity(showRout);
            }
        });

    }

    private void setUser() {
        String currUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".",",");
        DatabaseReference userData = FirebaseDatabase.getInstance().getReference("Users");
        Query a=userData.orderByChild("email").equalTo(currUserEmail);
        a.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User curr_user = null;
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    curr_user = singleSnapshot.getValue(User.class);
                }
                if (curr_user == null){
                    String currUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".",",");
                    DatabaseReference userData = FirebaseDatabase.getInstance().getReference("Users").child(currUserEmail);
                    userData.setValue(new User (currUserEmail,FirebaseAuth.getInstance().getCurrentUser().getUid()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setPopUp() {
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddFriend.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);

        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(20);
        textView.setTextColor(Color.WHITE);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(textView);
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        if(ActivityCompat.checkSelfPermission(ChooseRun.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(ChooseRun.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(ChooseRun.this,new String[] {
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                } , MY_PREMMISION_REQUEST_CODE);
                            Toast.makeText(ChooseRun.this,"Request Again", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                    mLocationCallback, Looper.myLooper());
                            Toast.makeText(ChooseRun.this,"Request Update", Toast.LENGTH_SHORT).show();
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
                                    rae.startResolutionForResult(ChooseRun.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(ChooseRun.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }
                    }
                });
    }


    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }




    private void createLocationCallback() {

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
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
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            }
        };
    }

    private double calc_degre() {
        double length1, length2,skalar;
        length1 = Math.sqrt((Math.pow(point2.getLongitude()-point1.getLongitude(),2))+(Math.pow(point2.getLatitude()-point1.getLatitude(),2)));
        length2 = Math.sqrt((Math.pow(point3.getLongitude()-point2.getLongitude(),2))+(Math.pow(point3.getLatitude()-point2.getLatitude(),2)));
        skalar = (point1.getLongitude()-point2.getLongitude())*(point3.getLongitude()-point2.getLongitude())+
                (point1.getLatitude()-point2.getLatitude())*(point3.getLatitude()-point2.getLatitude());
        return Math.toDegrees(Math.acos(skalar/(length1*length2)));
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
                        mRequestingLocationUpdates = false;
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
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(ChooseRun.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(ChooseRun.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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
