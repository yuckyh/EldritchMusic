package com.yuckyh.eldritchmusic.fragments;

import android.content.Intent;
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
import com.yuckyh.eldritchmusic.activities.PlaylistActivity;
import com.yuckyh.eldritchmusic.adapters.PlaylistAdapter;
import com.yuckyh.eldritchmusic.adapters.SongAdapter;
import com.yuckyh.eldritchmusic.models.Playlist;
import com.yuckyh.eldritchmusic.models.User;
import com.yuckyh.eldritchmusic.registries.UserRegistry;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {
    private static final String TAG = PlaylistFragment.class.getSimpleName();
    private ArrayList<Playlist> mFollowedPlaylists, mCreatedPlaylists;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        try {
            User user = UserRegistry.getInstance().itemFromId(currentUser.getUid());
            mFollowedPlaylists = user.appGetFollowedPlaylists();
            mCreatedPlaylists = user.appGetCreatedPlaylists();
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        ArrayList<Playlist> playlists = new ArrayList<>(mCreatedPlaylists);
        playlists.addAll(mFollowedPlaylists);

        RecyclerView rvPlaylists = view.findViewById(R.id.rvPlaylists);
        rvPlaylists.setAdapter(new PlaylistAdapter(getContext(), playlists, R.layout.item_playlist, -1,
                playlist -> startActivity(new Intent(getContext(), PlaylistActivity.class)
                        .putExtra("id", playlist.getId()))));

        return view;
    }
}
