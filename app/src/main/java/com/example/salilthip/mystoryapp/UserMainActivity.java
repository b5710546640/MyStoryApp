package com.example.salilthip.mystoryapp;

import android.*;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.example.salilthip.mystoryapp.SignUpActivity.READ_EXTERNAL_STORAGE;

public class UserMainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    private static final int GALLERY_INTENT = 2;
    private Uri mImageUri = null;
    private TextView emailProfile;
    private EditText displayname;
    private Button signout,updateProfile;
    private ImageButton profileImage;

    private ProgressDialog mProgressDialog;
    private Firebase mRootRef;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        mAuth = FirebaseAuth.getInstance();
        Firebase.setAndroidContext(this);

        emailProfile = (TextView)findViewById(R.id.emailProfileTxt);
        signout = (Button)findViewById(R.id.signoutBtn);
        updateProfile = (Button)findViewById(R.id.updateBtn);
        profileImage = (ImageButton)findViewById(R.id.profileImageBtn);
        displayname = (EditText)findViewById(R.id.displayNameTxt);

        displayname.setText(mAuth.getCurrentUser().getDisplayName().toString());

        mProgressDialog = new ProgressDialog(UserMainActivity.this);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mystoryapp-2e9ec.appspot.com/");

        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        profileImage.setImageURI(mAuth.getCurrentUser().getPhotoUrl());
        emailProfile.setText(mAuth.getCurrentUser().getEmail());
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForImage();
            }
        });
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userUpdateProfile(displayname.getText().toString(),mImageUri.toString());
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSignout();
            }
        });

    }

    private void askForImage(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Call for permission", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("DataonActivityResult", "check data :"+data+"request code : "+requestCode);
        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK) {
            mImageUri = data.getData();
            Log.d("mimageUri", "check :" + mImageUri.toString());
            profileImage.setImageURI(mImageUri);
            StorageReference filepath = mStorage.child("User_Images").child(mImageUri.getLastPathSegment());
            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Log.d("Success", "putFile:onSuccess");
                    mDatabaseRef.child("Image_URL").setValue(downloadUri.toString());
                    Glide.with(getApplicationContext())
                            .load(downloadUri)
                            .crossFade()
                            .placeholder(R.drawable.loading)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(profileImage);
                    Toast.makeText(getApplicationContext(), "Updated...", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });
        }
    }

    public void getUserProfile(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }
    }

    public void userUpdateProfile(String displayname,String uri){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayname)
                .setPhotoUri(Uri.parse(uri))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Update", "User profile updated.");
                        }
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
