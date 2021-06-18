package com.yuckyh.eldritchmusic.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.yuckyh.eldritchmusic.R;

public class SongPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.activity_slide_down);
    }
}
