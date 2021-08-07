package com.yuckyh.eldritchmusic.models;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public abstract class Model {
    protected static final String TAG = Model.class.getSimpleName();

    public Model() {
    }

    public abstract String getId();

    public abstract void setId(String id);

    public abstract void setObjectsFromRefs();

    public abstract void setRefsFromObjects();

    protected <T> void setRefArrayToObjArray(ArrayList<DocumentReference> references, ArrayList<T> tArrayList) {
        for (int i = 0; i < tArrayList.size(); i++) {
            DocumentReference reference = references.get(i);
            reference.set(tArrayList.get(i));
        }
    }
}
