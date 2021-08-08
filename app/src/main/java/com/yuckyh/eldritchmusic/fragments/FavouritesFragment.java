package com.yuckyh.eldritchmusic.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.adapters.SongAdapter;
import com.yuckyh.eldritchmusic.models.Song;
import com.yuckyh.eldritchmusic.models.User;
import com.yuckyh.eldritchmusic.registries.UserRegistry;

import java.util.ArrayList;

public class FavouritesFragment extends Fragment {
    private static final String TAG = FavouritesFragment.class.getSimpleName();
    private ArrayList<Song> mFavourites;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        try {
            User user = UserRegistry.getInstance().itemFromId(currentUser.getUid());
            mFavourites = user.appGetFavourites();
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        RecyclerView rvFavourites = view.findViewById(R.id.rvFavourites);
        rvFavourites.setAdapter(new SongAdapter(getContext(), mFavourites, R.layout.item_song, -1));

        return view;
    }
}
