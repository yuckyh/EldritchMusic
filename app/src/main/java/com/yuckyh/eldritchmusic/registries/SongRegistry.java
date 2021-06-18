package com.yuckyh.eldritchmusic.registries;

import com.yuckyh.eldritchmusic.models.Song;

public class SongRegistry extends Registry<Song> {
    private static final SongRegistry INSTANCE = new SongRegistry();

    protected SongRegistry() {
    }

    public static SongRegistry getInstance() {
        return INSTANCE;
    }

    public void syncFromDb() {
        super.syncFromDb("songs", Song.class);
    }
}
