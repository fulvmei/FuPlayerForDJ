package com.chengfu.music.player;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.chengfu.android.fuplayer.achieve.dj.audio.AudioPlayClient;
import com.chengfu.music.player.ui.album.SongListAdapter;
import com.chengfu.music.player.ui.main.SectionsPagerAdapter;
import com.chengfu.music.player.ui.player.BottomDialog;
import com.chengfu.music.player.ui.player.PlayListAdapter;
import com.chengfu.music.player.ui.widget.SmallAudioControlView;
import com.chengfu.music.player.util.MusicUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AlbumActivity extends AppCompatActivity {
    public static final String TAG = "AlbumActivity";

    public static AudioPlayClient audioPlayClient;
    SmallAudioControlView audioControlView;
//    Toolbar toolbar;

    RecyclerView recyclerView;
    SongListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

//        toolbar = findViewById(R.id.toolbar);
//
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                return onOptionsItemSelected(item);
//            }
//        });

        recyclerView= findViewById(R.id.recyclerView);
        adapter=new SongListAdapter();
        adapter.setData(MusicUtil.getTestMedias(20,false));
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        audioControlView = findViewById(R.id.audioControlView);

        audioControlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlbumActivity.this, AudioPlayActivity.class);
                startActivity(intent);
            }
        });

        audioPlayClient = new AudioPlayClient(this);

        audioPlayClient.getConnected().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                audioControlView.setSessionToken(audioPlayClient.getMediaBrowser().getSessionToken());
            }
        });

        audioPlayClient.connect();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        BottomDialog dialog=new BottomDialog(this);
        dialog.setContentView(R.layout.dialog_bottom_sheet);
        dialog.show();
        return super.onOptionsItemSelected(item);
    }
}
