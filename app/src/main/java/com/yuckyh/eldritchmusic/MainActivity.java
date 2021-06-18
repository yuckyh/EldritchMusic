package com.yuckyh.eldritchmusic;

import android.os.Bundle;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.yuckyh.eldritchmusic.activities.HomeActivity;
import com.yuckyh.eldritchmusic.registries.AlbumRegistry;
import com.yuckyh.eldritchmusic.registries.ArtisteRegistry;
import com.yuckyh.eldritchmusic.registries.SongRegistry;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isFirst) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }
        SongRegistry.getInstance().setSyncListener(() -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            isFirst = false;
        });
    }
}
