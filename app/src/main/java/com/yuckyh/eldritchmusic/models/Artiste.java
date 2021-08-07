package com.yuckyh.eldritchmusic.models;

import java.util.ArrayList;

public class Artiste extends Model {
    private String mId, mName, mProfileUrl;
    private final ArrayList<Album> mAlbums = new ArrayList<>();

    public Artiste() {
        super();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    @Override
    public void setObjectsFromRefs() {

    }

    @Override
    public void setRefsFromObjects() {

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

    public void addAlbum(Album album) {
        mAlbums.add(album);
    }
}
