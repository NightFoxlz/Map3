package com.example.liav.map3;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.liav.map3.Model.Route;
import com.example.liav.map3.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static io.fabric.sdk.android.Fabric.TAG;

public class AddFriend extends Activity {
    private EditText emailText;
    private TextView displyError;
    private Button okButton;
    private String currUserEmail;
    private User curr_user;
    private boolean isError;
    private User target;
    String targetEmail;
    private DatabaseReference userData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        this.isError = false;
        setPopUp();

        emailText = findViewById(R.id.addFriendEmail);
        displyError = findViewById(R.id.errorMsg);
        okButton = findViewById(R.id.addFriendBttn);
        curr_user = null;
        target = null;
        currUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".",",");
        userData = FirebaseDatabase.getInstance().getReference("Users");
        Query a=userData.orderByChild("email").equalTo(currUserEmail);
        a.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    curr_user = singleSnapshot.getValue(User.class);
                }
                if (curr_user == null) {
                    displyError.setText("Error getting user data");
                    displyError.setTextColor(Color.RED);
                    isError = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isError) return;
                targetEmail = emailText.getText().toString().replace(".",",");
                Query a= userData.orderByChild("email").equalTo(targetEmail);
                a.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                            target = singleSnapshot.getValue(User.class);
                        }
                        if (target == null) {
                            displyError.setText("Error: email don't exist");
                            displyError.setTextColor(Color.RED);
                        }
                        else{
                            if (! target.addFriend(currUserEmail,curr_user.getUid())){
                                displyError.setText("Error: already friends");
                                displyError.setTextColor(Color.RED);
                                target = null;
                                return;
                            }
                            curr_user.addFriend(targetEmail,target.getUid());
                            userData.child(currUserEmail).setValue(curr_user);
                            userData.child(targetEmail).setValue(target);
                            displyError.setText("Success");
                            displyError.setTextColor(Color.GREEN);
                            target = null;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }


    private void setPopUp() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.3));

        WindowManager.LayoutParams parm = getWindow().getAttributes();
        parm.gravity = Gravity.CENTER;
        parm.x = 0;
        parm.y = -20;

        getWindow().setAttributes(parm);
    }
}
