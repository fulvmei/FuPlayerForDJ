package com.chengfu.android.fuplayer.dj.demo;

import android.content.ComponentName;
import android.net.Uri;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.chengfu.android.fuplayer.dj.audio.MusicContract;
import com.chengfu.android.fuplayer.dj.audio.MusicService;
import com.chengfu.android.fuplayer.dj.audio.widget.AudioControlView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    AudioControlView audioControlView;
    MediaSessionConnection mediaSessionConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioControlView = findViewById(R.id.audioControlView);

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaSessionConnection mediaSessionConnection = new MediaSessionConnection(MainActivity.this, new ComponentName(MainActivity.this, MusicService.class));
                mediaSessionConnection.isConnected.observe(MainActivity.this, aBoolean -> {
                    if (aBoolean) {
                        MediaDescriptionCompat music = new MediaDescriptionCompat.Builder()
                                .setMediaId("12312313")
                                .setSubtitle("未知来源")
                                .setTitle("爱过的人我已不再拥有，错过的人是否可回首 . （治愈女声）")
                                .setMediaUri(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/1104/581b63392f6cb.mp3"))
                                .setIconUri(Uri.parse("http://mpic.spriteapp.cn/crop/566x360/picture/2016/1104/581b633864635.jpg"))
                                .build();
                        Bundle bundle = new Bundle();
                        ArrayList<MediaDescriptionCompat> list = new ArrayList<>();
                        list.add(music);
                        bundle.putParcelableArrayList(MusicContract.KEY_QUEUE_ITEMS, list);
                        mediaSessionConnection.mediaController.sendCommand(MusicContract.COMMAND_SET_QUEUE_ITEMS, bundle, null);
                        mediaSessionConnection.mediaController.getTransportControls().playFromMediaId(music.getMediaId(), null);
                    }
                });
            }
        });

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaSessionConnection.mediaController.getTransportControls().stop();
            }
        });


        mediaSessionConnection = new MediaSessionConnection(this, new ComponentName(this, MusicService.class));
        mediaSessionConnection.isConnected.observe(this, aBoolean -> {
            if (aBoolean) {
                audioControlView.setSessionToken(mediaSessionConnection.mediaBrowser.getSessionToken());
            } else {
                audioControlView.setSessionToken(null);
            }
        });
    }
}
