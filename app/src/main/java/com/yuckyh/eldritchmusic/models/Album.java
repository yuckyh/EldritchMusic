package com.yuckyh.eldritchmusic.models;

import com.google.firebase.firestore.DocumentReference;
import com.yuckyh.eldritchmusic.enums.AlbumTypeEnum;
import com.yuckyh.eldritchmusic.registries.ArtisteRegistry;

import java.util.ArrayList;

public class Album extends Model {
    private String mId, mName, mAlbumArtUrl;
    private int mYear;
    private double mSongsTotalDuration;
    private AlbumTypeEnum mType;
    private DocumentReference mArtisteId;
    private Artiste mArtiste;
    private final ArrayList<Song> mSongs = new ArrayList<>();

    public Album() {
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
        setArtiste(ArtisteRegistry.getInstance().refToObject(mArtisteId));
    }

    @Override
    public void appSetRefsFromObjects() {
        mArtisteId.set(mArtiste);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public AlbumTypeEnum getType() {
        return mType;
    }

    public void setType(AlbumTypeEnum type) {
        mType = type;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
    }

    public Artiste appGetArtiste() {
        return mArtiste;
    }

    public void setArtiste(Artiste artiste) {
        mArtiste = artiste;
    }

    public String getAlbumArtUrl() {
        return mAlbumArtUrl;
    }

    public void setAlbumArtUrl(String albumArtUrl) {
        mAlbumArtUrl = albumArtUrl;
    }

    public DocumentReference getArtisteId() {
        return mArtisteId;
    }

    public void setArtisteId(DocumentReference artisteId) {
        mArtisteId = artisteId;
    }

    public void addSong(Song song) {
        mSongs.add(song);
        mSongsTotalDuration = 0;
        for (Song song1 : mSongs) {
            mSongsTotalDuration += song1.getDuration();
        }
    }

    public void addToArtiste() {
        mArtiste.addAlbum(this);
    }

    public ArrayList<Song> appGetSongs() {
        return mSongs;
    }

    public double appGetSongsTotalDuration() {
        return mSongsTotalDuration;
    }
}
