package com.yuckyh.eldritchmusic.models;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class Artiste extends Model {
    private String mId, mName, mProfileUrl;
    private final ArrayList<Album> mAlbums = new ArrayList<>();

    public Artiste() {
        super("albums");
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    @Override
    public void appSetObjectsFromRefs() {

    }

    @Override
    public void appSetRefsFromObjects() {

    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getProfileUrl() {
        return mProfileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        mProfileUrl = profileUrl;
    }

    public ArrayList<Album> appGetAlbums() {
        return mAlbums;
    }

    public ArrayList<Song> appGetSongs() {
        ArrayList<Song> songs = new ArrayList<>();
        for (Album album : appGetAlbums()) {
            songs.addAll(album.appGetSongs());
        }
        return songs;
    }

    public void addAlbum(Album album) {
        mAlbums.add(album);
    }
}
