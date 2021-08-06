package com.yuckyh.eldritchmusic.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.yuckyh.eldritchmusic.R;

public class AddToPlaylistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_playlist);
        Intent intent =  getIntent();
        String id = intent.getStringExtra("id");
        
        findViewById(R.id.fabCreatePlaylist).setOnClickListener(v -> {
//            startActivity();
        });
    }
}
