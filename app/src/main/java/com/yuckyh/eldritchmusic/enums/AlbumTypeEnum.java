package com.yuckyh.eldritchmusic.enums;

import android.os.Parcel;
import android.os.Parcelable;

public enum AlbumTypeEnum implements Parcelable {
    SINGLE, EP, STUDIO, REPACKAGE;

    AlbumTypeEnum() {
    }

    public static final Creator<AlbumTypeEnum> CREATOR = new Creator<AlbumTypeEnum>() {
        @Override
        public AlbumTypeEnum createFromParcel(Parcel in) {
            return AlbumTypeEnum.valueOf(in.readString());
        }

        @Override
        public AlbumTypeEnum[] newArray(int size) {
            return new AlbumTypeEnum[size];
        }
    };

    @Override
    public int describeContents() {
        return CONTENTS_FILE_DESCRIPTOR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name());
    }
}
