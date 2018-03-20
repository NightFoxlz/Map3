package com.example.liav.map3;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.liav.map3.Model.Challange;
import com.example.liav.map3.Model.User;
import com.example.liav.map3.ViewHolders.ChallangeRecyclerAdapter;
import com.example.liav.map3.ViewHolders.friendsRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class recivedChallangesList extends AppCompatActivity {
    private int selector;
    private static Context mContext;

    ChallangeRecyclerAdapter mAdapter;
    DatabaseReference userRef , currUserRef, userData;

    //View
    RecyclerView challangeList;
    RecyclerView.LayoutManager layoutManager;
    //friendsRecyclerAdapter mAdapter;
    User curr_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recived_challanges_list);
        challangeList = findViewById(R.id.challengeList);
        mContext = this;

        if (getIntent() == null)
        {
            Toast.makeText(this,"Error", Toast.LENGTH_SHORT).show();
            return;
        }
        selector = getIntent().getIntExtra("choose",0);

        fetchUserData();

        challangeList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        challangeList.setLayoutManager(layoutManager);

        Toolbar toolbar = findViewById(R.id.toolBar);
        if (selector == 0) toolbar.setTitle("Received Challenges");
        else toolbar.setTitle("Sent Challenges");
        setSupportActionBar(toolbar);

    }

    private void fetchUserData() {
        String currUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".",",");
        userData = FirebaseDatabase.getInstance().getReference("Users");
        Query a=userData.orderByChild("email").equalTo(currUserEmail);
        a.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    List<Challange> list;
                    curr_user = singleSnapshot.getValue(User.class);
                    if (selector == 0) list = curr_user.getRecivedChallanges();
                    else list = curr_user.getSentChallanges();
                    mAdapter = new ChallangeRecyclerAdapter(list);
                    challangeList.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                if (curr_user == null) {
                    Toast.makeText(recivedChallangesList.this,"Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void requestShowMap (String userUID,String routeUID){
        Intent map = new Intent ( mContext,MapTracking.class);
        map.putExtra("userUID",userUID);
        map.putExtra("routUID",routeUID);
        mContext.startActivity(map);
    }
}
