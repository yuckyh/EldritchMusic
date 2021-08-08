package com.yuckyh.eldritchmusic.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.activities.PlaylistActivity;
import com.yuckyh.eldritchmusic.adapters.PlaylistAdapter;
import com.yuckyh.eldritchmusic.adapters.SongAdapter;
import com.yuckyh.eldritchmusic.registries.PlaylistRegistry;
import com.yuckyh.eldritchmusic.registries.SongRegistry;
import com.yuckyh.eldritchmusic.viewmodels.HomeViewModel;

public class ExploreFragment extends Fragment {
    private static final String TAG = ExploreFragment.class.getSimpleName();
    private HomeViewModel mModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = HomeViewModel.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        RecyclerView rvExploreSongs = view.findViewById(R.id.rvExploreSongs);
        RecyclerView rvExplorePlaylists = view.findViewById(R.id.rvExplorePlaylists);

        Log.d(TAG, "onCreateView: " + SongRegistry.getInstance().getList());

        rvExploreSongs.addItemDecoration(new DividerItemDecoration(rvExploreSongs.getContext(), DividerItemDecoration.VERTICAL));

        mModel.getSongs().observeForever(songs -> rvExploreSongs.setAdapter(new SongAdapter(
                getContext(), songs, R.layout.item_song, 20)));

        mModel.getPlaylists().observeForever(playlists -> rvExplorePlaylists.setAdapter(new PlaylistAdapter(getContext(),
                PlaylistRegistry.getInstance().getList(),
                R.layout.item_playlist_card_square,
                5,
                playlist -> startActivity(new Intent(getContext(), PlaylistActivity.class)
                        .putExtra("id", playlist.getId())))));

        return view;
    }
}
