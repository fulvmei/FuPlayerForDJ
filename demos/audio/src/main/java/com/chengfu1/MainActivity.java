package com.chengfu1;

import android.net.Uri;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.chengfu.android.fuplayer.achieve.dj.audio.MusicContract;
import com.chengfu.android.fuplayer.achieve.dj.audio.widget.AudioControlView;
import com.chengfu.android.fuplayer.util.FuLog;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AudioControlView audioControlView;
    MediaSessionConnection mediaSessionConnection;
    ExoPlayer player;
    ConcatenatingMediaSource concatenatingMediaSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FuLog.DEBUG = true;
        audioControlView = findViewById(R.id.audioControlView);

//        mediaSessionConnection = new MediaSessionConnection(this, new ComponentName(this, MusicService.class));
//        mediaSessionConnection.isConnected.observe(this, aBoolean -> {
//            if (aBoolean) {
//                mediaSessionConnection.mediaController.registerCallback(new MyCallback());
//                audioControlView.setSessionToken(mediaSessionConnection.mediaBrowser.getSessionToken());
//            } else {
//                audioControlView.setSessionToken(null);
//            }
//        });
        player = new SimpleExoPlayer.Builder(/* context= */ this, new DefaultRenderersFactory(this))
                .build();
//        player.setPlayWhenReady(true);
        player.addListener(new Player.EventListener() {
//            @Override
//            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
//                Log.e("onTimelineChanged", "onTimelineChanged timeline=" + timeline.isEmpty());
//            }

            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {
                Log.e("fupalyer", "onTimelineChanged timeline=" + timeline.isEmpty() + ",reason=" + reason);
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Log.e("fupalyer", "onPositionDiscontinuity reason=" + reason);
            }
        });
        concatenatingMediaSource = new ConcatenatingMediaSource();
        MediaSource mediaSource1 = new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(MainActivity.this, "qqqq"))
                .createMediaSource(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/1104/581b63392f6cb.mp3"));

        MediaSource mediaSource2 = new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(MainActivity.this, "qqqq"))
                .createMediaSource(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/0703/5778246106dab.mp3"));

        MediaSource mediaSource3 = new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(MainActivity.this, "qqqq"))
                .createMediaSource(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/0517/573b1240d0118.mp3"));

        concatenatingMediaSource.addMediaSource(mediaSource1);
        concatenatingMediaSource.addMediaSource(mediaSource2);
        concatenatingMediaSource.addMediaSource(mediaSource3);
        player.prepare(concatenatingMediaSource);
        player.setPlayWhenReady(true);

        findViewById(R.id.m3u8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaSource mediaSource =new HlsMediaSource.Factory(new DefaultDataSourceFactory(MainActivity.this, "qqqq"))
                            .createMediaSource(Uri.parse("https://qn-live.gzstv.com/icvkuzqj/yinyue.m3u8"));
                    player.prepare(mediaSource);
            }
        });

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    player.next();
//                    player.prepare(concatenatingMediaSource);
                    return;
                }

                if (mediaSessionConnection.isConnected.getValue()) {
                    MediaDescriptionCompat music0 = new MediaDescriptionCompat.Builder()
                            .setMediaId("0")
                            .setTitle("贵州交通广播")
                            .setSubtitle("未知来源")
                            .setMediaUri(Uri.parse("https://qn-live.gzstv.com/icvkuzqj/yinyue.m3u8"))
                            .setIconUri(Uri.parse("https://mstatic.gzstv.com/media/streams/images/2016/01/20/2ejVhB_USWMM_KsKg09p.jpg"))
                            .build();

                    MediaDescriptionCompat music = new MediaDescriptionCompat.Builder()
                            .setMediaId("1")
                            .setTitle("爱过的人我已不再拥有，错过的人是否可回首 . （治愈女声）")
                            .setSubtitle("未知来源")
                            .setMediaUri(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/1104/581b63392f6cb.mp3"))
                            .setIconUri(Uri.parse("http://mpic.spriteapp.cn/crop/566x360/picture/2016/1104/581b633864635.jpg"))
                            .build();
                    Bundle bundle = new Bundle();
                    ArrayList<MediaDescriptionCompat> list = new ArrayList<>();

                    MediaDescriptionCompat music1 = new MediaDescriptionCompat.Builder()
                            .setMediaId("2")
                            .setTitle("3D潮音 - 3D环绕嗨曲")
                            .setSubtitle("未知来源")
                            .setMediaUri(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/0517/573b1240d0118.mp3"))
                            .setIconUri(Uri.parse("http://mpic.spriteapp.cn/crop/566x360/picture/2016/0517/573b1240af3da.jpg"))
                            .build();

                    list.add(music0);
                    list.add(music);
                    list.add(music1);
                    bundle.putParcelableArrayList(MusicContract.KEY_QUEUE_ITEMS, list);

//                        MediaMetadataCompat metadata = mediaSessionConnection.mediaController.getMetadata();
//                        if(metadata!=null
//                                &&metadata.getDescription()!=null
//                                &&metadata.getDescription().getMediaId()!=null
//                                &&metadata.getDescription().getMediaId().equals(music.getMediaId())){
//                            mediaSessionConnection.mediaController.getTransportControls().play();
//                        }else {
                    mediaSessionConnection.mediaController.sendCommand(MusicContract.COMMAND_SET_QUEUE_ITEMS, bundle, null);
                    mediaSessionConnection.mediaController.getTransportControls().playFromMediaId("0", null);
//                        }
                }
            }
        });

        findViewById(R.id.play2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
//                    MediaSource mediaSource = new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(MainActivity.this, "qqqq"))
//                            .createMediaSource(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/0517/573b1240d0118.mp3"));
//                    player.prepare(mediaSource);
                    player.previous();
                    return;
                }
                if (mediaSessionConnection.isConnected.getValue()) {
                    MediaDescriptionCompat music = new MediaDescriptionCompat.Builder()
                            .setMediaId("321313")
                            .setTitle("3D潮音 - 3D环绕嗨曲")
                            .setSubtitle("未知来源")
                            .setMediaUri(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/0517/573b1240d0118.mp3"))
                            .setIconUri(Uri.parse("http://mpic.spriteapp.cn/crop/566x360/picture/2016/0517/573b1240af3da.jpg"))
                            .build();
                    Bundle bundle = new Bundle();
                    ArrayList<MediaDescriptionCompat> list = new ArrayList<>();
                    list.add(music);
                    bundle.putParcelableArrayList(MusicContract.KEY_QUEUE_ITEMS, list);

                    MediaMetadataCompat metadata = mediaSessionConnection.mediaController.getMetadata();
                    if (metadata != null
                            && metadata.getDescription() != null
                            && metadata.getDescription().getMediaId() != null
                            && metadata.getDescription().getMediaId().equals(music.getMediaId())) {
                        mediaSessionConnection.mediaController.getTransportControls().play();
                    } else {
                        mediaSessionConnection.mediaController.sendCommand(MusicContract.COMMAND_SET_QUEUE_ITEMS, bundle, null);
                        mediaSessionConnection.mediaController.getTransportControls().playFromMediaId("2", null);
                    }
                }
            }
        });

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.getPlayWhenReady()) {
                    player.setPlayWhenReady(false);
                } else {
                    player.setPlayWhenReady(true);
                }
////                mediaSessionConnection.mediaController.getTransportControls().stop();
//                Intent intent = new Intent("chengfu.intent.action.ACTION_SESSION_ACTIVITY");
////                Intent it = new Intent("chengfu.intent.action.ACTION_SESSION_ACTIVITY");
//                Intent it = new Intent();
//                it.setClassName(getPackageName(), getPackageName() + ".TestActivity");
////                intent.setAction("chengfu.intent.action.ACTION_SESSION_ACTIVITY");
//                startActivity(it);
            }
        });

    }


    private class MyCallback extends MediaControllerCompat.Callback {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            Log.d("FuPlayer", "onMetadataChanged : metadata=" + metadata);
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
            Log.d("FuPlayer", "onQueueChanged : queue=" + queue);
        }

        @Override
        public void onQueueTitleChanged(CharSequence title) {
            super.onQueueTitleChanged(title);
            Log.d("FuPlayer", "onQueueTitleChanged : title=" + title);
        }

        @Override
        public void onExtrasChanged(Bundle extras) {
            super.onExtrasChanged(extras);
            Log.d("FuPlayer", "onExtrasChanged : extras=" + extras);
        }
    }
}
