package com.chengfu.music.player.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.chengfu.android.fuplayer.achieve.dj.audio.MusicContract;
import com.chengfu.android.fuplayer.achieve.dj.audio.widget.AudioControlView;
import com.chengfu.music.player.R;
import com.squareup.picasso.Picasso;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class SmallAudioControlView extends AudioControlView {

    private boolean manualHide;//手动隐藏标识

    private OnNavigateListener onNavigateListener;

    public interface OnNavigateListener {
        boolean onNavigate(View v, Bundle extras);
    }

    public OnNavigateListener getOnNavigateListener() {
        return onNavigateListener;
    }

    public void setOnNavigateListener(OnNavigateListener onNavigateListener) {
        this.onNavigateListener = onNavigateListener;
    }

    public SmallAudioControlView(@NonNull Context context) {
        this(context, null);
    }

    public SmallAudioControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmallAudioControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        findViewById(R.id.audio_controller_close).setOnClickListener(v -> {
            if (getController() != null) {
                getController().sendCommand(MusicContract.COMMAND_CLEAR_QUEUE_ITEMS, null, null);
            }
        });
    }

    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.small_audio_control_view, parent, false);
    }

    @Override
    protected void updateMetadata(MediaMetadataCompat metadata) {
        super.updateMetadata(metadata);

        if (seek != null) {
            if (controller == null || controller.getMetadata() == null) {
                return;
            }
            Bundle extras = controller.getMetadata().getBundle();
        }
    }

    public void manualShow() {
        manualHide = false;
        if (isInShowState(controller != null ? controller.getPlaybackState() : null)) {
            show();
        }
    }

    public void manualHide() {
        manualHide = true;
        hide();
    }

    @Override
    protected boolean isInShowState(PlaybackStateCompat state) {
        if (manualHide) {
            return false;
        }
        return state != null && state.getState() != PlaybackStateCompat.STATE_NONE;
    }

    @Override
    protected void updateIcon(@NonNull ImageView icon, @Nullable MediaDescriptionCompat description) {
        if (description == null) {
            return;
        }
        if (description.getIconBitmap() != null) {
            Glide.with(getContext().getApplicationContext())
                    .load(description.getIconBitmap())
                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(dp2px(6.67f), 0, RoundedCornersTransformation.CornerType.ALL)))
                    .into(icon);
            return;
        }

        String path = description.getIconUri() != null ? description.getIconUri().toString() : "";
        if (getTag() == null || !getTag().equals(path)) {
            setTag(path);
            RequestOptions requestOptions = RequestOptions
                    .bitmapTransform(new CenterCropRoundedCornersTransformation(dp2px(6.67f), 0, CenterCropRoundedCornersTransformation.CornerType.ALL));
            Glide.with(getContext().getApplicationContext())
                    .load(path)
                    .apply(requestOptions)
                    .into(icon);
        }

    }

    @Override
    protected void setViewEnabled(View view, boolean enabled) {
        if (view == null) {
            return;
        }
        view.setEnabled(enabled);
        view.setAlpha(enabled ? 1f : 0.4f);
    }


    public int dp2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);

    }
}
