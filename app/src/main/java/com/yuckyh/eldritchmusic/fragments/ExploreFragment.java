package com.yuckyh.eldritchmusic.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.adapters.SongAdapter;
import com.yuckyh.eldritchmusic.registries.SongRegistry;

public class ExploreFragment extends Fragment {
    private static final String TAG = ExploreFragment.class.getSimpleName();

    // Required empty public constructor
    public ExploreFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        Log.d(TAG, "onCreateView: " + SongRegistry.getInstance().getList());

        RecyclerView rvExplore = view.findViewById(R.id.rvExploreSongs);

        rvExplore.setAdapter(new SongAdapter(getContext(), SongRegistry.getInstance().getList(), R.layout.item_song));
        rvExplore.addItemDecoration(new DividerItemDecoration(rvExplore.getContext(), DividerItemDecoration.VERTICAL));

        return view;
    }
}
