package com.example.salilthip.mystoryapp;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ViewStoryActivity extends AppCompatActivity {

    private EditText titleEdt,detailEdt;
    private FloatingActionButton updateStory;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);

        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("selectedPost");

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();


        mDatabaseRef.child("User_post").child(message).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ViewSingleStory story = dataSnapshot.getValue(ViewSingleStory.class);
                        String title = story.title; // "John Doe"
                        String detail = story.detail; // "Texas"
                        titleEdt.setText(title);
                        detailEdt.setText(detail);
                        Log.e("Titlw",title);
                        Log.e("Detail",detail);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
}
