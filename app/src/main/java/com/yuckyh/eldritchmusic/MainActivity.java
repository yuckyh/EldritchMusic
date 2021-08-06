package com.yuckyh.eldritchmusic;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.yuckyh.eldritchmusic.activities.HomeActivity;
import com.yuckyh.eldritchmusic.models.User;
import com.yuckyh.eldritchmusic.registries.AlbumRegistry;
import com.yuckyh.eldritchmusic.registries.PlaylistRegistry;
import com.yuckyh.eldritchmusic.registries.SongRegistry;
import com.yuckyh.eldritchmusic.registries.UserRegistry;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static boolean isFirstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance());

        Objects.requireNonNull(getSupportActionBar()).hide();

        if(!isFirstRun) {
            onLoadComplete();
            return;
        }

        UserRegistry.getInstance().setSyncListener(() -> {
            SongRegistry.getInstance().addToAlbums();
            AlbumRegistry.getInstance().addToArtiste();
            PlaylistRegistry.getInstance().addToOwners();
            onLoadComplete();
            isFirstRun = false;
        });
    }

    private void onLoadComplete() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
