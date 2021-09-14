package com.chengfu.android.fuplayer.achieve.dj.video;

import com.chengfu.android.fuplayer.ui.DefaultProgressAdapter;

public class DJProgressAdapter extends DefaultProgressAdapter {

    @Override
    public boolean showSeekView() {
        return isCurrentWindowSeekable() && !isCurrentWindowLive();
    }

    @Override
    public boolean showPositionViewView() {
        return isCurrentWindowSeekable() && !isCurrentWindowLive();
    }

    @Override
    public boolean showDurationView() {
        return isCurrentWindowSeekable() && !isCurrentWindowLive();
    }

}
