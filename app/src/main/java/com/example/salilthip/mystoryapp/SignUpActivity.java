package com.example.salilthip.mystoryapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailSignup,passwordSignup;
    private ImageButton selectedImage;
    private Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailSignup = (EditText)findViewById(R.id.emailSignup);
        passwordSignup = (EditText)findViewById(R.id.passwordSignup);
        signupBtn = (Button)findViewById(R.id.signupBtn);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getEmail = email.getText().toString();
                String getPassword = password.getText().toString();
                if(getEmail.isEmpty()||getPassword.isEmpty())
                    Toast.makeText(MainActivity.this, "Sign up Failed, please check your email and password",
                            Toast.LENGTH_SHORT).show();
                else
                    callSignup(getEmail, getPassword);
                finish();
            }
        });

    }

    private void callSignup(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Success", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Sign Up Failed, please check your email and password",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            userProfile();
                            Toast.makeText(MainActivity.this, "Account Created",
                                    Toast.LENGTH_SHORT).show();
                            Log.d("Test","Acoount Created");
                        }

                        // ...
                    }
                });
    }


}
