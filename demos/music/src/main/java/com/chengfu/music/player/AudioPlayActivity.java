package com.chengfu.music.player;

import android.content.ComponentName;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
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

import com.chengfu.android.fuplayer.achieve.dj.audio.AudioPlayClient;
import com.chengfu.android.fuplayer.achieve.dj.audio.AudioPlayManager;
import com.chengfu.android.fuplayer.achieve.dj.audio.MusicService;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.vo.CurrentPlay;
import com.chengfu.android.fuplayer.achieve.dj.audio.util.ConverterUtil;
import com.chengfu.music.player.ui.main.CurrentPlayListAdapter;
import com.chengfu.music.player.ui.widget.AppAudioControlView;
import com.chengfu.music.player.util.MusicUtil;

import java.util.List;

public class AudioPlayActivity extends AppCompatActivity {
    public static final String TAG = "AudioPlayActivity";
    AudioPlayClient audioPlayClient;
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

        audioPlayClient = new AudioPlayClient(this);
        audioPlayClient.connect();

        audioPlayClient.getConnected().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean connected) {
                if (connected) {
                    audioControlView.setSessionToken(audioPlayClient.getMediaBrowser().getSessionToken());
                    adapter.setData( audioPlayClient.getMediaController().getQueue());
                    audioPlayClient.getMediaController().registerCallback(new MediaControllerCompatCallback());

                } else {
                    audioControlView.setSessionToken(null);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.set_list) {
            audioPlayClient.setPlayList(MusicUtil.getTestMedias(5, false), true);
        } else if (id == R.id.add_one) {
            audioPlayClient.appendPlayList(MusicUtil.getTestMedias(1, false), true);
        } else if (id == R.id.add_two) {
            audioPlayClient.appendPlayList(MusicUtil.getTestMedias(2, false), true);
        } else if (id == R.id.add_clear) {
            audioPlayClient.clearPlayList();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioPlayClient.disconnect();
    }


    class MediaControllerCompatCallback extends MediaControllerCompat.Callback {

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
            adapter.setData(queue);
            Log.d(TAG, "onQueueChanged : queue=" + queue);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            if (state == null) {
                return;
            }
            long activeQueueItemId = state.getActiveQueueItemId();
            adapter.setActiveQueueItemId(activeQueueItemId);
        }
    }

}
