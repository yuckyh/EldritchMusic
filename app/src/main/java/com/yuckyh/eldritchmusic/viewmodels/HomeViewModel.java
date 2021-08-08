package com.yuckyh.eldritchmusic.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yuckyh.eldritchmusic.models.Playlist;
import com.yuckyh.eldritchmusic.models.Song;
import com.yuckyh.eldritchmusic.models.User;
import com.yuckyh.eldritchmusic.registries.PlaylistRegistry;
import com.yuckyh.eldritchmusic.registries.SongRegistry;
import com.yuckyh.eldritchmusic.registries.UserRegistry;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    private static final String TAG = HomeViewModel.class.getSimpleName();
    private static HomeViewModel mInstance = new HomeViewModel();
    private final MutableLiveData<FirebaseUser> mCurrentUser = new MutableLiveData<>();
    private final MutableLiveData<User> mUser = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Playlist>> mPlaylists = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Song>> mSongs = new MutableLiveData<>();

    public static HomeViewModel getInstance() {
        return mInstance;
    }

    public static void setInstance(HomeViewModel instance) {
        mInstance = instance;
    }

    public HomeViewModel() {
        reload();
    }

    public LiveData<User> getUser() {
        if (mUser.getValue() == null) {
            try {
                loadUser();
            } catch (Exception e) {
                Log.e(TAG, "getUser: ", e);
                return null;
            }
        }

        return mUser;
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return mCurrentUser;
    }

    public LiveData<ArrayList<Playlist>> getPlaylists() {
        return mPlaylists;
    }

    public LiveData<ArrayList<Song>> getSongs() {
        return mSongs;
    }

    public void reload() {
        if (mCurrentUser.getValue() != FirebaseAuth.getInstance().getCurrentUser()) {
            mCurrentUser.postValue(FirebaseAuth.getInstance().getCurrentUser());
        }
        mPlaylists.postValue(PlaylistRegistry.getInstance().getList());
        mSongs.postValue(SongRegistry.getInstance().getList());
        loadUser();
    }

    private void loadUser() {
        FirebaseUser currentUser = mCurrentUser.getValue();
        if (currentUser == null) {
            mUser.postValue(null);
            return;
        }

        if (mUser.getValue() == null || !currentUser.getUid().equals(mUser.getValue().getId())) {
            try {
                mUser.postValue(UserRegistry.getInstance().itemFromId(currentUser.getUid()));
            } catch (Exception e) {
                Log.e(TAG, "loadUser: ", e);
                createUser();
            }
        }
    }

    private void createUser() {
        FirebaseUser currentUser = mCurrentUser.getValue();
        if (currentUser == null) {
            mUser.postValue(null);
            return;
        }

        Log.d(TAG, "onSignInResult: pls download");

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .set(new User(currentUser.getUid(), currentUser.getDisplayName()))
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "onSuccess: " + currentUser.getUid() + " added");
                    loadUser();
                })
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));

    }
}
