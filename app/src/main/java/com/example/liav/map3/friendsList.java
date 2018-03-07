package com.example.liav.map3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.liav.map3.CustomClickListeners.ClickListener;
import com.example.liav.map3.CustomClickListeners.friendListClickListener;
import com.example.liav.map3.Model.Challange;
import com.example.liav.map3.Model.Route;
import com.example.liav.map3.Model.User;
import com.example.liav.map3.ViewHolders.friendsRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class friendsList extends AppCompatActivity {
    
    DatabaseReference userRef , currUserRef, userData;
    //FirebaseRecyclerAdapter<Route,ShowMyRoutsViewHolder> adapter;

    //View
    RecyclerView friendList;
    RecyclerView.LayoutManager layoutManager;
    friendsRecyclerAdapter mAdapter;

    User curr_user, target = null;

    Route curr_route;

    String targetEmail, userUID, routeUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        if (getIntent() != null)
        {
            userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            routeUID = getIntent().getStringExtra("routeUID");
        }

        fetchRoute();

        friendList = findViewById(R.id.friendsList);
        friendList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        friendList.setLayoutManager(layoutManager);

        updateFriendList();

        //set toolbar and logout/join menu
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("Choose a friend to challange");
        setSupportActionBar(toolbar);

        friendList.addOnItemTouchListener(new friendListClickListener(getApplicationContext(), friendList, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                targetEmail = curr_user.getFriendList().get(position);
                //Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
                setChallange();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //firebase references
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        currUserRef = userRef.child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".",","));

    }

    private void fetchRoute() {
        DatabaseReference routeRef= FirebaseDatabase.getInstance().getReference().child("Routes").child(userUID);
        Query a=routeRef.orderByChild("uid").equalTo(routeUID);
        a.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    curr_route = singleSnapshot.getValue(Route.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setChallange() {
        userData = FirebaseDatabase.getInstance().getReference("Users");
        Query a= userData.orderByChild("email").equalTo(targetEmail);
        a.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    target = singleSnapshot.getValue(User.class);
                }
                if (target == null) {
                    Toast.makeText(friendsList.this,"Error fetching target data", Toast.LENGTH_SHORT).show();
                }
                else{
                    Challange c = new Challange(curr_route.getUid(),curr_user.getEmail(),target.getEmail(),
                                                curr_route.getRouteTime(),curr_route.getDistance());
                    if (! target.addRecivedChallange(c)){
                        Toast.makeText(friendsList.this,"Error : Challange already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (! curr_user.addSentChallange(c)){
                        Toast.makeText(friendsList.this,"Error : Challange already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    userData.child(curr_user.getEmail()).setValue(curr_user);
                    userData.child(targetEmail).setValue(target);
                    Toast.makeText(friendsList.this,"Challange sent to: " + targetEmail, Toast.LENGTH_SHORT).show();
                    target = null;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void updateFriendList() {
        String currUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".",",");
        DatabaseReference userData = FirebaseDatabase.getInstance().getReference("Users");
        Query a=userData.orderByChild("email").equalTo(currUserEmail);
        a.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    curr_user = singleSnapshot.getValue(User.class);
                    mAdapter = new friendsRecyclerAdapter(curr_user.getFriendList());
                    friendList.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                if (curr_user == null) {
                    Toast.makeText(friendsList.this,"Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
