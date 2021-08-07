package com.yuckyh.eldritchmusic;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.yuckyh.eldritchmusic.registries.AlbumRegistry;
import com.yuckyh.eldritchmusic.registries.ArtisteRegistry;
import com.yuckyh.eldritchmusic.registries.PlaylistRegistry;
import com.yuckyh.eldritchmusic.registries.SongRegistry;
import com.yuckyh.eldritchmusic.registries.UserRegistry;

public class EldritchMusic extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance());
        ArtisteRegistry artisteRegistry = ArtisteRegistry.getInstance();
        AlbumRegistry albumRegistry = AlbumRegistry.getInstance();
        SongRegistry songRegistry = SongRegistry.getInstance();
        PlaylistRegistry playlistRegistry = PlaylistRegistry.getInstance();
        UserRegistry userRegistry = UserRegistry.getInstance();

        artisteRegistry.setSyncListener(() -> albumRegistry.readFromDb());
        albumRegistry.setSyncListener(() -> songRegistry.readFromDb());
        songRegistry.setSyncListener(() -> playlistRegistry.readFromDb());
        playlistRegistry.setSyncListener(() -> userRegistry.readFromDb());
        artisteRegistry.readFromDb();
    }
}
