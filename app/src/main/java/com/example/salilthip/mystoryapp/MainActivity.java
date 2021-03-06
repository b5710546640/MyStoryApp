package com.example.salilthip.mystoryapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    private EditText email,password;
    private Button loginBtn;
    private ImageButton profileImage;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        email = (EditText)findViewById(R.id.emailEdt);
        password = (EditText)findViewById(R.id.passwordEdt);
        loginBtn = (Button)findViewById(R.id.loginBtn);
        profileImage = (ImageButton)findViewById(R.id.profileImageBtn);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        if (mAuth.getCurrentUser() != null) {
            email.setText(mAuth.getCurrentUser().getEmail());
            if(mAuth.getCurrentUser().getPhotoUrl()!=null)
            profileImage.setImageURI(mAuth.getCurrentUser().getPhotoUrl());
        }

        password.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    // Perform action on key press
                    String getEmail = email.getText().toString();
                    String getPassword = password.getText().toString();
                    if(getEmail.isEmpty()||getPassword.isEmpty())
                        Toast.makeText(MainActivity.this, "Sign in Failed, please check your email and password",
                                Toast.LENGTH_SHORT).show();
                    else
                        callSignin(getEmail, getPassword);
                    return true;
                }
                return false;
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                String getEmail = email.getText().toString();
                String getPassword = password.getText().toString();
                if(getEmail.isEmpty()||getPassword.isEmpty())
                    Toast.makeText(MainActivity.this, "Sign in Failed, please check your email and password",
                            Toast.LENGTH_SHORT).show();
                else
                    callSignin(getEmail, getPassword);
//                alertNoMember();
            }
        });

    }

    private void alertInvalidAccount(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Please signup before login!").setCancelable(false)
                .setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setTitle("Invalid Account");
        dialog.show();
    }

    private void callSignin(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Test", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Test", "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, "Login Failed, please check your email and password",
                                    Toast.LENGTH_SHORT).show();
                            alertInvalidAccount();
                        }else {
                            Intent i = new Intent(MainActivity.this, ViewAllStory.class);
                            finish();
                            Log.e("Login","Successful");
                            startActivity(i);
                        }
                        // ...
                    }
                });
    }
}
