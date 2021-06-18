package com.yuckyh.eldritchmusic.models;

import android.os.Parcel;

import java.util.ArrayList;

public class Artiste extends Model {
    public static final Creator<Artiste> CREATOR = new Creator<Artiste>() {
        @Override
        public Artiste createFromParcel(Parcel in) {
            return new Artiste(in);
        }

        @Override
        public Artiste[] newArray(int size) {
            return new Artiste[size];
        }
    };
    private final ArrayList<Album> mAlbums = new ArrayList<>();
    private String mId, mName, mProfileLink;

    public Artiste() {
        super();
    }

    public Artiste(String id, String name, String profileLink) {
        super(id);
        this.mName = name;
        this.mProfileLink = profileLink;
    }

    protected Artiste(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        mProfileLink = in.readString();
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getProfileLink() {
        return mProfileLink;
    }

    public void setProfileLink(String mProfileLink) {
        this.mProfileLink = mProfileLink;
    }

    public void addAlbum(Album album) {
        this.mAlbums.add(album);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mProfileLink);
        dest.writeTypedList(mAlbums);
    }
}
