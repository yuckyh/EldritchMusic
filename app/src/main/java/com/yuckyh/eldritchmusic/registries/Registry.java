package com.yuckyh.eldritchmusic.registries;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.yuckyh.eldritchmusic.models.Model;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Objects;

public abstract class Registry<T extends Model> {
    Class<T> mTypeClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    protected static final String TAG = Registry.class.getSimpleName();
    public final FirebaseFirestore DB = FirebaseFirestore.getInstance();
    protected final ArrayList<T> mList = new ArrayList<>();
    protected OnSyncListener mOnSyncListener;
    protected String mCollectionPath;

    protected Registry(String collectionPath) {
        mCollectionPath = collectionPath;
    }

    public T itemFromId(String id) throws Exception {
        for (T val : this.mList) {
            if (val.getId().equals(id)) {
                return val;
            }
        }
        throw new Exception("Item with id " + id + " not found");
    }

    public DocumentReference refFromId(String id) {
        return DB.collection(mCollectionPath).document(id);
    }

    public void readFromDb() {
        DB.collection(mCollectionPath).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    T t = document.toObject(mTypeClass);
                    t.setObjectsFromRefs();
                    addToList(t);
                }
                if (mOnSyncListener != null) {
                    mOnSyncListener.onDataRead();
                }
                Log.d(TAG, "readFromDb: " + mTypeClass.getSimpleName() + ": " + mCollectionPath);
            } else {
                Log.e(TAG, "readFromDb: ", task.getException());
            }
        });
    }

    public void writeToDb() {
        for (T t : mList) {
            t.setRefsFromObjects();
            DB.collection(mCollectionPath).document(t.getId()).set(t)
                    .addOnCompleteListener(task -> Log.d(TAG, "writeToDb: " + t.getClass().getSimpleName() + ": " + t.getId()))
                    .addOnFailureListener(e -> Log.e(TAG, "writeToDb: ", e));
        }
    }

    ;

    public void addToList(T t) {
        Log.d(TAG, "Item added to list " + t.getId());
        mList.add(t);
    }

    public void removeToList(T t) {
        Log.d(TAG, "removeToList: " + t.getId());
        mList.remove(t);
    }

    public ArrayList<T> getList() {
        return this.mList;
    }

    public void setSyncListener(OnSyncListener onSyncListener) {
        mOnSyncListener = onSyncListener;
    }

    public T refToObject(DocumentReference reference) {
        try {
            return this.itemFromId(reference.getId());
        } catch (Exception e) {
            Log.e(TAG, "refToObject: ", e);
            return null;
        }
    }

    public ArrayList<T> refListToObjectList(ArrayList<DocumentReference> references) {
        ArrayList<T> tArrayList = new ArrayList<>();
        if (references == null) {
            return tArrayList;
        }

        for (DocumentReference id : references) {
            try {
                tArrayList.add(this.itemFromId(id.getId()));
            } catch (Exception e) {
                Log.e(TAG, "setFollowedArtisteIds: ", e);
            }
        }

        return tArrayList;
    }

    public interface OnSyncListener {
        void onDataRead();
    }
}
