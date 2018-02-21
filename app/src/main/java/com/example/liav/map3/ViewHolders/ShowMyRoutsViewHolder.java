package com.example.liav.map3.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.liav.map3.R;

/**
 * Created by Liav on 2/21/2018.
 */

public class ShowMyRoutsViewHolder extends RecyclerView.ViewHolder {
    public TextView date;
    public TextView dist;
    public TextView showTime;
    public Button showMap;
    public Button challenge;


    public ShowMyRoutsViewHolder(View itemView) {
        super(itemView);
        date = itemView.findViewById(R.id.DateText);
        dist = itemView.findViewById(R.id.DistText);
        showTime = itemView.findViewById(R.id.TimeText);
        showMap = itemView.findViewById(R.id.ShowMap);
        challenge = itemView.findViewById(R.id.Challenge);
        //itemView.setOnClickListener(this);
        //txtEmail = (TextView)itemView.findViewById(R.id.txt_email);
    }
}
