package com.yuckyh.eldritchmusic.registries;

import com.yuckyh.eldritchmusic.models.Playlist;

public class PlaylistRegistry extends Registry<Playlist> {
    private static final PlaylistRegistry INSTANCE = new PlaylistRegistry();

    public static PlaylistRegistry getInstance() {
        return INSTANCE;
    }

    public PlaylistRegistry syncFromDb() {
        super.syncFromDb("playlists", Playlist.class);
        return INSTANCE;
    }

    public void addToOwners() {
        for (Playlist playlist : mList) {
            playlist.addToOwner();
        }
    }
}
