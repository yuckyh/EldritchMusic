package com.yuckyh.eldritchmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.yuckyh.eldritchmusic.activities.HomeActivity;
import com.yuckyh.eldritchmusic.registries.AlbumRegistry;
import com.yuckyh.eldritchmusic.registries.PlaylistRegistry;
import com.yuckyh.eldritchmusic.registries.SongRegistry;
import com.yuckyh.eldritchmusic.registries.UserRegistry;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static boolean isFirstRun = true;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        UserRegistry.getInstance().setSyncListener(() -> {
            SongRegistry.getInstance().addToAlbums();
            AlbumRegistry.getInstance().addToArtiste();
            PlaylistRegistry.getInstance().addToOwners();
            isFirstRun = false;
            startHomeActivity();
        });

        if (!isFirstRun) {
            startHomeActivity();
        }
    }

    private void startHomeActivity() {
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }
}
