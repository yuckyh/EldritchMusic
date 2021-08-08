package com.yuckyh.eldritchmusic.models;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.yuckyh.eldritchmusic.registries.PlaylistRegistry;
import com.yuckyh.eldritchmusic.registries.Registry;
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
        super("playlists");
    }

    public Playlist(String id, String name, User user, String songId) {
        this();
        mId = id;
        mName = name;
        mOwner = user;
        mOwnerId = DB.collection(mCollectionPath).document(mOwner.getId());

        if (songId == null) {
            return;
        }

        ArrayList<Song> songs = new ArrayList<>();
        try {
            Song song = SongRegistry.getInstance().itemFromId(songId);
            songs.add(song);
        } catch (Exception e) {
            Log.e(TAG, "Playlist: ", e);
        }
        setSongs(songs);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    @Override
    public void appSetObjectsFromRefs() {
        setSongs(SongRegistry.getInstance().refListToObjectList(mSongIds));
    }

    @Override
    public void appSetRefsFromObjects() {
        setOwner(UserRegistry.getInstance().refToObject(mOwnerId));
        setSongIds(SongRegistry.getInstance().objectListToRefList(mSongs));
    }

    public User appGetOwner() {
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

    public ArrayList<Song> appGetSongs() {
        return mSongs;
    }

    public void setSongs(ArrayList<Song> songs) {
        mSongs = songs;
        mSongsTotalDuration = 0;
        for (Song song : mSongs) {
            mSongsTotalDuration += song.getDuration();
        }
        try {
            mPlaylistArtUrl = mSongs.get(0).appGetAlbum().getAlbumArtUrl();
        } catch (Exception e) {
            Log.e(TAG, "setSongs: ", e);
        }
    }

    public ArrayList<DocumentReference> getSongIds() {
        return mSongIds;
    }

    public void setSongIds(ArrayList<DocumentReference> songIds) {
        mSongIds = songIds;
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

    public void addSong(Song song) {
        mSongs.add(song);
    }

    public double appGetSongsTotalDuration() {
        return mSongsTotalDuration;
    }

    public String getPlaylistArtUrl() {
        return mPlaylistArtUrl;
    }
}
