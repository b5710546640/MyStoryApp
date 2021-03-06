package com.example.salilthip.mystoryapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

public class UserMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;



    private View navProfile,navStory,navSignOut;

    FloatingActionButton newStory;



    private static final int GALLERY_INTENT = 2;
    private Uri mImageUri = null;
    private TextView emailProfile;
    private EditText displayname;
    private Button updateProfile;
    private ImageButton profileImage;

//    //Navigation
//    private ImageView navImage;
//    private TextView navName,navEmail;

    private ProgressDialog mProgressDialog;
    private Firebase mRootRef;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorage;
    public InputMethodManager imm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.salilthip.mystoryapp.R.layout.activity_user_main);

        mAuth = FirebaseAuth.getInstance();
        Firebase.setAndroidContext(this);

        emailProfile = (TextView)findViewById(R.id.emailProfileTxt);
        updateProfile = (Button)findViewById(R.id.updateBtn);
        profileImage = (ImageButton)findViewById(R.id.profileImageBtn);
        displayname = (EditText)findViewById(R.id.displayNameTxt);

        newStory = (FloatingActionButton)findViewById(R.id.new_story);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);


        Log.e("Test","UserMainAct");

//        navEmail = (TextView)findViewById(R.id.emailNav);
//        navImage = (ImageView)findViewById(R.id.imageProfileNav);
//        navName = (TextView)findViewById(R.id.displayNameNav);

        mProgressDialog = new ProgressDialog(UserMainActivity.this);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mystoryapp-2e9ec.appspot.com/");
        Log.e("test","Before check mAuth");
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        String email = mAuth.getCurrentUser().getEmail();
        String nameDisplay = mAuth.getCurrentUser().getDisplayName();
        Uri imageUri = mAuth.getCurrentUser().getPhotoUrl();
        Log.e("TEST","1"+email);
        Log.e("TEST","2"+nameDisplay);
        Log.e("TEST","3"+imageUri);
        //Display profile
        if (imageUri!=null){
            profileImage.setImageURI(imageUri);
//            navImage.setImageURI(imageUri);
        }
        emailProfile.setText(email);
//        navEmail.setText(email);
        if (nameDisplay!=null){
            displayname.setText(nameDisplay);
//            navName.setText(nameDisplay);
        }

        newStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Success","Add new story");
                startActivity(new Intent(getApplicationContext(), NewStoryActivity.class));
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForImage();
            }
        });
        updateProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                userUpdateProfile(displayname.getText().toString(),mImageUri.toString());
            }
        });

    }




    private void askForImage(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Call for permission", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, SignUpActivity.READ_EXTERNAL_STORAGE);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {

            LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_container);
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.content_sign_in, null);
            mainLayout.removeAllViews();
            mainLayout.addView(layout);

        } else if (id == R.id.nav_story) {
            finish();
            startActivity(new Intent(getApplicationContext(), ViewAllStory.class));
//            LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_container);
//            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View layout = inflater.inflate(R.layout.content_story_list, null);
//            mainLayout.removeAllViews();
//            mainLayout.addView(layout);

        } else if (id == R.id.nav_signout) {
            callSignout();
            Log.e("Success","Call Signout");

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}