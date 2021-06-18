package com.yuckyh.eldritchmusic.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;

public class ImageViewUtils {
    private static final String TAG = ImageViewUtils.class.getSimpleName();
    public static Bitmap bitmap;

    public static void downloadImageBitmap(String url, Runnable runnable) {
        Executors.newCachedThreadPool().execute(() -> {
            Handler handler = new Handler(Looper.getMainLooper());

            try {
                bitmap = BitmapFactory.decodeStream(new URL(url).openStream());
            } catch (IOException e) {
                Log.e(TAG, "run: ", e);
            }

            handler.post(runnable);
        });
    }
}
