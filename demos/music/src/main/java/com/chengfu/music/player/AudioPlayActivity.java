package com.chengfu.music.player;

import android.content.ComponentName;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.NestedScrollingChild;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chengfu.android.fuplayer.achieve.dj.audio.AudioPlayManager;
import com.chengfu.android.fuplayer.achieve.dj.audio.MusicService;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.vo.CurrentPlay;
import com.chengfu.android.fuplayer.achieve.dj.audio.util.ConverterUtil;
import com.chengfu.music.player.ui.main.CurrentPlayListAdapter;
import com.chengfu.music.player.ui.widget.AppAudioControlView;

import java.util.List;

public class AudioPlayActivity extends AppCompatActivity {
    public static final String TAG = "AudioPlayActivity";
    //媒体浏览器
    private MediaBrowserCompat mMediaBrowser;
    //媒体控制器
    private MediaControllerCompat mMediaController;
    private MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            super.onConnected();
            Log.d(TAG, "onConnected");
            audioControlView.setSessionToken(mMediaBrowser.getSessionToken());
            try {
                mMediaController = new MediaControllerCompat(AudioPlayActivity.this, mMediaBrowser.getSessionToken());
                mMediaController.registerCallback(new MediaControllerCompatCallback());
                adapter.setData(mMediaController.getQueue());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
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


    RecyclerView recyclerView;
    CurrentPlayListAdapter adapter;
    AppAudioControlView audioControlView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_play);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        audioControlView = findViewById(R.id.audioControlView);

        adapter = new CurrentPlayListAdapter();

        recyclerView.setAdapter(adapter);

//        AudioPlayManager.getCurrentPlayList(this).observe(this, new Observer<List<CurrentPlay>>() {
//            @Override
//            public void onChanged(List<CurrentPlay> currentPlays) {
//                adapter.setData(currentPlays);
//            }
//        });

        test();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MainActivity.addList(mMediaController);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaBrowser.disconnect();
    }

    private void test() {
        mMediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService.class), connectionCallback, null);
        mMediaBrowser.connect();
    }

    class MediaControllerCompatCallback extends MediaControllerCompat.Callback {

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
            adapter.setData(queue);
            Log.d(TAG, "onQueueChanged : queue=" + queue);
        }
    }

}
