package com.yuckyh.eldritchmusic.utils;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.ColorInt;

public class ColorUtil {
    public static @ColorInt int getColorFromAttr(Resources.Theme theme, int resourceId) {
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(resourceId, typedValue, true);
        @ColorInt int color = typedValue.data;
        return color;
    }

    public static double getLuminance(@ColorInt int color) {
        final double r = (double) Color.red(color) / 255;
        final double g = (double) Color.green(color) / 255;
        final double b = (double) Color.blue(color) / 255;
        Log.d("Color", "getLuminance: " + r + " " + g + " " + b);
        final double luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b;
        Log.d("Color", "getLuminance: " + luminance);
        return luminance;
    }
}
