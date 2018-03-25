package com.example.liav.map3.ViewHolders;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.liav.map3.ChooseRun;
import com.example.liav.map3.MapTracking;
import com.example.liav.map3.Model.Challange;
import com.example.liav.map3.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by Liav on 3/7/2018.
 */

public class ChallangeRecyclerAdapter extends RecyclerView.Adapter<ChallangeRecyclerAdapter.MyViewHolder> {
    private List<Challange> challangeList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView challangerText, challangeeText, distText, timeText, winnerText;
        public Button showMap, improve;

        public MyViewHolder(View view) {
            super(view);
            challangeeText = view.findViewById(R.id.ChallengeeText);
            challangerText = view.findViewById(R.id.ChallengerText);
            distText = view.findViewById(R.id.DistText);
            timeText = view.findViewById(R.id.TimeText);
            winnerText = view.findViewById(R.id.WinnerText);
            showMap = view.findViewById(R.id.ShowMap);
            improve = view.findViewById(R.id.Improve);
        }
    }


    public ChallangeRecyclerAdapter(List<Challange> challangeList) {
        this.challangeList = challangeList;
    }

    @Override
    public ChallangeRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recived_challanges_layout, parent, false);

        return new ChallangeRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChallangeRecyclerAdapter.MyViewHolder holder, int position) {
        final Challange challange = this.challangeList.get(position);
        holder.challangerText.setText("Challenger: " + challange.getChallengerMail());
        holder.challangeeText.setText("Challengee: " + challange.getChallengeMail());
        holder.distText.setText("Distance: " + Float.toString(challange.getDistance()) + 'm');
        long secLong = challange.getTime();
        int min = (int)secLong/60;
        int sec = (int)secLong - min*60;
        holder.timeText.setText("Time: " + min+ " min " +sec + " sec");
        if (challange.getWinner() == 0){
            holder.winnerText.setText("Winner: " + challange.getChallengerMail());
        }
        else holder.winnerText.setText("Winner: " + challange.getChallengeMail());
        holder.showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.example.liav.map3.recivedChallangesList.requestShowMap(challange.getChallengerUID(),challange.getRouteUID());
            }
        });
        holder.improve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.example.liav.map3.recivedChallangesList.improveListener(challange);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.challangeList.size();
    }
}
