package com.chengfu.music.player.ui.widget;

import android.content.Context;
import android.support.v4.media.MediaDescriptionCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chengfu.android.fuplayer.achieve.dj.audio.widget.AudioControlView;
import com.chengfu.music.player.R;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class AppAudioControlView extends AudioControlView {

    ImageView background;
    ViewGroup content;
    int contentPaddingTop;

    public AppAudioControlView(@NonNull Context context) {
        this(context, null);
    }

    public AppAudioControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppAudioControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        background = findViewById(R.id.audio_controller_background);
        background.setAlpha(0.85F);

        content = findViewById(R.id.audio_controller_content);

        if (content != null) {
            content.setPadding(0, contentPaddingTop, 0, 0);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (background != null) {
            background.getLayoutParams().height=h;
        }
    }

    public int getContentPaddingTop() {
        return contentPaddingTop;
    }

    public void setContentPaddingTop(int contentPaddingTop) {
        this.contentPaddingTop = contentPaddingTop;
        if (content != null) {
            content.setPadding(0, contentPaddingTop, 0, 0);
        }
    }

    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.app_audio_control_view, parent, false);
    }

    @Override
    protected void updateIcon(@NonNull ImageView icon, @Nullable MediaDescriptionCompat description) {
        super.updateIcon(icon, description);
        if (description == null) {
            return;
        }
        if (description.getIconBitmap() != null) {
            Glide.with(getContext())
                    .load(description.getIconBitmap())
                    .dontAnimate()
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(200, 3)))
                    .into(background);

            Glide.with(getContext())
                    .load(description.getIconBitmap())
                    .dontAnimate()
                    .circleCrop()
                    .centerCrop()
                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(255, 0)))
                    .into(icon);
            return;
        }

        String path = description.getIconUri() != null ? description.getIconUri().toString() : "";
        if (getTag() == null || !getTag().equals(path)) {
            setTag(path);
            Glide.with(getContext())
                    .load(path)
                    .dontAnimate()
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(200, 3)))
                    .into(background);

            Glide.with(getContext())
                    .load(path)
                    .circleCrop()
                    .centerCrop()
                    .dontAnimate()
                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(255, 0)))
                    .into(icon);
        }

    }
}
