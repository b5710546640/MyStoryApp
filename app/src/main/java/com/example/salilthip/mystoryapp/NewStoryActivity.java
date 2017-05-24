package com.example.salilthip.mystoryapp;

import android.content.Context;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.salilthip.mystoryapp.R;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NewStoryActivity extends AppCompatActivity {

    private EditText title,detail;

    private FloatingActionButton saveStory;

    FirebaseAuth mAuth;
    private Firebase mRootRef;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorage;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_story);

        title = (EditText)findViewById(R.id.title_input);
        detail = (EditText)findViewById(R.id.detail_input);
        saveStory = (FloatingActionButton)findViewById(R.id.saveStory);
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User_post").push();
        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mystoryapp-2e9ec.appspot.com/");

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        saveStory.setOnClickListener(new View.OnClickListener() {
            public InputMethodManager imm;

            @Override
            public void onClick(View v) {
//                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                String mTitle = title.getText().toString();
                String mDetail = detail.getText().toString();
                if (mTitle.isEmpty() && mDetail.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Failure Save, please check your title or detail",Toast.LENGTH_SHORT).show();
                }
                DatabaseReference post_ref = mDatabaseRef;
                post_ref.child("title").setValue(mTitle);
                post_ref.child("detail").setValue(mDetail);
                post_ref.child("Timestamp").setValue(ServerValue.TIMESTAMP);
                post_ref.child("writer").setValue(mAuth.getCurrentUser().getUid());
                finish();

            }
        });
    }
}
