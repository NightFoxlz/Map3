package com.example.liav.map3.ViewHolders;

/**
 * Created by Liav on 3/7/2018.
 */
    import android.support.v7.widget.RecyclerView;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import com.example.liav.map3.R;

    import java.util.List;
public class friendsRecyclerAdapter extends RecyclerView.Adapter<friendsRecyclerAdapter.MyViewHolder> {
    private List<String> emailList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView emailTex;

        public MyViewHolder(View view) {
            super(view);
            emailTex = view.findViewById(R.id.friendEmail);
        }
    }


    public friendsRecyclerAdapter(List<String> emailList) {
        this.emailList = emailList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_list_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String friendmail = this.emailList.get(position);
        holder.emailTex.setText(friendmail);
    }

    @Override
    public int getItemCount() {
        return this.emailList.size();
    }
}
