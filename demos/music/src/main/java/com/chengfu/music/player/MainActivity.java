package com.chengfu.music.player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chengfu.android.fuplayer.achieve.dj.audio.AudioPlayClient;
import com.chengfu.android.fuplayer.achieve.dj.audio.MusicService;
import com.chengfu.music.player.ui.main.SectionsPagerAdapter;
import com.chengfu.music.player.ui.player.BottomDialog;
import com.chengfu.music.player.ui.widget.SmallAudioControlView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    public static AudioPlayClient audioPlayClient;
    SmallAudioControlView audioControlView;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
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

        audioControlView = findViewById(R.id.audioControlView);

        audioControlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AudioPlayActivity.class);
                startActivity(intent);
            }
        });

        audioPlayClient = new AudioPlayClient(this, AppMusicService.class);

        audioPlayClient.getConnected().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                audioControlView.setSessionToken(audioPlayClient.getMediaBrowser().getSessionToken());
            }
        });

        audioPlayClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        audioPlayClient.disconnect();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        BottomDialog dialog=new BottomDialog(this);
//        dialog.setContentView(R.layout.dialog_bottom_sheet);
//        dialog.show();
        Intent intent = new Intent(this, AudioPlayActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}
