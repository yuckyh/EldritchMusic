package com.yuckyh.eldritchmusic.registries;

import com.yuckyh.eldritchmusic.models.Playlist;

public class PlaylistRegistry extends Registry<Playlist> {
    private static final PlaylistRegistry INSTANCE = new PlaylistRegistry();

    protected PlaylistRegistry() {
        super("playlists");
    }

    public static PlaylistRegistry getInstance() {
        return INSTANCE;
    }

    public void addToOwners() {
        for (Playlist playlist : mList) {
            playlist.addToOwner();
        }
    }
}
