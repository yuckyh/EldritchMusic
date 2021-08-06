package com.yuckyh.eldritchmusic.enums;

import com.yuckyh.eldritchmusic.R;

public enum HomeFragmentEnum {
    LIBRARY_FRAGMENT(0, R.id.menu_library),
    EXPLORE_FRAGMENT(1, R.id.menu_explore),
    PROFILE_FRAGMENT(2, R.id.menu_profile);

    private final int mPosition, mItemId;

    HomeFragmentEnum(int position, int itemId) {
        mPosition = position;
        mItemId = itemId;
    }

    public static int getPosition(int itemId) {
        for (HomeFragmentEnum val : HomeFragmentEnum.values()) {
            if (val.mItemId == itemId) {
                return val.mPosition;
            }
        }

        return -1;
    }

//    public static int getItemId(int position) {
//        for (HomeFragmentEnum val : HomeFragmentEnum.values()) {
//            if (val.mPosition == position) {
//                return val.mItemId;
//            }
//        }
//
//        return -1;
//    }
}
