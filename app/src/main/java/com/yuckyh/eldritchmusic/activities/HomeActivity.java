package com.yuckyh.eldritchmusic.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.enums.HomeFragmentEnum;
import com.yuckyh.eldritchmusic.fragments.ExploreFragment;
import com.yuckyh.eldritchmusic.fragments.LibraryFragment;
import com.yuckyh.eldritchmusic.fragments.ProfileFragment;

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
    }

    public void setItem(int itemId) {
        int position = HomeFragmentEnum.getPosition(itemId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flHome, fragments.get(position));
        transaction.commit();
    }

    public void setNavTab(int itemId) {
        mNavMenuMain.setSelectedItemId(itemId);
    }
}
