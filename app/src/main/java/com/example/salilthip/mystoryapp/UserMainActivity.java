package com.example.salilthip.mystoryapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class UserMainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    private TextView emailProfile;
    private Button signout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        mAuth = FirebaseAuth.getInstance();

        emailProfile = (TextView)findViewById(R.id.emailProfileTxt);
        signout = (Button)findViewById(R.id.signoutBtn);

        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        emailProfile.setText(mAuth.getCurrentUser().getEmail());
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSignout();
            }
        });

    }

    private void callSignout(){
        mAuth.signOut();
        Intent i = new Intent(UserMainActivity.this, MainActivity.class);
        finish();
        startActivity(i);
    }
}
