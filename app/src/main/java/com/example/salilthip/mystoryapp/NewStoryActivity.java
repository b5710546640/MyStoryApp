package com.example.salilthip.mystoryapp;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.salilthip.mystoryapp.R;

public class NewStoryActivity extends AppCompatActivity {

    private FloatingActionButton saveStory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_story);

        saveStory = (FloatingActionButton)findViewById(R.id.saveStory);

        saveStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
