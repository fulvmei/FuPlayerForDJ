package com.chengfu.music.player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;


import android.content.ComponentName;
import android.content.Intent;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.IMediaControllerCallback;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;

import com.chengfu.android.fuplayer.achieve.dj.audio.MusicService;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.AudioDatabase;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.MediaEntity;
import com.chengfu.music.player.ui.main.SectionsPagerAdapter;
import com.chengfu.music.player.util.MusicUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "p_test";
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
                    super.onSessionReady();
                }

                @Override
                public void onSessionDestroyed() {
                    super.onSessionDestroyed();
                }

                @Override
                public void onSessionEvent(String event, Bundle extras) {
                    super.onSessionEvent(event, extras);
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);
                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    super.onMetadataChanged(metadata);
                }

                @Override
                public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
                    super.onQueueChanged(queue);
                }

                @Override
                public void onQueueTitleChanged(CharSequence title) {
                    super.onQueueTitleChanged(title);
                }

                @Override
                public void onExtrasChanged(Bundle extras) {
                    super.onExtrasChanged(extras);
                }

                @Override
                public void onAudioInfoChanged(MediaControllerCompat.PlaybackInfo info) {
                    super.onAudioInfoChanged(info);
                }

                @Override
                public void onCaptioningEnabledChanged(boolean enabled) {
                    super.onCaptioningEnabledChanged(enabled);
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {
                    super.onRepeatModeChanged(repeatMode);
                }

                @Override
                public void onShuffleModeChanged(int shuffleMode) {
                    super.onShuffleModeChanged(shuffleMode);
                }

                @Override
                public void binderDied() {
                    super.binderDied();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MediaEntity> list = MusicUtil.getNetMusics(MainActivity.this);
                AudioDatabase.getInstance(MainActivity.this).audioDao().insertAll(list);

            }
        }).start();


        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaController.getTransportControls().skipToNext();
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
}
