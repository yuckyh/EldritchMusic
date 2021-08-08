package com.yuckyh.eldritchmusic.models;

import com.google.firebase.firestore.DocumentReference;
import com.yuckyh.eldritchmusic.registries.AlbumRegistry;

public class Song extends Model {
    private String mId, mName;
    private double mDuration;
    private Album mAlbum;
    private DocumentReference mAlbumId;

    public Song() {
        super("songs");
    }

    public String getId() {
        return this.mId;
    }

    public void setId(String id) {
        mId = id;
    }

    @Override
    public void appSetObjectsFromRefs() {
        setAlbum(AlbumRegistry.getInstance().refToObject(mAlbumId));
    }

    @Override
    public void appSetRefsFromObjects() {
        mAlbumId.set(mAlbum);
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public double getDuration() {
        return this.mDuration;
    }

    public void setDuration(double duration) {
        mDuration = duration;
    }

    public Album appGetAlbum() {
        return this.mAlbum;
    }

    public void setAlbum(Album album) {
        mAlbum = album;
    }

    public DocumentReference getAlbumId() {
        return mAlbumId;
    }

    public void setAlbumId(DocumentReference albumId) {
        mAlbumId = albumId;
    }

    public void addToAlbum() {
        if (mAlbum == null) {
            return;
        }
        mAlbum.addSong(this);
    }
}
