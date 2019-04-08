package com.chengfu.android.fuplayer.dj;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chengfu.android.fuplayer.dj.gesture.GestureImpl;
import com.chengfu.android.fuplayer.dj.screen.ScreenRotationHelper;
import com.chengfu.android.fuplayer.video.DefaultControlView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DJVideoControlView extends DefaultControlView {

    private static final String TAG = "VideoControlView";

    View controllerTop;
    TextView titleView;
    ProgressBar mBottomProgressView;
    ImageView fullScreenView;
    ImageButton backView;

    View slideBrightnessView;
    TextView slideBrightnessPercent;
    ProgressBar slideBrightnessProgress;


    View slideForwardView;
    ImageView slideForwardImage;
    TextView slideForwardDuration;
    TextView slideForwardPosition;
    ProgressBar slideForwardProgress;

    View slideVolumeView;
    TextView slideVolumePercent;
    ProgressBar slideVolumeProgress;

    protected OnScreenClickListener onScreenClickListener;

    String title;

    boolean showBottomProgress;
    boolean controlViewShow;
    boolean fullScreen;
    boolean showTopOnlyFullScreen;
    boolean showPlayPauseInBuffering;

    Gesture gestureHelper;
    GestureDetector gestureDetector;
    long oldPosition;
    long newPosition;
    long duration;

    Rotation rotation;

    public interface OnScreenClickListener {
        void onScreenClick(boolean fullScreen);
    }

    protected OnBackClickListener onBackClickListener;

    public interface OnBackClickListener {

        void onBackClick(View v);
    }

    public DJVideoControlView(Context context) {
        this(context, null);
    }

    public DJVideoControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DJVideoControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        gestureDetector = new GestureDetector(mContext, onGestureListener);

        gestureHelper = new GestureImpl(this);

        gestureHelper.setOnSlideChangedListener(onSlideChangedListener);

        rotation = new ScreenRotationHelper((Activity) context);
    }

    @Override
    protected int getLayoutResourcesId(int layoutId) {
        return R.layout.fpu_view_controller;
    }

    @Override
    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        super.initView(context, attrs, defStyleAttr);

        controllerTop = findViewById(R.id.controller_top);

        updateTopVisibility();

        mBottomProgressView = findViewById(R.id.controller_bottom_progress);
        if (mBottomProgressView != null) {
            mBottomProgressView.setMax(mSeekNumber);
        }

        updateBottomProgressView();

        fullScreenView = findViewById(R.id.controller_screen_switch);
        if (fullScreenView != null) {
            fullScreenView.setOnClickListener(v -> {
                if (onScreenClickListener != null) {
                    onScreenClickListener.onScreenClick(fullScreen);
                }
            });
            updateFullScreenViewResource(fullScreenView, fullScreen);
            hideAfterTimeout();
        }

        backView = findViewById(R.id.controller_back);

        if (backView != null) {
            backView.setOnClickListener(v -> {
                if (onBackClickListener != null) {
                    onBackClickListener.onBackClick(v);
                }
            });
            updateBackViewResource(backView, fullScreen);
            hideAfterTimeout();
        }

        titleView = findViewById(R.id.controller_title);

        setTitle(title);

        slideBrightnessView = findViewById(R.id.controller_slide_brightness);
        slideBrightnessPercent = findViewById(R.id.controller_slide_brightness_percent);
        slideBrightnessProgress = findViewById(R.id.controller_slide_brightness_progress);
        if (slideBrightnessProgress != null) {
            slideBrightnessProgress.setMax(100);
        }

        slideForwardView = findViewById(R.id.controller_slide_forward);
        slideForwardImage = findViewById(R.id.controller_slide_forward_image);
        slideForwardPosition = findViewById(R.id.controller_slide_forward_position);
        slideForwardDuration = findViewById(R.id.controller_slide_forward_duration);
        slideForwardProgress = findViewById(R.id.controller_slide_forward_progress);
        if (slideForwardProgress != null) {
            slideForwardProgress.setMax(100);
        }

        slideVolumeView = findViewById(R.id.controller_slide_volume);
        slideVolumePercent = findViewById(R.id.controller_slide_volume_percent);
        slideVolumeProgress = findViewById(R.id.controller_slide_volume_progress);
        if (slideVolumeProgress != null) {
            slideVolumeProgress.setMax(100);
        }
    }

    @Override
    protected void updateAll() {
        updateBottomProgressView();
        super.updateAll();
    }

    @Override
    protected void updatePlayPauseViewResource(@NonNull ImageButton imageButton, boolean playWhenReady) {
        if (playWhenReady) {
            if (fullScreen) {
                imageButton.setImageResource(R.drawable.fpu_selector_pause_land);
            } else {
                imageButton.setImageResource(R.drawable.fpu_selector_pause_port);
            }
        } else {
            if (fullScreen) {
                imageButton.setImageResource(R.drawable.fpu_selector_play_land);
            } else {
                imageButton.setImageResource(R.drawable.fpu_selector_play_port);
            }

        }
    }

    @Override
    protected boolean onProgressUpdate(long position, long duration, int bufferedPercent) {
        if (showBottomProgress && mBottomProgressView != null) {
            if (duration > 0) {
                long pos = mSeekNumber * position / duration;
                mBottomProgressView.setProgress((int) pos);
            }
            mBottomProgressView.setSecondaryProgress(bufferedPercent * 10);
        }
        return true;
    }

    @Override
    protected void onHideChanged(boolean hide) {
        super.onHideChanged(hide);
        controlViewShow = !hide;

        updateBottomProgressView();
    }

    public void setEnableGestureType(int enableGestureType) {
        gestureHelper.setShowType(enableGestureType);
    }

    public boolean isShowTopOnlyFullScreen() {
        return showTopOnlyFullScreen;
    }

    public void setShowTopOnlyFullScreen(boolean showTopOnlyFullScreen) {
        if (this.showTopOnlyFullScreen == showTopOnlyFullScreen) {
            return;
        }
        this.showTopOnlyFullScreen = showTopOnlyFullScreen;

        updateTopVisibility();
    }

    protected void updateTopVisibility() {
        if (controllerTop == null) {
            return;
        }
        if (showTopOnlyFullScreen && !fullScreen) {
            controllerTop.setVisibility(View.GONE);
        } else {
            controllerTop.setVisibility(View.VISIBLE);
        }
    }

    public void setTitle(String title) {
        this.title = title;
        if (titleView != null) {
            titleView.setText(TextUtils.isEmpty(title) ? "" : title);
        }
    }

    public String getTitle() {
        return title;
    }

    public OnScreenClickListener getOnScreenClickListener() {
        return onScreenClickListener;
    }

    public void setOnScreenClickListener(OnScreenClickListener onScreenClickListener) {
        this.onScreenClickListener = onScreenClickListener;
    }

    public OnBackClickListener getOnBackClickListener() {
        return onBackClickListener;
    }

    public void setOnBackClickListener(OnBackClickListener onBackClickListener) {
        this.onBackClickListener = onBackClickListener;
    }

    @Override
    public boolean isFullScreen() {
        return fullScreen;
    }

    @Override
    public void setFullScreen(boolean fullScreen) {
        if (this.fullScreen == fullScreen) {
            return;
        }
        this.fullScreen = fullScreen;

        updateFullScreenViewResource(fullScreenView, fullScreen);
        updateBackViewResource(backView, fullScreen);
        updatePlayPauseView();
        updateVolumeView();
        updateTopVisibility();
    }

    protected void updateBackViewResource(@Nullable ImageView imageButton, boolean fullScreen) {
        if (imageButton == null) {
            return;
        }
        if (fullScreen) {
            imageButton.setImageResource(R.drawable.fpu_selector_back_land);
        } else {
            imageButton.setImageResource(R.drawable.fpu_selector_back_port);
        }
    }

    protected void updateFullScreenViewResource(@Nullable ImageView imageButton, boolean fullScreen) {
        if (imageButton == null) {
            return;
        }
        if (fullScreen) {
            imageButton.setImageResource(R.drawable.fpu_ic_exit_full_screen);
        } else {
            imageButton.setImageResource(R.drawable.fpu_ic_full_screen);
        }
    }

    @Override
    protected void updateVolumeViewResource(@NonNull ImageButton imageButton, float volume) {
        if (volume > 0.0f) {
            if (fullScreen) {
                imageButton.setImageResource(R.drawable.fpu_ic_control_volume_on_land);
            } else {
                imageButton.setImageResource(R.drawable.fpu_ic_control_volume_on_port);
            }

        } else {
            if (fullScreen) {
                imageButton.setImageResource(R.drawable.fpu_ic_control_volume_off_land);
            } else {
                imageButton.setImageResource(R.drawable.fpu_ic_control_volume_off_port);
            }
        }
    }

    protected void updateBottomProgressView() {
        if (mBottomProgressView == null) {
            return;
        }
        if (isInShowState() && showBottomProgress && !controlViewShow) {
            mBottomProgressView.setVisibility(VISIBLE);
        } else {
            mBottomProgressView.setVisibility(GONE);
        }
    }

    public boolean isShowBottomProgress() {
        return showBottomProgress;
    }

    public void setShowBottomProgress(boolean showBottomProgress) {
        this.showBottomProgress = showBottomProgress;
        updateBottomProgressView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            gestureHelper.onUp(ev);
        }
        if (isInShowState()) {
            return gestureDetector.onTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            gestureHelper.onUp(ev);
        }
        if (isInShowState()) {
            return gestureDetector.onTouchEvent(ev);
        }
        return false;
    }

    private final Gesture.OnSlideChangedListener onSlideChangedListener = new Gesture.OnSlideChangedListener() {
        @Override
        public void onStartSlide(int slideType) {
            if (!isInShowState()) {
                return;
            }
            if (isShowing()) {
                hide();
            }
            switch (slideType) {
                case Gesture.SLIDE_TYPE_BRIGHTNESS:
                    if (slideBrightnessView != null) {
                        slideBrightnessView.setVisibility(View.VISIBLE);
                    }
                    break;
                case Gesture.SLIDE_TYPE_PROGRESS:
                    if (slideForwardView != null) {
                        slideForwardView.setVisibility(View.VISIBLE);
                    }
                    oldPosition = mPlayer.getCurrentPosition();
                    duration = mPlayer.getDuration();
                    if (slideForwardDuration != null) {
                        slideForwardDuration.setText(stringForTime(duration));
                    }
                    break;
                case Gesture.SLIDE_TYPE_VOLUME:
                    if (slideVolumeView != null) {
                        slideVolumeView.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }

        @Override
        public void onPercentChanged(int slideType, int percent) {
            if (!isInShowState()) {
                return;
            }
            switch (slideType) {
                case Gesture.SLIDE_TYPE_BRIGHTNESS:
                    if (slideBrightnessPercent != null) {
                        slideBrightnessPercent.setText(percent + "%");
                    }
                    if (slideBrightnessProgress != null) {
                        slideBrightnessProgress.setProgress(percent);
                    }
                    break;
                case Gesture.SLIDE_TYPE_PROGRESS:
                    newPosition = (long) ((percent * duration * 1.0f / 100) + oldPosition);
                    if (newPosition > duration) {
                        newPosition = duration;
                    } else if (newPosition < 0) {
                        newPosition = 0;
                    }

                    if (slideForwardPosition != null) {
                        slideForwardPosition.setText(stringForTime(newPosition));
                    }
                    if (slideForwardProgress != null) {
                        slideForwardProgress.setProgress((int) (newPosition * 1.0f / duration * 100));
                    }
                    if (slideForwardImage != null) {
                        slideForwardImage.setImageResource(percent < 0 ? R.drawable.fpu_ic_control_slide_rewind : R.drawable.fpu_ic_control_slide_forward);
                    }
                    break;
                case Gesture.SLIDE_TYPE_VOLUME:
                    if (slideVolumePercent != null) {
                        slideVolumePercent.setText(percent + "%");
                    }
                    if (slideVolumeProgress != null) {
                        slideVolumeProgress.setProgress(percent);
                    }
                    break;
            }
        }

        @Override
        public void onStopSlide(int slideType) {
            switch (slideType) {
                case Gesture.SLIDE_TYPE_BRIGHTNESS:
                    if (slideBrightnessView != null) {
                        slideBrightnessView.setVisibility(View.GONE);
                    }
                    break;
                case Gesture.SLIDE_TYPE_PROGRESS:
                    if (slideForwardView != null) {
                        slideForwardView.setVisibility(View.GONE);
                    }
                    seekTo(newPosition);
                    break;
                case Gesture.SLIDE_TYPE_VOLUME:
                    if (slideVolumeView != null) {
                        slideVolumeView.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    private final GestureDetector.SimpleOnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            gestureHelper.onDown(e);
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            if (isShowing()) {
                hide();
            } else {
                show();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            gestureHelper.onScroll(e1, e2, distanceX, distanceY);
            return true;
        }
    };

    public interface Gesture {
        float DEFAULT_SLIP_RATE = 0.8f;//滑动速率

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({SLIDE_TYPE_PROGRESS, SLIDE_TYPE_VOLUME, SLIDE_TYPE_BRIGHTNESS})
        @interface SlideType {
        }

        int SLIDE_TYPE_BRIGHTNESS = 1;
        int SLIDE_TYPE_PROGRESS = 2;
        int SLIDE_TYPE_VOLUME = 3;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({SHOW_TYPE_NONE, SHOW_TYPE_BRIGHTNESS, SHOW_TYPE_PROGRESS, SHOW_TYPE_VOLUME})
        @interface ShowType {
        }

        int SHOW_TYPE_NONE = 1;
        int SHOW_TYPE_BRIGHTNESS = 1 << 1;
        int SHOW_TYPE_PROGRESS = 1 << 2;
        int SHOW_TYPE_VOLUME = 1 << 3;

        interface OnSlideChangedListener {

            void onStartSlide(@SlideType int slideType);

            void onPercentChanged(@SlideType int slideType, int percent);

            void onStopSlide(@SlideType int slideType);
        }

        void setOnSlideChangedListener(OnSlideChangedListener onSlideChangedListener);

        void setShowType(@ShowType int showType);

        void onDown(MotionEvent ev);

        void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        void onUp(MotionEvent ev);
    }

    public interface Rotation {

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({AUTO_ROTATION_MODE_NONE, AUTO_ROTATION_MODE_ONLY_LANDSCAPE, AUTO_ROTATION_MODE_SYSTEM, AUTO_ROTATION_MODE_ALWAYS})
        @interface AutoRotation {
        }

        int AUTO_ROTATION_MODE_NONE = 0;
        int AUTO_ROTATION_MODE_ONLY_LANDSCAPE = 1;
        int AUTO_ROTATION_MODE_SYSTEM = 2;
        int AUTO_ROTATION_MODE_ALWAYS = 3;

        interface OnScreenChangedListener {
            void onScreenChanged(boolean portraitFullScreen);
        }

        default void setOnScreenChangedListener(OnScreenChangedListener onScreenChangedListener) {
        }


        default OnScreenChangedListener getOnScreenChangedListener() {
            return null;
        }

        void setAutoRotationMode(@AutoRotation int autoRotationMode);

        @AutoRotation
        int getAutoRotationMode();

        void setEnablePortraitFullScreen(boolean enablePortraitFullScreen);

        boolean isEnablePortraitFullScreen();
    }


}
