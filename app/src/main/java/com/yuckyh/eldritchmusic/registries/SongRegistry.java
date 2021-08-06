package com.yuckyh.eldritchmusic.registries;

import com.yuckyh.eldritchmusic.models.Song;

public class SongRegistry extends Registry<Song> {
    private static final SongRegistry INSTANCE = new SongRegistry();

    public static SongRegistry getInstance() {
        return INSTANCE;
    }

    public SongRegistry syncFromDb() {
        super.syncFromDb("songs", Song.class);
        return INSTANCE;
    }

    public void addToAlbums() {
        for (Song song : mList) {
            song.addToAlbum();
        }
    }
}
