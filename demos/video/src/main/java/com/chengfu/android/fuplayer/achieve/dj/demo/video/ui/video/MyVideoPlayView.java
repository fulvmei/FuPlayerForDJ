package com.chengfu.android.fuplayer.achieve.dj.demo.video.ui.video;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.Window;

import com.chengfu.android.fuplayer.ui.FuPlayerView;

import java.util.List;

public class MyVideoPlayView extends FuPlayerView {

    public MyVideoPlayView(Context context) {
        super(context);
    }

    public MyVideoPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        System.out.println("onConfigurationChanged newConfig.orientation=" + newConfig.orientation + ",context=" + getContext());
        super.onConfigurationChanged(newConfig);

        postDelayed(new Runnable() {
            @Override
            public void run() {
//                adasd();
            }
        },100);
    }

    private void adasd(){
        Activity activity = (Activity) getContext();
        DisplayCutout displayCutout = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            displayCutout = activity.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
            Log.e("TAG", "安全区域距离屏幕左边的距离 SafeInsetLeft:" + displayCutout.getSafeInsetLeft());
            Log.e("TAG", "安全区域距离屏幕右部的距离 SafeInsetRight:" + displayCutout.getSafeInsetRight());
            Log.e("TAG", "安全区域距离屏幕顶部的距离 SafeInsetTop:" + displayCutout.getSafeInsetTop());
            Log.e("TAG", "安全区域距离屏幕底部的距离 SafeInsetBottom:" + displayCutout.getSafeInsetBottom());

            List<Rect> rects = displayCutout.getBoundingRects();
            if (rects == null || rects.size() == 0) {
                Log.e("TAG", "不是刘海屏");
            } else {
                Log.e("TAG", "刘海屏数量:" + rects.size());
                for (Rect rect : rects) {
                    Log.e("TAG", "刘海屏区域：" + rect);
                }
            }
        }
    }

}
