package com.yuckyh.eldritchmusic.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.enums.HomeFragmentEnum;
import com.yuckyh.eldritchmusic.fragments.ExploreFragment;
import com.yuckyh.eldritchmusic.fragments.LibraryFragment;
import com.yuckyh.eldritchmusic.fragments.ProfileFragment;
import com.yuckyh.eldritchmusic.fragments.SettingFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView mNavMenuMain = null;
    private ViewPager2 mVpMain = null;
    private final LibraryFragment mLibraryFragment = new LibraryFragment();
    private final ExploreFragment mExploreFragment = new ExploreFragment();
    private final ProfileFragment mProfileFragment = new ProfileFragment();
    private final SettingFragment mSettingFragment = new SettingFragment();
    private final ArrayList<Fragment> fragments = new ArrayList<Fragment>() {
        {
            add(mLibraryFragment);
            add(mExploreFragment);
            add(mProfileFragment);
            add(mSettingFragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        mNavMenuMain = findViewById(R.id.navMenuMain);
        mVpMain = findViewById(R.id.vpMain);

        initializeEventListeners();

        mNavMenuMain.setSelectedItemId(R.id.menu_explore);
    }

    private void initializeEventListeners() {
        mVpMain.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @NotNull
            @Override
            public Fragment createFragment(int position) {
                return fragments.get(position);
            }

            @Override
            public int getItemCount() {
                return fragments.size();
            }
        });

        mVpMain.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mNavMenuMain.setSelectedItemId(HomeFragmentEnum.getItemId(position));
            }
        });

        mNavMenuMain.setOnNavigationItemSelectedListener(item -> {
            int position = HomeFragmentEnum.getPosition(item.getItemId());
            mVpMain.setCurrentItem(position);

            return true;
        });
    }
}
