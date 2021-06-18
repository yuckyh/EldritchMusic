package com.yuckyh.eldritchmusic.registries;

import com.yuckyh.eldritchmusic.models.Album;

public class AlbumRegistry extends Registry<Album> {
    private static final AlbumRegistry INSTANCE = new AlbumRegistry();

    protected AlbumRegistry() {
    }

    public static AlbumRegistry getInstance() {
        return INSTANCE;
    }

    public void syncFromDb() {
        super.syncFromDb("albums", Album.class);
    }
}
