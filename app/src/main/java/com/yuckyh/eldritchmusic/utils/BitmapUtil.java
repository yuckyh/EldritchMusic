package com.yuckyh.eldritchmusic.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;

public class BitmapUtil {
    private static final String TAG = BitmapUtil.class.getSimpleName();
    private Bitmap mBitmap;

    public void downloadImageBitmap(String url, Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        Executors.newCachedThreadPool().execute(() -> {
            try {
                mBitmap = BitmapFactory.decodeStream(new URL(url).openStream());
                handler.post(runnable);
            } catch (IOException e) {
                Log.e(TAG, "run: ", e);
                handler.removeCallbacks(runnable);
            }
        });
    }

    public static Bitmap blur(Context context, Bitmap image, float bitmapScale, float blurRadius) {
        int width = Math.round(image.getWidth() * bitmapScale);
        int height = Math.round(image.getHeight() * bitmapScale);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript script = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(script, Element.U8_4(script));
        Allocation tmpIn = Allocation.createFromBitmap(script, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(script, outputBitmap);
        theIntrinsic.setRadius(blurRadius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    public static Bitmap blur(Context context, Bitmap image) {
        return blur(context, image, 1f, 5f);
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }
}
