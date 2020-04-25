package com.chengfu.music.player;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chengfu.android.fuplayer.achieve.dj.audio.AudioPlayClient;
import com.chengfu.music.player.ui.player.AudioPlayViewModel;
import com.chengfu.music.player.ui.player.PlayListAdapter;
import com.chengfu.music.player.ui.player.PlayListFragment;
import com.chengfu.music.player.ui.player.RecentListFragment;
import com.chengfu.music.player.ui.player.TestFragment;
import com.chengfu.music.player.ui.widget.AppAudioControlView;
import com.chengfu.music.player.util.MusicUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gyf.barlibrary.ImmersionBar;

import java.util.List;

public class AudioPlayActivity extends AppCompatActivity {
    public static final String TAG = "AudioPlayActivity";
    AudioPlayClient audioPlayClient;
    RecyclerView recyclerView;
    PlayListAdapter adapter;
    AppAudioControlView audioControlView;
    //    Toolbar toolbar;
    AudioPlayViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_play);

        viewModel = ViewModelProviders.of(this).get(AudioPlayViewModel.class);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        audioControlView = findViewById(R.id.audioControlView);

        audioControlView.setContentPaddingTop(ImmersionBar.getStatusBarHeight(this));

        audioControlView.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.audio_controller_back) {
                    finish();
                } else if (id == R.id.audio_controller_menu) {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(item -> onOptionsItemSelected(item));
                } else if (id == R.id.audio_controller_playlist) {
                    PlayListFragment playListFragment = PlayListFragment.newInstance();
                    playListFragment.setStyle(PlayListFragment.STYLE_NORMAL,R.style.BottomDialogTheme);
                    playListFragment.show(getSupportFragmentManager(), "playListFragment");
                }else if (id == R.id.audio_controller_more) {
//                    TestFragment fragment=TestFragment.newInstance();
//                    fragment.setStyle(TestFragment.STYLE_NORMAL,R.style.BottomDialogTheme);
//                    PlayListFragment fragment = PlayListFragment.newInstance();
//                    fragment.show(getSupportFragmentManager(),"test");
//                    getSupportFragmentManager().beginTransaction().add(fragment,"test").commit();
//                    PlayListFragment playListFragment = PlayListFragment.newInstance();
//                    getSupportFragmentManager().beginTransaction().add(R.id.f,playListFragment).commit();

                    RecentListFragment fragment = RecentListFragment.newInstance();
//                    fragment.setStyle(PlayListFragment.STYLE_NORMAL,R.style.BottomDialogTheme);
                    fragment.show(getSupportFragmentManager(), "RecentListFragment");
                }
            }
        });

        adapter = new PlayListAdapter();

        adapter.setOnItemClickListener(new PlayListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, MediaSessionCompat.QueueItem item) {
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
                    viewModel.setActiveQueueItemId(audioPlayClient.getMediaController().getPlaybackState().getActiveQueueItemId());
                    viewModel.setPlayList(audioPlayClient.getMediaController().getQueue());
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
            viewModel.setPlayList(queue);
            Log.d(TAG, "onQueueChanged : queue=" + queue);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            if (state == null) {
                return;
            }
            long activeQueueItemId = state.getActiveQueueItemId();
            viewModel.setActiveQueueItemId(activeQueueItemId);
            adapter.setActiveQueueItemId(activeQueueItemId);
        }
    }

}
