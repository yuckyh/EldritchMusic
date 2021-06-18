package com.yuckyh.eldritchmusic.registries;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.yuckyh.eldritchmusic.models.Model;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Registry<T extends Model> {
    protected static final String TAG = Registry.class.getSimpleName();
    protected final FirebaseFirestore DB = FirebaseFirestore.getInstance();
    protected final ArrayList<T> list = new ArrayList<>();
    protected SyncListener mSyncListener;

//    public T fromId(String id) {
//        for (T val : this.list) {
//            if (val.getId().equals(id)) {
//                return val;
//            }
//        }
//        return null;
//    }

    public void syncFromDb(String collectionPath, Class<T> className) {
        DB.collection(collectionPath).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    T t = document.toObject(className);
                    addToList(t);
                    if (mSyncListener != null) {
                        mSyncListener.onDataSync();
                    }
                }
            } else {
                Log.e(TAG, "onComplete: ", task.getException());
            }
        });
    }

    public void addToList(T t) {
        Log.d(TAG, "Item added to list " + t.getId());
        list.add(t);
    }

    public ArrayList<T> getList() {
        return this.list;
    }

    public void setSyncListener(SyncListener syncListener) {
        mSyncListener = syncListener;
    }

    public interface SyncListener {
        void onDataSync();
    }
}
