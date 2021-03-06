package com.chengfu.android.fuplayer.achieve.dj.audio.util;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import androidx.annotation.RequiresApi;

public class AppIconHelper {
    public static Bitmap getAppIconBitmap(PackageManager mPackageManager, String packageName) {

        if (Build.VERSION.SDK_INT >= 26) {
            return getAppIconBitmapV26(mPackageManager, packageName);
        }

        try {
            Drawable drawable = mPackageManager.getApplicationIcon(packageName);
            return ((BitmapDrawable) drawable).getBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Bitmap getAppIconBitmapV26(PackageManager mPackageManager, String packageName) {

        try {
            Drawable drawable = mPackageManager.getApplicationIcon(packageName);

            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof AdaptiveIconDrawable) {
                Drawable backgroundDr = ((AdaptiveIconDrawable) drawable).getBackground();
                Drawable foregroundDr = ((AdaptiveIconDrawable) drawable).getForeground();

                Drawable[] drr = new Drawable[2];
                drr[0] = backgroundDr;
                drr[1] = foregroundDr;

                LayerDrawable layerDrawable = new LayerDrawable(drr);

                int width = layerDrawable.getIntrinsicWidth();
                int height = layerDrawable.getIntrinsicHeight();

                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(bitmap);

                layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                layerDrawable.draw(canvas);

                return bitmap;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
