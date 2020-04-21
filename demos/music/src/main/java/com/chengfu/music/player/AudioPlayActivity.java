package com.chengfu.music.player;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chengfu.android.fuplayer.achieve.dj.audio.AudioPlayClient;
import com.chengfu.android.fuplayer.achieve.dj.audio.widget.AudioControlView;
import com.chengfu.music.player.ui.main.CurrentPlayListAdapter;
import com.chengfu.music.player.ui.player.RecentListFragment;
import com.chengfu.music.player.ui.widget.AppAudioControlView;
import com.chengfu.music.player.util.MusicUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;

import java.util.List;

public class AudioPlayActivity extends AppCompatActivity {
    public static final String TAG = "AudioPlayActivity";
    AudioPlayClient audioPlayClient;
    RecyclerView recyclerView;
    CurrentPlayListAdapter adapter;
    AppAudioControlView audioControlView;
//    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_play);

//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        ImmersionBar.with(this)
//                .statusBarColorInt(Color.TRANSPARENT)
//                .navigationBarColorInt(Color.WHITE)
//                .transparentStatusBar()
//                .statusBarDarkFont(true, 0.3f)
//                .fitsSystemWindows(true)
//                .hideBar(BarHide.FLAG_SHOW_BAR)
//                .init();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(this::onOptionsItemSelected);
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        audioControlView = findViewById(R.id.audioControlView);

        audioControlView.setContentPaddingTop(100);

        adapter = new CurrentPlayListAdapter();

        adapter.setOnItemClickListener(new CurrentPlayListAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, MediaSessionCompat.QueueItem item) {
//                audioPlayClient.playFromMediaId(item.getDescription().getMediaId());
                audioPlayClient.playFromItemId(item.getQueueId());
            }
        });

        recyclerView.setAdapter(adapter);

        audioPlayClient = new AudioPlayClient(this);
        audioPlayClient.connect();

        audioPlayClient.getConnected().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean connected) {
                if (connected) {
                    audioControlView.setSessionToken(audioPlayClient.getMediaBrowser().getSessionToken());
                    adapter.setData(audioPlayClient.getMediaController().getQueue());
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
        } else if (id == R.id.recent) {
            RecentListFragment fragment = RecentListFragment.newInstance();
            fragment.show(getSupportFragmentManager(), "RecentListFragment");
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
