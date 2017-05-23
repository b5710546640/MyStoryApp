package com.example.salilthip.mystoryapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class ViewStoryActivity extends AppCompatActivity {

    private EditText titleEdt,detailEdt;
    private FloatingActionButton updateStory;
    private DatabaseReference mDatabaseRef;
    String refPost;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);

        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        titleEdt = (EditText)findViewById(R.id.title_edit);
        detailEdt = (EditText)findViewById(R.id.detail_edit);
        updateStory = (FloatingActionButton)findViewById(R.id.updateStory);

//        titleEdt.setFocusable(false);
//        titleEdt.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
//        titleEdt.setClickable(false);
//
//        detailEdt.setFocusable(false);
//        detailEdt.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
//        detailEdt.setClickable(false);

        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("selectedPost");
        refPost = message;

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

        updateStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                toggleEditable();
//                if (titleEdt!=null&&detailEdt!=null)
//                    try {
                DatabaseReference ref = mDatabaseRef.child("User_post");
                Log.e("Test", ref.child(refPost).child("title")+"");
                        ref.child(refPost).child("title").setValue(titleEdt.getText()+"");
                        ref.child(refPost).child("detail").setValue(detailEdt.getText()+"");
                        ref.child(refPost).child("writer").setValue(mAuth.getCurrentUser().getUid()+"");
                        ref.child(refPost).child("Timestamp").setValue(ServerValue.TIMESTAMP+"");
                finish();
                startActivity(new Intent(getApplicationContext(), UserMainActivity.class));
//                        toggleEditable();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }



            }
        });

    }

    private void toggleEditable(){
            if (titleEdt.isClickable() && detailEdt.isClickable()){
                titleEdt.setFocusable(false);
                titleEdt.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                titleEdt.setClickable(false);

                detailEdt.setFocusable(false);
                detailEdt.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                detailEdt.setClickable(false);
            }else{
                titleEdt.setFocusable(true);
                titleEdt.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
                titleEdt.setClickable(true);

                detailEdt.setFocusable(true);
                detailEdt.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
                detailEdt.setClickable(true);
            }

    }
}
