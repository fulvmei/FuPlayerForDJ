package com.chengfu.android.fuplayer.dj.screen;

import android.content.Context;
import android.hardware.SensorManager;
import android.support.annotation.IntDef;
import android.view.OrientationEventListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class OrientationEventObserver {

    private static final int SENSOR_DELAY = 800;
    private long lastTime;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SCREEN_ORIENTATION_PORTRAIT, SCREEN_ORIENTATION_LANDSCAPE, SCREEN_ORIENTATION_REVERSE_LANDSCAPE})
    @interface ScreenOrientation {
    }

    public static final int SCREEN_ORIENTATION_PORTRAIT = 0;
    public static final int SCREEN_ORIENTATION_LANDSCAPE = 1;
    public static final int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 2;


    private final Context context;
    private final OrientationEventListener orientationEventListener;
    private OnOrientationChangedListener onOrientationChangedListener;

    private int screenOrientation = SCREEN_ORIENTATION_PORTRAIT;

    public interface OnOrientationChangedListener {
        void onOrientationChanged(int orientation);

        void onScreenOrientationChanged(@ScreenOrientation int screenOrientation);

    }

    public OrientationEventObserver(Context context) {
        this.context = context;

        orientationEventListener = new OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (onOrientationChangedListener != null) {
                    onOrientationChangedListener.onOrientationChanged(orientation);
                }
                if ((System.currentTimeMillis() - lastTime) < SENSOR_DELAY) {
                    return;
                }
                lastTime = System.currentTimeMillis();
                int temp = -1;
                if (((orientation >= 0) && (orientation <= 30)) || (orientation >= 330)) {
                    temp = SCREEN_ORIENTATION_PORTRAIT;
                } else if ((orientation >= 240) && (orientation <= 300)) {
                    temp = SCREEN_ORIENTATION_LANDSCAPE;
                } else if ((orientation >= 60) && (orientation <= 120)) {
                    temp = SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                } else if ((orientation >= 150) && (orientation <= 210)) {
                    temp = SCREEN_ORIENTATION_PORTRAIT;
                }
                if (temp == -1) {
                    return;
                }
                if (temp == screenOrientation) {
                    return;
                }
                screenOrientation = temp;
                if (onOrientationChangedListener != null) {
                    onOrientationChangedListener.onScreenOrientationChanged(screenOrientation);
                }
            }
        };
    }

    public OnOrientationChangedListener getOnOrientationChangedListener() {
        return onOrientationChangedListener;
    }

    public void setOnOrientationChangedListener(OnOrientationChangedListener onOrientationChangedListener) {
        this.onOrientationChangedListener = onOrientationChangedListener;
    }

    @ScreenOrientation
    public int getScreenOrientation() {
        return screenOrientation;
    }

    public void enable() {
        orientationEventListener.enable();
    }

    public void disable() {
        orientationEventListener.disable();
    }
}
