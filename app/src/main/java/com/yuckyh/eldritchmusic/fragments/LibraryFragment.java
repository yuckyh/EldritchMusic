package com.yuckyh.eldritchmusic.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.enums.LibraryFragmentEnum;

import java.util.ArrayList;

public class LibraryFragment extends Fragment {
    private FavouritesFragment mFavouritesFragment;
    private PlaylistFragment mPlaylistFragment;
    private RecentFragment mRecentFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        TabLayout tlLibrary = view.findViewById(R.id.tlLibrary);
        ViewPager2 vpLibrary = view.findViewById(R.id.vpLibrary);
        vpLibrary.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) return mFavouritesFragment;
                if (position == 1) return mPlaylistFragment;
                return mRecentFragment;
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(tlLibrary, vpLibrary, (tab, position) -> {
            tab.setText(LibraryFragmentEnum.getTabLabel(position));
        }).attach();

        vpLibrary.setSaveEnabled(false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFavouritesFragment = new FavouritesFragment();
        mPlaylistFragment = new PlaylistFragment();
        mRecentFragment = new RecentFragment();
    }
}
