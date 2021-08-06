package com.yuckyh.eldritchmusic.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.activities.PlaylistActivity;
import com.yuckyh.eldritchmusic.adapters.ArtisteAdapter;
import com.yuckyh.eldritchmusic.adapters.PlaylistAdapter;
import com.yuckyh.eldritchmusic.models.User;
import com.yuckyh.eldritchmusic.registries.UserRegistry;
import com.yuckyh.eldritchmusic.utils.BitmapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );
    private TextView mTxtViewUsername, mTxtViewPlaylistCount, mTxtViewSongCount, mTxtViewFollowingCount;
    private ImageView mImgViewPfp;
    private RecyclerView mRvProfilePlaylists;
    private final AuthListener mListener;
    private ProfileViewModel mModel;
    private RecyclerView mRvProfileArtistes;
    private TextView mTxtViewPlaylistLabel;
    private TextView mTxtViewFollowingLabel;

    public ProfileFragment(AuthListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        reload();

        if (mModel.getCurrentUser().getValue() != null) {
            return;
        }

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder()
                        .setRequireName(true)
                        .setAllowNewAccounts(true)
                        .build(),
                new AuthUI.IdpConfig.GoogleBuilder()
                        .setSignInOptions(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.ic_launcher_round)
                .setTheme(R.style.Theme_EldritchMusic)
                .build();

        signInLauncher.launch(signInIntent);

        Log.d(TAG, "onCreate: no user");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        if (view == null) {
            return null;
        }

        ImageButton imgBtnLogout = view.findViewById(R.id.imgBtnLogout);
        mTxtViewUsername = view.findViewById(R.id.txtViewUsername);
        mImgViewPfp = view.findViewById(R.id.imgViewPfp);
        mTxtViewPlaylistCount = view.findViewById(R.id.txtViewPlaylistCount);
        mTxtViewSongCount = view.findViewById(R.id.txtViewSongCount);
        mTxtViewFollowingCount = view.findViewById(R.id.txtViewFollowingCount);
        mTxtViewPlaylistLabel = view.findViewById(R.id.txtViewPlaylistLabel);
        mTxtViewFollowingLabel = view.findViewById(R.id.txtViewFollowingLabel);
        mRvProfilePlaylists = view.findViewById(R.id.rvProfilePlaylists);
        mRvProfileArtistes = view.findViewById(R.id.rvProfileArtistes);
        imgBtnLogout.setOnClickListener(v -> AuthUI.getInstance()
                .signOut(requireContext())
                .addOnCompleteListener(task -> logout()));

        mModel.getCurrentUser().observeForever(currentUser -> {
            if (currentUser == null) {
                mTxtViewUsername.setText("Guest");
                mImgViewPfp.setImageDrawable(AppCompatResources.getDrawable(view.getContext(),R.drawable.ic_round_account_circle_24));
                return;
            }
            mTxtViewUsername.setText(currentUser.getDisplayName());
            if (currentUser.getPhotoUrl() != null) {
                BitmapUtil util = new BitmapUtil();
                util.downloadImageBitmap(currentUser.getPhotoUrl().toString(),
                        () -> mImgViewPfp.setImageBitmap(util.getBitmap()));
            }
            if (mModel.getUser() == null) {
                mModel.loadUser();
            }
        });

        mModel.getUser().observeForever(user -> {
            if (user == null) {
                mModel.reload();

                mTxtViewFollowingCount.setText("0");
                mTxtViewSongCount.setText("0");
                mTxtViewPlaylistCount.setText("0");
                mTxtViewPlaylistLabel.setVisibility(View.GONE);
                mTxtViewFollowingLabel.setVisibility(View.GONE);
                PlaylistAdapter playlistAdapter = new PlaylistAdapter(getContext(),
                        new ArrayList<>(),
                        R.layout.item_playlist_card,
                        itemId -> {});
                mRvProfilePlaylists.setAdapter(playlistAdapter);
                mRvProfileArtistes.setAdapter(new ArtisteAdapter(getContext(), new ArrayList<>()));
                return;
            }

            int followingCount = user.getFollowedArtistes().size();
            int songCount = user.getFavourites().size();
            int playlistCount = user.getCreatedPlaylists().size();

            if (playlistCount > 0) {
                mTxtViewPlaylistLabel.setVisibility(View.VISIBLE);
            } else {
                mTxtViewPlaylistLabel.setVisibility(View.GONE);
            }

            if (followingCount > 0) {
                mTxtViewFollowingLabel.setVisibility(View.VISIBLE);
            } else {
                mTxtViewFollowingLabel.setVisibility(View.GONE);
            }

            mTxtViewFollowingCount.setText(String.valueOf(followingCount));
            mTxtViewSongCount.setText(String.valueOf(songCount));
            mTxtViewPlaylistCount.setText(String.valueOf(playlistCount));
            mRvProfilePlaylists.setAdapter(new PlaylistAdapter(getContext(),
                    user.getCreatedPlaylists(),
                    R.layout.item_playlist_card,
                    itemId -> {
                        startActivity(new Intent(getContext(), PlaylistActivity.class)
                                .putExtra("id", itemId));
                    }));
            mRvProfileArtistes.setAdapter(new ArtisteAdapter(getContext(), user.getFollowedArtistes()));
        });

        return view;
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (response == null) {
            Log.d(TAG, "onSignInResult: null");
            return;
        }

        if (result.getResultCode() == RESULT_OK) {
            reload();
            Log.d(TAG, "onSignInResult: logged in");
        } else {
            logout();
            Log.e(TAG, "onSignInResult: ", response.getError());
        }
    }

    private void logout() {
        mListener.onLogout();
        Log.d(TAG, "logout: ");
        reload();
    }

    public void reload() {
        mModel.reload();
    }

    public interface AuthListener {
        void onLogout();
    }

    public static class ProfileViewModel extends ViewModel {
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

        private void reload() {
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
}
