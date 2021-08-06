package com.yuckyh.eldritchmusic.utils;

public class Duration {
    public static String minutesToTimer(double duration) {
        int minutes = (int) Math.floor(duration);

        int seconds = (int)((duration - minutes) * 60);

        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

    public static String minutesToHours(double duration) {
        int hours = (int) duration / 60;

        int minutes = (int) duration % 60;

        return (hours > 0 ? hours + "h " : " ") + minutes + "m";
    }
}
