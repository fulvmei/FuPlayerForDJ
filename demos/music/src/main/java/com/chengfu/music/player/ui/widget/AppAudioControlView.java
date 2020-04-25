package com.chengfu.music.player.ui.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.chengfu.android.fuplayer.achieve.dj.audio.PlaybackStateCompatExt;
import com.chengfu.android.fuplayer.achieve.dj.audio.widget.AudioControlView;
import com.chengfu.music.player.R;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class AppAudioControlView extends AudioControlView {

    ImageView background;
    ViewGroup content;
    ImageButton back;
    ImageButton menu;
    ImageButton playlist;
    ImageButton more;
    int contentPaddingTop;

    OnClickListener actionClickListener;

    ObjectAnimator rotaAnim;

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
        setContentPaddingTop(contentPaddingTop);

        back = findViewById(R.id.audio_controller_back);
        menu = findViewById(R.id.audio_controller_menu);
        playlist = findViewById(R.id.audio_controller_playlist);
        more = findViewById(R.id.audio_controller_more);

        setActionClickListener(actionClickListener);

//        RotateAnimation rotate  = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        LinearInterpolator lin = new LinearInterpolator();
//        rotate.setInterpolator(lin);
//        rotate.setDuration(4000);//设置动画持续周期
//        rotate.setRepeatCount(-1);//设置重复次数
//        rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
//        rotate.setStartOffset(0);//执行前的等待时间
//        icon.setAnimation(rotate);

        rotaAnim = ObjectAnimator.ofFloat(icon, "rotation", 0f, 360f);
        rotaAnim.setDuration(6000);
        rotaAnim.setInterpolator(new LinearInterpolator());
        rotaAnim.setRepeatCount(ValueAnimator.INFINITE);
    }

    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.app_audio_control_view, parent, false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (background != null) {
            background.getLayoutParams().height = h;
        }
    }

    public void setActionClickListener(OnClickListener actionClickListener) {
        this.actionClickListener = actionClickListener;
        if (back != null) {
            back.setOnClickListener(actionClickListener);
        }
        if (menu != null) {
            menu.setOnClickListener(actionClickListener);
        }
        if (playlist != null) {
            playlist.setOnClickListener(actionClickListener);
        }
        if (more != null) {
            more.setOnClickListener(actionClickListener);
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
    protected void updateIcon(@NonNull ImageView icon, @Nullable MediaDescriptionCompat description) {
        if (description == null) {
            return;
        }
        if (description.getIconBitmap() != null) {
            Glide.with(getContext().getApplicationContext())
                    .load(description.getIconBitmap())
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(200, 5)))
                    .into(background);

            Glide.with(getContext().getApplicationContext())
                    .load(description.getIconBitmap())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(icon);
            return;
        }

        String path = description.getIconUri() != null ? description.getIconUri().toString() : "";
        if (getTag() == null || !getTag().equals(path)) {
            setTag(path);
            DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(500).setCrossFadeEnabled(true).build();
            Glide.with(getContext().getApplicationContext())
                    .load(path)
                    .placeholder(background.getDrawable())
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(200, 5)))
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Glide.with(getContext().getApplicationContext())
                                    .load(path)
                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                    .into(icon);
                            return false;
                        }
                    })
                    .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                    .into(background);
        }

    }

    @Override
    protected void updateMetadata(MediaMetadataCompat metadata) {
        super.updateMetadata(metadata);
//        if (rotaAnim!=null){
//            rotaAnim.end();
//        }
    }

    @Override
    protected void updatePlaybackState(PlaybackStateCompat state) {
        super.updatePlaybackState(state);

        if (rotaAnim == null) {
            return;
        }
        if (state != null && PlaybackStateCompatExt.isPlaying(state)) {
            if(!rotaAnim.isStarted()){
                rotaAnim.start();
            }else {
                rotaAnim.resume();
            }
        } else {
            rotaAnim.pause();
        }
    }
}
