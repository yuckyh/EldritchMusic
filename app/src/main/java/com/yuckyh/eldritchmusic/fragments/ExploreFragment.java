package com.yuckyh.eldritchmusic.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.adapters.SongAdapter;
import com.yuckyh.eldritchmusic.models.Song;
import com.yuckyh.eldritchmusic.registries.SongRegistry;

import java.util.ArrayList;

public class ExploreFragment extends Fragment {
    private static final String TAG = ExploreFragment.class.getSimpleName();

    // Required empty public constructor
    public ExploreFragment() {
    }

    public static ExploreFragment getInstance(ArrayList<Song> songs) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("songs", songs);
        ExploreFragment fragment = new ExploreFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        Log.d(TAG, "onCreateView: " + SongRegistry.getInstance().getList());

        RecyclerView rvExplore = view.findViewById(R.id.rvExplore);

        rvExplore.setAdapter(new SongAdapter(getContext(), SongRegistry.getInstance().getList()));

//        rvExplore.setOnClickListener(on);

//        SongViewModel mViewModel = new ViewModelProvider(this).get(SongViewModel.class);

//        Observer<ArrayList<Song>> observer = songs -> {
//            Log.d(TAG, "onCreateView: " + SongRegistry.getInstance().getList());
//            rvExplore.setAdapter(new SongAdapter(getContext(), SongRegistry.getInstance().getList()));
//        };
//
//        mViewModel.getListLiveData().observe(getViewLifecycleOwner(), observer);

        return view;
    }

    public void onItemClick(View view) {
        Log.d(TAG, "onItemClick: " + view.toString());
    }
}