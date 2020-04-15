package com.chengfu.music.player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;


import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.chengfu.android.fuplayer.achieve.dj.audio.MusicContract;
import com.chengfu.android.fuplayer.achieve.dj.audio.MusicService;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.AudioDatabase;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.MediaEntity;
import com.chengfu.music.player.ui.main.SectionsPagerAdapter;
import com.chengfu.music.player.util.MusicUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    //媒体浏览器
    private MediaBrowserCompat mMediaBrowser;
    //媒体控制器
    private MediaControllerCompat mMediaController;
    private MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            super.onConnected();
            Log.d(TAG, "onConnected");
            try {
                mMediaController = new MediaControllerCompat(MainActivity.this, mMediaBrowser.getSessionToken());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mMediaController.registerCallback(new MediaControllerCompat.Callback() {
                @Override
                public void onSessionReady() {
                    Log.d(TAG, "onSessionReady");
                }

                @Override
                public void onSessionDestroyed() {
                    Log.d(TAG, "onSessionDestroyed");
                }

                @Override
                public void onSessionEvent(String event, Bundle extras) {
                    Log.d(TAG, "onSessionEvent : event=" + event + ",extras=" + extras);
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    Log.d(TAG, "onPlaybackStateChanged : state=" + state);
                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    Log.d(TAG, "onMetadataChanged : metadata=" + metadata);
                    img.setImageBitmap(metadata.getBitmap("METADATA_KEY_DISPLAY_ICON"));

                }

                @Override
                public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
                    Log.d(TAG, "onQueueChanged : queue=" + queue);
                }

                @Override
                public void onQueueTitleChanged(CharSequence title) {
                    Log.d(TAG, "onQueueTitleChanged : title=" + title);
                }

                @Override
                public void onExtrasChanged(Bundle extras) {
                    Log.d(TAG, "onExtrasChanged : extras=" + extras);
                }

                @Override
                public void onAudioInfoChanged(MediaControllerCompat.PlaybackInfo info) {
                    Log.d(TAG, "onAudioInfoChanged : info=" + info);
                }

                @Override
                public void onCaptioningEnabledChanged(boolean enabled) {
                    Log.d(TAG, "onCaptioningEnabledChanged : enabled=" + enabled);
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {
                    Log.d(TAG, "onRepeatModeChanged : repeatMode=" + repeatMode);
                }

                @Override
                public void onShuffleModeChanged(int shuffleMode) {
                    Log.d(TAG, "onShuffleModeChanged : shuffleMode=" + shuffleMode);
                }

            });

            mMediaBrowser.subscribe(mMediaBrowser.getRoot(), new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    super.onChildrenLoaded(parentId, children);
                    Log.d(TAG, "onChildrenLoaded1");
                }

                @Override
                public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children, @NonNull Bundle options) {
                    super.onChildrenLoaded(parentId, children, options);
                    Log.d(TAG, "onChildrenLoaded2");
                }

                @Override
                public void onError(@NonNull String parentId) {
                    super.onError(parentId);
                    Log.d(TAG, "onError1");
                }

                @Override
                public void onError(@NonNull String parentId, @NonNull Bundle options) {
                    super.onError(parentId, options);
                    Log.d(TAG, "onError2");
                }
            });


        }

        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
            Log.d(TAG, "onConnectionFailed");
        }

        @Override
        public void onConnectionSuspended() {
            super.onConnectionSuspended();
            Log.d(TAG, "onConnectionFailed");
        }
    };

    View audioControlView;
    ImageView img;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                addList(mMediaController);
                return true;
            }
        });

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager2);
        viewPager2.setAdapter(sectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setText(SectionsPagerAdapter.TAB_TITLES[position]);
        });

        tabLayoutMediator.attach();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 0);
        }

        audioControlView = findViewById(R.id.audioControlView);

        audioControlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AudioPlayActivity.class);
                startActivity(intent);
            }
        });


        test();

        img = findViewById(R.id.img);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MediaEntity> list = MusicUtil.getNetMusics(MainActivity.this);
                AudioDatabase.getInstance(MainActivity.this).audioDao().insertAll(list);

            }
        }).start();

        findViewById(R.id.previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaController.getTransportControls().skipToPrevious();
            }
        });

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaController.getTransportControls().skipToNext();
            }
        });

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaController.getTransportControls().prepare();
                mMediaController.getTransportControls().play();
            }
        });

        findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaController.getTransportControls().pause();
            }
        });

        findViewById(R.id.fast_rewind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaController.getTransportControls().rewind();
            }
        });

        findViewById(R.id.fast_forward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaController.getTransportControls().fastForward();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                List<MediaEntity> list = MusicUtil.getMusics(MainActivity.this);
//                AudioDatabase.getInstance(MainActivity.this).audioDao().insertAll(list);
//
//            }
//        }).start();


    }

    private void test() {
        mMediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService.class), connectionCallback, null);
        mMediaBrowser.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaBrowser.disconnect();
    }

    public static void addList(MediaControllerCompat mediaController) {
        MediaDescriptionCompat music0 = new MediaDescriptionCompat.Builder()
                .setMediaId("0")
                .setTitle("贵州交通广播")
                .setSubtitle("未知来源")
                .setMediaUri(Uri.parse("https://qn-live.gzstv.com/icvkuzqj/yinyue.m3u8"))
                .setIconUri(Uri.parse("https://mstatic.gzstv.com/media/streams/images/2016/01/20/2ejVhB_USWMM_KsKg09p.jpg"))
                .build();

        MediaDescriptionCompat music1 = new MediaDescriptionCompat.Builder()
                .setMediaId("1")
                .setTitle("爱过的人我已不再拥有，错过的人是否可回首 . （治愈女声）")
                .setSubtitle("未知来源")
                .setMediaUri(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/1104/581b63392f6cb.mp3"))
                .setIconUri(Uri.parse("http://mpic.spriteapp.cn/crop/566x360/picture/2016/1104/581b633864635.jpg"))
                .build();
        Bundle bundle = new Bundle();
        ArrayList<MediaDescriptionCompat> list = new ArrayList<>();

        MediaDescriptionCompat music2 = new MediaDescriptionCompat.Builder()
                .setMediaId("2")
                .setTitle("3D潮音 - 3D环绕嗨曲")
                .setSubtitle("未知来源")
                .setMediaUri(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/0517/573b1240d0118.mp3"))
                .setIconUri(Uri.parse("http://mpic.spriteapp.cn/crop/566x360/picture/2016/0517/573b1240af3da.jpg"))
                .build();

        MediaDescriptionCompat music3 = new MediaDescriptionCompat.Builder()
                .setMediaId("3")
                .setTitle("电音House 耳机福利")
                .setSubtitle("未知来源")
                .setMediaUri(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/1108/5821463c8ea94.mp3"))
                .setIconUri(Uri.parse("http://mpic.spriteapp.cn/crop/566x360/picture/2016/0517/573b1240af3da.jpg"))
                .build();


        MediaDescriptionCompat music4 = new MediaDescriptionCompat.Builder()
                .setMediaId("4")
                .setTitle("感觉很放松，我最喜欢在我的兰博基尼上听这首歌，先不说，我换一下电池，还能再听几圈")
                .setSubtitle("未知来源")
                .setMediaUri(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/1123/5834c6bc02059.mp3"))
                .setIconUri(Uri.parse("http://mpic.spriteapp.cn/crop/566x360/picture/2016/0517/573b1240af3da.jpg"))
                .build();

        MediaDescriptionCompat music5 = new MediaDescriptionCompat.Builder()
                .setMediaId("5")
                .setTitle("一辈子有多少的来不及发现已失去最重要的东西 . （精神节奏）")
                .setSubtitle("未知来源")
                .setMediaUri(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/0703/5778246106dab.mp3"))
                .setIconUri(Uri.parse("http://mpic.spriteapp.cn/crop/566x360/picture/2016/0517/573b1240af3da.jpg"))
                .build();


        list.add(music5);
        list.add(music4);
        list.add(music3);
        list.add(music2);
        list.add(music1);
        list.add(music0);
//        Collections.shuffle(list);
        bundle.putParcelableArrayList(MusicContract.KEY_QUEUE_ITEMS, list);
        mediaController.sendCommand(MusicContract.COMMAND_ADD_QUEUE_ITEMS, bundle, null);
    }
}
