package com.yuckyh.eldritchmusic.models;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public abstract class Model {
    protected static final String TAG = Model.class.getSimpleName();
    protected final FirebaseFirestore DB = FirebaseFirestore.getInstance();
    protected final String mCollectionPath;

    public Model(String collectionPath) {
        mCollectionPath = collectionPath;
    }

    public abstract String getId();

    public abstract void setId(String id);

    public abstract void appSetObjectsFromRefs();

    public abstract void appSetRefsFromObjects();
}
