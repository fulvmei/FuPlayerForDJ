package com.chengfu.android.fuplayer.achieve.dj.demo.videofordj;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chengfu.android.fuplayer.achieve.dj.demo.videofordj.been.Video;

/**
 * 直播状态显示View(未开播、已结束，可回看)
 */
public class LiveStateView extends FrameLayout {

    private TextView live_hint;
    private Button btn;
    private int state;

    private OnLiveStateBtnClickListener onLiveStateBtnClickListener;

    public interface OnLiveStateBtnClickListener {
        void onLiveStateBtnClick(View view, int state);
    }

    public OnLiveStateBtnClickListener getOnLiveStateBtnClickListener() {
        return onLiveStateBtnClickListener;
    }

    public void setOnLiveStateBtnClickListener(OnLiveStateBtnClickListener onLiveStateBtnClickListener) {
        this.onLiveStateBtnClickListener = onLiveStateBtnClickListener;
    }

    public LiveStateView(@NonNull Context context) {
        this(context, null);
    }

    public LiveStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LiveStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        LayoutInflater.from(context).inflate(R.layout.media_layout_live_state, this);

        live_hint = findViewById(R.id.fu_state_live_hint);
        btn = findViewById(R.id.fu_state_live_btn);

        btn.setOnClickListener(view -> {
            if (onLiveStateBtnClickListener != null) {
                onLiveStateBtnClickListener.onLiveStateBtnClick(view, state);
            }
        });

        setState(Video.STATUS_STARTING);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        switch (state) {
            case Video.STATUS_SOON_START:
                live_hint.setText("直播未开始，请于指定时间进行观看");
                btn.setText("刷 新");
                break;
            case Video.STATUS_STARTED:
                live_hint.setText("直播已结束，请稍等片刻后进行回看");
                btn.setText("刷 新");
                break;
            case Video.STATUS_REVIEW:
                live_hint.setText("直播已结束，是否需要进行回看");
                btn.setText("回 看");
                break;
            default:
                live_hint.setText("");
                btn.setText("");
        }
    }
}
