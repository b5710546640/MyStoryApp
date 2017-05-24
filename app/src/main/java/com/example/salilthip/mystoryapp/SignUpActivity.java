package com.example.salilthip.mystoryapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailSignup,passwordSignup;
    private ImageButton selectedImage;
    private Button signupBtn;

    public static final int READ_EXTERNAL_STORAGE = 0;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;
    private Firebase mRootRef;
    private Uri mImageUri = null;
    private String storageUri = "";
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorage;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();


        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }


        mRootRef = new Firebase("gs://mystoryapp-2e9ec.appspot.com/").child("User_Details").push();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mystoryapp-2e9ec.appspot.com/");

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        emailSignup = (EditText)findViewById(R.id.emailSignup);
        passwordSignup = (EditText)findViewById(R.id.passwordSignup);
        signupBtn = (Button)findViewById(R.id.signupBtn);
        selectedImage = (ImageButton)findViewById(R.id.selectImageBtn);

        mProgressDialog = new ProgressDialog(SignUpActivity.this);

        passwordSignup.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    // Perform action on key press
                    String getEmail = emailSignup.getText().toString();
                    String getPassword = passwordSignup.getText().toString();
                    if(getEmail.isEmpty()||getPassword.isEmpty())
                        Toast.makeText(SignUpActivity.this, "Sign up Failed, please check your email and password",
                                Toast.LENGTH_SHORT).show();
                    else {
                        Log.e("Pass","email :"+getEmail);
                        Log.e("Pass","password :"+getPassword);
                        callSignup(getEmail, getPassword);

                        finish();

                    }
                }
                return false;
            }
        });

        Log.e("Access","Into Signin Activity");

        selectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askImageForSignUp();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                String getEmail = emailSignup.getText().toString();
                String getPassword = passwordSignup.getText().toString();
                if(getEmail.isEmpty()||getPassword.isEmpty())
                    Toast.makeText(SignUpActivity.this, "Sign up Failed, please check your email and password",
                            Toast.LENGTH_SHORT).show();
                else {
                    Log.e("Pass","email :"+getEmail);
                    Log.e("Pass","password :"+getPassword);
                    callSignup(getEmail, getPassword);

                    finish();

                }
            }
        });

    }

    private void askImageForSignUp(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Call for permission", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
            }
        } else {
            callGallery();
        }
    }

    private void callGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    public void uploadImageToDatabase(String uri){
        Log.e("INN","Upload Image");


        String uidStr = mAuth.getCurrentUser().getUid()+"";
        mDatabaseRef.child("User_ID").setValue(uidStr);
        mDatabaseRef.child("image_url").setValue(uri);


        if (mAuth!=null) {
            Toast.makeText(getApplicationContext(), "Please Sign In", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getApplicationContext(), "Update Info", Toast.LENGTH_SHORT).show();
    }

    private void callSignup(String email, String password) {
        Log.e("callsignup","email :"+email);
        Log.e("callsugnup","password :"+password);
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
                            Toast.makeText(SignUpActivity.this, "Account Created",
                                    Toast.LENGTH_SHORT).show();
                            Log.d("Test","Acoount Created");
                        }

                        // ...
                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    callGallery();
                return;
        }
        Toast.makeText(getApplicationContext(), "....", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){
            mImageUri = data.getData();
            selectedImage.setImageURI(mImageUri);
            StorageReference filepath = mStorage.child("User_Images").child(mImageUri.getLastPathSegment());

            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();
            Log.e("Test","Filepath :"+filepath.toString());
//            Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
//            StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
            UploadTask uploadTask = filepath.putFile(mImageUri);
            Log.e("Test","Filepath2 :"+uploadTask.isInProgress());


            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(),"Failure",Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    mDatabaseRef.child("Image_URL").setValue(downloadUri.toString());
                    Log.e("Test","OnSuccess");
                    Glide.with(getApplicationContext())
                            .load(downloadUri)
                            .crossFade()
                            .placeholder(R.drawable.loading)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(selectedImage);
                    Toast.makeText(getApplicationContext(),"Updated...",Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });
        }

    }

    private void userProfile(){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName((emailSignup.getText().toString())).build();
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d("test","user profile updates");
                    }
                }
            });
        }
    }


}
