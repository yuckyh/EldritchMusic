package com.yuckyh.eldritchmusic.registries;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.yuckyh.eldritchmusic.models.Model;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Registry<T extends Model> {
    protected static final String TAG = Registry.class.getSimpleName();
    public final FirebaseFirestore DB = FirebaseFirestore.getInstance();
    protected final ArrayList<T> mList = new ArrayList<>();
    protected OnSyncListener mOnSyncListener;

    public T fromId(String id) throws Exception {
        for (T val : this.mList) {
            if (val.getId().equals(id)) {
                return val;
            }
        }
        throw new Exception("Item with id " + id + " not found");
    }

    public void syncFromDb(String collectionPath, Class<T> tClass) {
        DB.collection(collectionPath).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    T t;
                    t = document.toObject(tClass);
                    addToList(t);
                }
                if (mOnSyncListener != null) {
                    mOnSyncListener.onDataSync();
                }
                Log.d(TAG, "syncFromDb: " + collectionPath);
            } else {
                Log.e(TAG, "onComplete: ", task.getException());
            }
        });
    }

    public void addToList(T t) {
        Log.d(TAG, "Item added to list " + t.getId());
        mList.add(t);
    }

    public ArrayList<T> getList() {
        return this.mList;
    }

    public void setSyncListener(OnSyncListener onSyncListener) {
        mOnSyncListener = onSyncListener;
    }

    public T refToObject(DocumentReference reference) {
        try {
            return this.fromId(reference.getId());
        } catch (Exception e) {
            Log.e(TAG, "refToObject: ", e);
            return null;
        }
    }

    public ArrayList<T> refListToObjectList(ArrayList<DocumentReference> references) {
        ArrayList<T> tArrayList = new ArrayList<>();
        for (DocumentReference id : references) {
            try {
                tArrayList.add(this.fromId(id.getId()));
            } catch (Exception e) {
                Log.e(TAG, "setFollowedArtisteIds: ", e);
            }
        }

        return tArrayList;
    }

    public interface OnSyncListener {
        void onDataSync();
    }
}
