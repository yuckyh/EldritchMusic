package com.yuckyh.eldritchmusic.utils;

public class SongDuration {
    public static String parse(double duration) {
        int minutes = (int) Math.floor(duration);
        int seconds = (int)((duration - minutes) * 60);

        return minutes + ":" + seconds;
    }
}
