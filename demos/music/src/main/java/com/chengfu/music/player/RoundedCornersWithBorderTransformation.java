package com.chengfu.music.player;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;


public class RoundedCornersWithBorderTransformation extends BitmapTransformation {

    private static final int VERSION = 1;
    private static final String ID = "com.chengfu.glide.transformations.RoundedCornersTransformation." + VERSION;

    private int radius;
    private int borderSize;
    private int borderColor;

    public RoundedCornersWithBorderTransformation(int radius, int borderSize, @ColorInt int borderColor) {
        this.radius = radius;
        this.borderSize = borderSize;
        this.borderColor = borderColor;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();

        Bitmap bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setHasAlpha(true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(new RectF(0, 0, width, height), radius, radius, paint);

        Paint borderPaint = new Paint();
        borderPaint.setDither(true);
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderSize);
        canvas.drawRoundRect(new RectF(borderSize / 2f, borderSize / 2f, width - borderSize / 2f, height - borderSize / 2f), radius - borderSize / 2f, radius - borderSize / 2f, borderPaint);
        return bitmap;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RoundedCornersWithBorderTransformation &&
                ((RoundedCornersWithBorderTransformation) o).radius == radius &&
                ((RoundedCornersWithBorderTransformation) o).borderSize == borderSize &&
                ((RoundedCornersWithBorderTransformation) o).borderColor == borderColor;
    }

    @Override
    public int hashCode() {
        return ID.hashCode() + radius * 10000 + borderSize * 100 + borderColor * 10;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + radius + borderSize + borderColor).getBytes(CHARSET));
    }
}
