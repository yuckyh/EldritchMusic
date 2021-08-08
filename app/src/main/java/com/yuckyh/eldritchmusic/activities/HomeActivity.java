package com.yuckyh.eldritchmusic.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.enums.HomeFragmentEnum;
import com.yuckyh.eldritchmusic.fragments.ExploreFragment;
import com.yuckyh.eldritchmusic.fragments.LibraryFragment;
import com.yuckyh.eldritchmusic.fragments.MiniPlayerFragment;
import com.yuckyh.eldritchmusic.fragments.ProfileFragment;
import com.yuckyh.eldritchmusic.models.Song;
import com.yuckyh.eldritchmusic.utils.Duration;
import com.yuckyh.eldritchmusic.utils.MusicPlayer;
import com.yuckyh.eldritchmusic.viewmodels.HomeViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private final LibraryFragment mLibraryFragment = new LibraryFragment();
    private final ExploreFragment mExploreFragment = new ExploreFragment();
    private final ProfileFragment mProfileFragment = new ProfileFragment(
            new ProfileFragment.AuthListener() {
                @Override
                public void onLogout() {
                    mNavMenuMain.setSelectedItemId(R.id.menu_explore);
                }
            });
    private final ArrayList<Fragment> fragments = new ArrayList<>(
            Arrays.asList(mLibraryFragment, mExploreFragment, mProfileFragment));
    private NavigationBarView mNavMenuMain;
    private final HomeViewModel mModel = HomeViewModel.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mNavMenuMain = findViewById(R.id.navMenuMain);

        mNavMenuMain.setOnItemSelectedListener(item -> {
            setItem(item.getItemId());
            return true;
        });

        setNavTab(R.id.menu_explore);

        HomeViewModel.setInstance(new ViewModelProvider(this).get(HomeViewModel.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.flMusicPlayer).setVisibility(MusicPlayer.getInstance().getCurrentSong() == null ? View.GONE : View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.flMusicPlayer, new MiniPlayerFragment()).commit();

        mModel.reload();
    }

    public void setItem(int itemId) {
        int position = HomeFragmentEnum.getPosition(itemId);
        getSupportFragmentManager().beginTransaction().replace(R.id.flHome, fragments.get(position)).commit();
    }

    public void setNavTab(int itemId) {
        mNavMenuMain.setSelectedItemId(itemId);
    }
}
