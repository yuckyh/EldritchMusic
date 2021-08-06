package com.yuckyh.eldritchmusic.models;

public abstract class Model {
    protected static final String TAG = Model.class.getSimpleName();
    public Model() {}
    public abstract String getId();
    public abstract void setId(String id);
}
