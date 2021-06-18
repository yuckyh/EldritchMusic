package com.yuckyh.eldritchmusic.models;

import android.os.Parcel;

public class Song extends Model {
    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
    private String mId, mName, mFilePath;
    private double mDuration;
    private Album mAlbum;

    public Song() {
        super();
    }

    public Song(String id, String name, double duration, Album album, String filePath) {
        super(id);
        mName = name;
        mFilePath = filePath;
        mAlbum = album;
        mDuration = duration;
        mAlbum.addSong(this);
    }

    protected Song(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        mFilePath = in.readString();
        mDuration = in.readDouble();
    }

    public String getId() {
        return this.mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getFilePath() {
        return this.mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public double getDuration() {
        return this.mDuration;
    }

    public void setDuration(double duration) {
        mDuration = duration;
    }

    public Album getAlbum() {
        return this.mAlbum;
    }

    public void setAlbum(Album album) {
        mAlbum = album;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mFilePath);
        dest.writeDouble(mDuration);
        dest.writeParcelable(mAlbum, flags);
    }
}
