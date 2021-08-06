package com.yuckyh.eldritchmusic.models;

import com.google.firebase.firestore.DocumentReference;
import com.yuckyh.eldritchmusic.enums.AlbumTypeEnum;
import com.yuckyh.eldritchmusic.registries.ArtisteRegistry;

import java.util.ArrayList;

public class Album extends Model {
    private String mId, mName, mAlbumArtUrl;
    private int mYear;
    private AlbumTypeEnum mType;
    private DocumentReference mArtisteId;
    private Artiste mArtiste;
    private final ArrayList<Song> mSongs = new ArrayList<>();

    public Album() {
        super();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
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

    public Artiste getArtiste() {
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
        setArtiste(ArtisteRegistry.getInstance().refToObject(artisteId));
    }

    public void addSong(Song song) {
        mSongs.add(song);
    }

    public void addToArtiste() {
        mArtiste.addAlbum(this);
    }
}
