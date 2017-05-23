package com.example.salilthip.mystoryapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewAllStory extends AppCompatActivity {

    RecyclerView recyclerView;
    private DatabaseReference mDatabaseRef;
    private FirebaseRecyclerAdapter<ViewSingleStory,ShowDataViewHolder> mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_story);
        Firebase.setAndroidContext(this);
        recyclerView = (RecyclerView)findViewById(R.id.storyListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewAllStory.this));
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User_post");

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ViewSingleStory, ViewAllStory.ShowDataViewHolder>(ViewSingleStory.class,R.layout.view_single_story,ViewAllStory.ShowDataViewHolder.class,mDatabaseRef) {
            @Override
            protected void populateViewHolder(final ViewAllStory.ShowDataViewHolder viewHolder, ViewSingleStory model, final int position) {
                viewHolder.Story_Title(model.getTitle());
                viewHolder.Story_Detail(model.getDetail());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewAllStory.this);
                        builder.setMessage("View").setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int selectedItems = position;
                                        //////////Show the story
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.setTitle("Are you sure?");
                        dialog.show();
                    }
                });

            }
        };
        recyclerView.setAdapter(mFirebaseAdapter);
    }


    public static class ShowDataViewHolder extends RecyclerView.ViewHolder{
        private final TextView story_title;
        private final TextView story_detail;

        public ShowDataViewHolder(final View itemView){
            super(itemView);
            story_title = (TextView)itemView.findViewById(R.id.story_title);
            story_detail = (TextView)itemView.findViewById(R.id.story_intro);
        }

        private void Story_Title(String title){
            story_title.setText(title);
        }

        private  void Story_Detail(String detail){
            story_detail.setText(detail);
        }
    }


}

