package com.yuckyh.eldritchmusic.registries;

import com.yuckyh.eldritchmusic.models.Album;

public class AlbumRegistry extends Registry<Album> {
    private static final AlbumRegistry INSTANCE = new AlbumRegistry();

    public static AlbumRegistry getInstance() {
        return INSTANCE;
    }

    public AlbumRegistry syncFromDb() {
        super.syncFromDb("albums", Album.class);
        return INSTANCE;
    }

    public void addToArtiste() {
        for (Album album : mList) {
            album.addToArtiste();
        }
    }
}
