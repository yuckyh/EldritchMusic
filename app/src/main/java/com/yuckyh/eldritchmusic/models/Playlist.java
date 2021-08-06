package com.yuckyh.eldritchmusic.models;

import com.google.firebase.firestore.DocumentReference;
import com.yuckyh.eldritchmusic.registries.SongRegistry;
import com.yuckyh.eldritchmusic.registries.UserRegistry;

import java.util.ArrayList;

public class Playlist extends Model {
    private String mId, mName, mPlaylistArtUrl;
    private User mOwner;
    private DocumentReference mOwnerId;
    private ArrayList<Song> mSongs;
    private ArrayList<DocumentReference> mSongIds;
    private double mSongsTotalDuration;

    public Playlist() {
        super();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public User getOwner() {
        return mOwner;
    }

    public void setOwner(User owner) {
        mOwner = owner;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public ArrayList<Song> getSongs() {
        return mSongs;
    }

    public void setSongs(ArrayList<Song> songs) {
        mSongs = songs;
        mSongsTotalDuration = 0;
        mPlaylistArtUrl = mSongs.get(0).getAlbum().getAlbumArtUrl();
        for (Song song: mSongs) {
            mSongsTotalDuration += song.getDuration();
        }
    }

    public ArrayList<DocumentReference> getSongIds() {
        return mSongIds;
    }

    public void setSongIds(ArrayList<DocumentReference> songIds) {
        mSongIds = songIds;
        setSongs(SongRegistry.getInstance().refListToObjectList(songIds));
    }

    public DocumentReference getOwnerId() {
        return mOwnerId;
    }

    public void setOwnerId(DocumentReference ownerId) {
        mOwnerId = ownerId;
    }

    public void addToOwner() {
        setOwner(UserRegistry.getInstance().refToObject(mOwnerId));
        mOwner.addPlaylist(this);
    }

    public double getSongsTotalDuration() {
        return mSongsTotalDuration;
    }

    public String getPlaylistArtUrl() {
        return mPlaylistArtUrl;
    }
}
