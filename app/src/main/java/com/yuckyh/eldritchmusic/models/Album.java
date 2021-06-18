package com.yuckyh.eldritchmusic.models;

import android.os.Parcel;

import com.yuckyh.eldritchmusic.enums.AlbumTypeEnum;

import java.util.ArrayList;

public class Album extends Model {
    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
    private String mName, mAlbumArtPath;
    private int mYear;
    private AlbumTypeEnum mType;
    private Artiste mArtiste;
    private ArrayList<Song> mSongs = new ArrayList<>();

    public Album() {
        super();
    }

    public Album(String id, String title, AlbumTypeEnum type, int year, String mAlbumArtPath, Artiste artiste) {
        super(id);
        this.mName = title;
        this.mType = type;
        this.mYear = year;
        this.mArtiste = artiste;
        this.mAlbumArtPath = mAlbumArtPath;

        this.mArtiste.addAlbum(this);
    }

    protected Album(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        mAlbumArtPath = in.readString();
        mYear = in.readInt();
        mArtiste = in.readParcelable(Artiste.class.getClassLoader());
        mSongs = in.createTypedArrayList(Song.CREATOR);
    }

    public String getId() {
        return this.mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public AlbumTypeEnum getType() {
        return this.mType;
    }

    public void setType(AlbumTypeEnum mType) {
        this.mType = mType;
    }

    public int getYear() {
        return this.mYear;
    }

    public void setYear(int mYear) {
        this.mYear = mYear;
    }

    public Artiste getArtiste() {
        return this.mArtiste;
    }

    public void setArtiste(Artiste mArtiste) {
        this.mArtiste = mArtiste;
    }

    public String getAlbumArtPath() {
        return this.mAlbumArtPath;
    }

    public void setAlbumArtPath(String mAlbumArtPath) {
        this.mAlbumArtPath = mAlbumArtPath;
    }

    public void addSong(Song song) {
        mSongs.add(song);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mAlbumArtPath);
        dest.writeInt(mYear);
        dest.writeParcelable(mArtiste, flags);
        dest.writeTypedList(mSongs);
    }
}
