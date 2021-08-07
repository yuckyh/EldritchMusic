package com.yuckyh.eldritchmusic.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yuckyh.eldritchmusic.fragments.ProfileFragment;
import com.yuckyh.eldritchmusic.models.User;
import com.yuckyh.eldritchmusic.registries.UserRegistry;

public class ProfileViewModel extends ViewModel {
    private static final String TAG = ProfileViewModel.class.getSimpleName();
    private final MutableLiveData<FirebaseUser> mCurrentUser = new MutableLiveData<>();
    private final MutableLiveData<User> mUser = new MutableLiveData<>();

    public ProfileViewModel() {
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

    public void reload() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (mCurrentUser.getValue() != auth.getCurrentUser()) {
            mCurrentUser.postValue(auth.getCurrentUser());
        }
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
                mUser.postValue(UserRegistry.getInstance().fromId(currentUser.getUid()));
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
