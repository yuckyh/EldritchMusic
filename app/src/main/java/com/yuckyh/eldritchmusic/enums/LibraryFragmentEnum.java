package com.yuckyh.eldritchmusic.enums;

public enum LibraryFragmentEnum {
    FAVOURITES_FRAGMENT(0, "Favourites"),
    PLAYLISTS_FRAGMENT(1, "Playlists"),
    RECENT_FRAGMENT(2, "Recent");

    private final int mPosition;
    private final String mTabLabel;

    LibraryFragmentEnum(int position, String tabLabel) {
        mPosition = position;
        mTabLabel = tabLabel;
    }

    public static int getPosition(String tabLabel) {
        for (LibraryFragmentEnum val : LibraryFragmentEnum.values()) {
            if (val.mTabLabel.equals(tabLabel)) {
                return val.mPosition;
            }
        }

        return -1;
    }

    public static String getTabLabel(int position) {
        for (LibraryFragmentEnum val : LibraryFragmentEnum.values()) {
            if (val.mPosition == position) {
                return val.mTabLabel;
            }
        }

        return "";
    }
}
