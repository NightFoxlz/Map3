/*
package com.example.liav.map3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.liav.map3.Model.Tracking;
import com.example.liav.map3.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListOnline extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener{


    //firebase
    DatabaseReference onlineRef,currentUserRef,counterRef,locations;
    FirebaseRecyclerAdapter<User,ListOnlineViewHolder> adapter;

    //View
    RecyclerView listOnline;
    RecyclerView.LayoutManager layoutManager;

    //Lcation
    private static final int MY_PREMMISION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RES_REQUEST=7172;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;


    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISTANCE = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_online);
        // Init View
        listOnline = findViewById(R.id.listOnline);
        listOnline.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listOnline.setLayoutManager(layoutManager);



        Log.d("TEST","START");
        //set toolbar and logout/join menu
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("something something is present system");
        setSupportActionBar(toolbar);

        //Firebase

        locations = FirebaseDatabase.getInstance().getReference("Locations");
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        counterRef = FirebaseDatabase.getInstance().getReference("lastOnline"); // create new child name lastOnline
        currentUserRef = FirebaseDatabase.getInstance().getReference("lastOnline")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()); //create new child in lastOnline with key is uid

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                    } , MY_PREMMISION_REQUEST_CODE);
        }
        else
        {
            if (checkPlayServices())
            {
                buildGoogleApiClient();
                creatLocationRequest();
                displyLocation();
            }
        }

        setupSystem();
        //After setup system , we just load all user from counterRef and display on RecyclerView
        //This is online list
        updateList();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PREMMISION_REQUEST_CODE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (checkPlayServices())
                    {
                        buildGoogleApiClient();
                        creatLocationRequest();
                        displyLocation();
                    }
                }
            }
            break;
        }
    }

    private void displyLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null)
        {
            // Update to Firebase
            locations.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(new Tracking(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            String.valueOf(mLastLocation.getLatitude()),
                            String.valueOf(mLastLocation.getLongitude())));
        }
        else
        {
            //Toast.makeText(this, "Couldn't get the location", Toast.LENGTH_SHORT).show();
            Log.d("TEST","Couldn't load location");
        }

    }

    private void creatLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(DISTANCE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_RES_REQUEST).show();
            }
            else
            {
                Toast.makeText(this,"This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void updateList() {
        Log.d("TEST","11");
        adapter = new FirebaseRecyclerAdapter<User, ListOnlineViewHolder>(
                User.class,
                R.layout.user_layout,
                ListOnlineViewHolder.class,
                counterRef
        ) {
            @Override
            protected void populateViewHolder(ListOnlineViewHolder viewHolder, final User model, int position) {
                viewHolder.txtEmail.setText(model.getEmail());
                //We need to implement item click of recyler view
                viewHolder.itemClickListenener = new ItemClickListenener() {
                    @Override
                    public void onClick(View view, int position) {
                        //Log.d("TEST","1");
                        // if model is current user not set click event
                        if (!model.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        {
                            Intent map = new Intent ( ListOnline.this,MapTracking.class);
                            map.putExtra("email",model.getEmail());
                            map.putExtra("lat",mLastLocation.getLatitude());
                            map.putExtra("lng",mLastLocation.getLongitude());
                            Log.d("TEST","2");
                            startActivity(map);
                        }
                    }
                };

            }
        };
        adapter.notifyDataSetChanged();
        listOnline.setAdapter(adapter);
    }

    private void setupSystem() {
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class))
                {
                    currentUserRef.onDisconnect().removeValue(); // Delete old value
                    // set Online user in list
                    counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(),"Online"));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        counterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    User user = postSnapshot.getValue(User.class);
                    Log.d("Log",""+user.getEmail()+" is "+user.getUid());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_join:
                counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(),"Online"));
                break;
            case R.id.action_logout:
                currentUserRef.removeValue();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displyLocation();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displyLocation();
        startLocationUpdate();

    }

    private void startLocationUpdate() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,  this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
        {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null)
        {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }
}


*/
