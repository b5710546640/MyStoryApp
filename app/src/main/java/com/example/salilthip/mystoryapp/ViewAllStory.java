package com.example.salilthip.mystoryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

            toolbar.setNavigationIcon(R.drawable.arrow_back_m);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), UserMainActivity.class));
                }
            });


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
                    public void onClick(final View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewAllStory.this);
                        builder.setMessage("Delete or View").setCancelable(false)
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int selectedItems = position;
                                        mFirebaseAdapter.getRef(selectedItems).removeValue();
                                        mFirebaseAdapter.notifyItemRemoved(selectedItems);
                                        recyclerView.invalidate();
                                        onStart();

//                                        RecyclerView.ViewHolder story = recyclerView.getRecycledViewPool().getRecycledView(position);
                                        //////////Show the story
                                    }
                                })
                                .setNegativeButton("View", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int selectedItems = position;
                                        Intent intent = new Intent(ViewAllStory.this, ViewStoryActivity.class);
                                        intent.putExtra("selectedPost",mFirebaseAdapter.getRef(selectedItems).getKey()+"");
                                        startActivity(intent);
                                        Log.e("Test","Position"+selectedItems);
                                        finish();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.setTitle("What do you do?");
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

