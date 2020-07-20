package com.chengfu.android.fuplayer.achieve.dj.audio;

public class MusicContract {

    public static final String ACTION_SESSION_ACTIVITY = "chengfu.intent.action.ACTION_SESSION_ACTIVITY";

    public static final int DEFAULT_QUEUE_ADD_INDEX = 0;

    public static final int REQUEST_CODE_SESSION_ACTIVITY = 100;

    static final String AUTHORITY = "com.chengfu.android.media.provider.MusicProvider";

    public static final String COMMAND_ADD_TO_TO_FRONT_OF_CURRENT_PLAY = "command_add_to_to_front_of_current_play";

    public static final String COMMAND_ADD_AFTER_CURRENT_PLAY = "command_add_after_current_play";

    public static final String COMMAND_SET_QUEUE_ITEMS = "command_set_queue_items";

    public static final String COMMAND_ADD_QUEUE_ITEMS = "command_add_queue_items";

    public static final String COMMAND_CLEAR_QUEUE_ITEMS = "command_clear_queue_items";

    public static final String COMMAND_SET_TIMING_OFF_MODE = "command_set_timing_off_mode";

    public static final String KEY_TIMING_OFF = "key_timing_off";

    public static final String KEY_QUEUE_ITEM = "key_queue_item";

    public static final String KEY_QUEUE_ITEMS = "key_queue_items";

    public static final String KEY_QUEUE_ITEMS_INDEX = "key_queue_items_index";

    public static final String KEY_DEFAULT_PARENT_ID = "key_default_parent_id";

    public static final String KEY_MEDIA_DESCRIPTION_EXTRAS = "key_media_description_extras";

    public static final String KEY_PARENT_ID = "key_parent_id";

    public static final String KEY_MEDIA_EXTRA_ID = "key_media_extra_id";

    public static final String KEY_MEDIA_EXTRA_CTYPE = "key_media_extra_ctype";

    public static final String EVENT_CLOSED = "event_closed";
}
