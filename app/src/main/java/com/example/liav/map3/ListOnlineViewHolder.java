package com.example.liav.map3;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Liav on 11/15/2017.
 */

public class ListOnlineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtEmail;
    ItemClickListenener itemClickListenener;
    public ListOnlineViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        txtEmail = (TextView)itemView.findViewById(R.id.txt_email);
    }

    public void setItemClickListener(ItemClickListenener itemClickListener){
        this.itemClickListenener = itemClickListener;
    }


    @Override
    public void onClick(View view) {
        itemClickListenener.onClick(view,getAdapterPosition());
    }
}
