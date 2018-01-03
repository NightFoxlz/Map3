package com.example.liav.map3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnLogin;
    private  final static int LOGIN_PERMISSION = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //List<AuthUI.IdpConfig> providers = Arrays.asList(
         //       new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());


        btnLogin = findViewById(R.id.btnSignIn);
 //       regButton = findViewById(R.id.reg_bttn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        AuthUI.getInstance().createSignInIntentBuilder()
                        .setAllowNewEmailAccounts(true).build(), LOGIN_PERMISSION
                );

            }

        });
 //       regButton.setOnClickListener(new View.OnClickListener() {
  //          @Override
  //          public void onClick(View view) {
 //               Intent intent2 = new Intent(MainActivity.this,EmailPasswordActivity.class);
 //               startActivity(intent2);

  //          }
   //     });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_PERMISSION)
        {
            startNewActivity (resultCode,data);
        }
    }

    private void startNewActivity (int resultCode, Intent data){
        if (resultCode == RESULT_OK)
        {
            Intent intent = new Intent(MainActivity.this,ChooseRun.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(this,"login failed!!",Toast.LENGTH_SHORT).show();
        }
    }
}
