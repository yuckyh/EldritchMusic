package com.yuckyh.eldritchmusic.registries;

import com.yuckyh.eldritchmusic.models.Song;

public class SongRegistry extends Registry<Song> {
    private static final SongRegistry INSTANCE = new SongRegistry();

    protected SongRegistry() {
        super("songs");
    }

    public static SongRegistry getInstance() {
        return INSTANCE;
    }

    public void addToAlbums() {
        for (Song song : mList) {
            song.addToAlbum();
        }
    }
}
