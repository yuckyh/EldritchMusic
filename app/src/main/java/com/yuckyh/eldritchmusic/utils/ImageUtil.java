package com.yuckyh.eldritchmusic.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.palette.graphics.Palette;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;

public class ImageUtil {
    private static final String TAG = ImageUtil.class.getSimpleName();
    private final Context mContext;
    private Bitmap mBitmap;

    public ImageUtil(Context context) {
        mContext = context;
    }

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

    public Bitmap blur(float bitmapScale, float blurRadius) {
        int width = Math.round(mBitmap.getWidth() * bitmapScale);
        int height = Math.round(mBitmap.getHeight() * bitmapScale);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(mBitmap, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript script = RenderScript.create(mContext);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(script, Element.U8_4(script));
        Allocation tmpIn = Allocation.createFromBitmap(script, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(script, outputBitmap);
        theIntrinsic.setRadius(blurRadius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    public Bitmap blur() {
        return blur(1f, 5f);
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setAlpha(Context context, ImageView imageView) {
        Palette palette = Palette.from(mBitmap).generate();
        @ColorInt int color;

        int nightModeFlags = context.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
            color = palette.getLightVibrantColor(ColorUtil.getColorFromAttr(context.getTheme(), Color.WHITE));

            double luminance = ColorUtil.getLuminance(color);
            imageView.setAlpha((float) (1 - luminance));
        } else {
            color = palette.getDarkVibrantColor(ColorUtil.getColorFromAttr(context.getTheme(), Color.WHITE));

            double luminance = ColorUtil.getLuminance(color);
            imageView.setAlpha((float) (1 - luminance));
        }
    }
}
