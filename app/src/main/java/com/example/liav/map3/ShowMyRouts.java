package com.example.liav.map3;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.liav.map3.Model.Route;
import com.example.liav.map3.Model.User;
import com.example.liav.map3.ViewHolders.ShowMyRoutsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class ShowMyRouts extends AppCompatActivity {

    DatabaseReference routeRef , myRouteRef;
    FirebaseRecyclerAdapter<Route,ShowMyRoutsViewHolder> adapter;

    //View
    RecyclerView routeList;
    RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_routs);
        // Init View
        routeList = findViewById(R.id.routeList);
        routeList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        routeList.setLayoutManager(layoutManager);
        //set toolbar and logout/join menu
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("My Routes");
        setSupportActionBar(toolbar);
        //firebase references
        routeRef = FirebaseDatabase.getInstance().getReference("Routes");
        myRouteRef = routeRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        updateList();
    }

    private void updateList() {
        adapter = new FirebaseRecyclerAdapter<Route, ShowMyRoutsViewHolder>(
                Route.class,
                R.layout.my_routs_layout,
                ShowMyRoutsViewHolder.class,
                myRouteRef
        ) {
            @Override
            protected void populateViewHolder(ShowMyRoutsViewHolder viewHolder, final Route model, int position) {
                viewHolder.date.setText(model.getDate().toString());
                viewHolder.dist.setText(Float.toString(model.getDistance()) + " m");
                long secLong = model.getRouteTime();
                int min = (int)secLong/60;
                int sec = (int)secLong - min*60;
                viewHolder.showTime.setText( min+ " min " +sec + " sec");

                viewHolder.showMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent map = new Intent ( ShowMyRouts.this,MapTracking.class);
                        map.putExtra("userUID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.putExtra("routUID",model.getUid());
                        startActivity(map);
                    }
                });

                viewHolder.challenge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent challange = new Intent(ShowMyRouts.this,friendsList.class);
                        challange.putExtra("routeUID",model.getUid());
                        startActivity(challange);
                    }
                });


            }
        };
        adapter.notifyDataSetChanged();
        routeList.setAdapter(adapter);
    }
}
