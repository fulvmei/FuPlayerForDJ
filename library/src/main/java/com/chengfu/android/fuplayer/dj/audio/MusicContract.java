package com.chengfu.android.fuplayer.dj.audio;

import android.net.Uri;
import android.provider.BaseColumns;

public class MusicContract {
    static final String AUTHORITY = "com.chengfu.android.media.provider.MusicProvider";

    public  static final String COMMAND_SET_QUEUE_ITEMS="command_set_queue_items";

    public  static final String COMMAND_CLEAR_QUEUE_ITEMS="command_clear_queue_items";

    public  static final String KEY_QUEUE_ITEMS="key_queue_items";

    public  static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Columns extends BaseColumns {

        String DISPLAY_NAME = "display_name";

        String ABSOLUTE_PATH = "absolute_path";

        String SIZE = "size";
    }

    static final String[] PROJECTION_ALL = new String[]{
            Columns._ID,
            Columns.DISPLAY_NAME,
            Columns.ABSOLUTE_PATH,
            Columns.SIZE
    };
}
