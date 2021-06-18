package com.yuckyh.eldritchmusic.models;

import android.os.Parcelable;

public abstract class Model implements Parcelable {
    protected String mId;

    protected Model() {
    }

    protected Model(String id) {
        mId = id;
    }

    public abstract String getId();
}
