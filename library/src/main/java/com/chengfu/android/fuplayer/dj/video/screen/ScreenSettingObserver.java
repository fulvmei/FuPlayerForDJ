package com.chengfu.android.fuplayer.dj.video.screen;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;

public final class ScreenSettingObserver {

    private final Context context;
    private final ChangedObserver changedObserver;

    final ContentResolver contentResolver;
    private boolean allowScreenRotation;
    private OnScreenSettingChangedListener onScreenSettingChangedListener;

    public interface OnScreenSettingChangedListener {
        void onScreenSettingChanged(boolean allowScreenRotation);
    }

    public ScreenSettingObserver( Context context) {
        this.context = context;
        contentResolver = context.getContentResolver();

        changedObserver = new ChangedObserver(new Handler());
    }

    public boolean isAllowScreenRotation() {
        int accelerometerRotation = Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0);
        allowScreenRotation = accelerometerRotation == 1;
        return allowScreenRotation;
    }

    public void setOnScreenSettingChangedListener(OnScreenSettingChangedListener onScreenSettingChangedListener) {
        this.onScreenSettingChangedListener = onScreenSettingChangedListener;
    }

    public OnScreenSettingChangedListener getOnScreenSettingChangedListener() {
        return onScreenSettingChangedListener;
    }

    public void startObserver() {
        contentResolver.registerContentObserver(Settings.System
                        .getUriFor(Settings.System.ACCELEROMETER_ROTATION), false,
                changedObserver);
    }

    public void stopObserver() {
        contentResolver.unregisterContentObserver(changedObserver);
    }

    private class ChangedObserver extends ContentObserver {
        public ChangedObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            int accelerometerRotation = Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0);
            boolean allowScreenRotation = accelerometerRotation == 1;
            if (onScreenSettingChangedListener != null
                    && ScreenSettingObserver.this.allowScreenRotation != allowScreenRotation) {
                ScreenSettingObserver.this.allowScreenRotation = allowScreenRotation;
                onScreenSettingChangedListener.onScreenSettingChanged(allowScreenRotation);
            }
        }
    }
}
