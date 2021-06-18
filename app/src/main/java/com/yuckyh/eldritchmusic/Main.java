package com.yuckyh.eldritchmusic;

import android.app.Application;
import com.yuckyh.eldritchmusic.registries.AlbumRegistry;
import com.yuckyh.eldritchmusic.registries.ArtisteRegistry;
import com.yuckyh.eldritchmusic.registries.SongRegistry;

public class Main extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ArtisteRegistry.getInstance().syncFromDb();
        AlbumRegistry.getInstance().syncFromDb();
        SongRegistry.getInstance().syncFromDb();
    }
}
