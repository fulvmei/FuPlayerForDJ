package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.media.MediaBrowserServiceCompat;

import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.achieve.dj.R;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.vo.CurrentPlay;
import com.chengfu.android.fuplayer.achieve.dj.audio.notification.AudioNotificationManager;
import com.chengfu.android.fuplayer.achieve.dj.audio.util.ConverterUtil;
import com.chengfu.android.fuplayer.ui.PlayerNotificationManager;
import com.chengfu.android.fuplayer.util.FuLog;
import com.google.android.exoplayer2.C;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import static com.chengfu.android.fuplayer.achieve.dj.audio.NotificationBuilder.NOW_PLAYING_NOTIFICATION;

public class MusicService extends MediaBrowserServiceCompat implements LifecycleOwner {
    public static final String TAG = "MusicService";

    private LifecycleRegistry lifecycle;
    private boolean isForegroundService;

    private MediaSessionCompat mediaSession;

    private BecomingNoisyReceiver becomingNoisyReceiver;

    private MediaSessionPlayer mediaSessionPlayer;
    private QueueAdapter queueAdapter;

    private AudioNotificationManager audioNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        FuLog.DEBUG = true;
        FuLog.d(TAG, "onCreate");
        lifecycle = new LifecycleRegistry(this);
        lifecycle.setCurrentState(Lifecycle.State.RESUMED);

        mediaSession = new MediaSessionCompat(this, TAG);
        mediaSession.setActive(true);

        setSessionToken(mediaSession.getSessionToken());

        audioNotificationManager = new AudioNotificationManager(this, mediaSession);

        Picasso picasso = null;
        try {
            picasso = Picasso.get();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        if (picasso == null) {
            Picasso.setSingletonInstance(new Picasso.Builder(getApplicationContext()).build());
        }

        audioNotificationManager.setNotificationListener(new AudioNotificationManager.NotificationListener() {

            @Nullable
            @Override
            public PendingIntent createCurrentContentIntent(MediaDescriptionCompat description) {
                FuLog.d(TAG, "createCurrentContentIntent  description=" + description);
                Intent sessionIntent = new Intent();
                sessionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sessionIntent.putExtra(MusicContract.KEY_MEDIA_DESCRIPTION_EXTRAS, description != null ? description.getExtras() : null);
                ComponentName componentName = new ComponentName(MusicService.this, getApplication().getPackageName() + ".FuSessionActivity");
                sessionIntent.setComponent(componentName);
                return PendingIntent.getActivity(MusicService.this, MusicContract.REQUEST_CODE_SESSION_ACTIVITY, sessionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            @Nullable
            @Override
            public Bitmap getCurrentLargeIcon(MediaDescriptionCompat description, AudioNotificationManager.BitmapCallback callback) {
                if (description == null) {
                    return BitmapFactory.decodeResource(getResources(), getApplicationInfo().icon);
                }
                if (description.getIconBitmap() != null) {
                    return description.getIconBitmap();
                }
                if (description.getIconUri() == null) {
                    return BitmapFactory.decodeResource(getResources(), getApplicationInfo().icon);
                }
                Picasso.get().load(description.getIconUri())
                        .centerCrop()
                        .resize(336, 336)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                callback.onBitmap(bitmap);
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                callback.onBitmap(BitmapFactory.decodeResource(getResources(), getApplicationInfo().icon));
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                callback.onBitmap(BitmapFactory.decodeResource(getResources(), getApplicationInfo().icon));
                            }
                        });
                return BitmapFactory.decodeResource(getResources(), getApplicationInfo().icon);
            }

            @Override
            public void onNotificationPosted(int notificationId, Notification notification) {
                FuLog.d(TAG, "onNotificationPosted  notification=" + notification);
                if (!isForegroundService) {
                    becomingNoisyReceiver.register();

                    ContextCompat.startForegroundService(
                            getApplicationContext(),
                            new Intent(getApplicationContext(), MusicService.class));

                    startService(new Intent(getApplicationContext(), MusicService.class));
                    startForeground(notificationId, notification);
                    isForegroundService = true;
                }
            }

            @Override
            public void onNotificationCancelled() {
                FuLog.d(TAG, "onNotificationCancelled");
                if (isForegroundService) {
                    becomingNoisyReceiver.unregister();
                    stopForeground(true);
                    isForegroundService = false;
                }
            }
        });

        mediaSessionPlayer = new MediaSessionPlayer(this, mediaSession);
        queueAdapter = new QueueAdapterImpl();
        mediaSessionPlayer.setQueueAdapter(queueAdapter);

        AudioPlayManager.getCurrentPlayList(this).observe(this, new Observer<List<CurrentPlay>>() {
            @Override
            public void onChanged(List<CurrentPlay> currentPlays) {
                Log.d("fff", "onChanged size" + currentPlays.size());
                queueAdapter.addAll(ConverterUtil.currentPlayListToQueueItemList(currentPlays));
            }
        });

        try {
            becomingNoisyReceiver =
                    new BecomingNoisyReceiver(this, mediaSession.getSessionToken());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        FuLog.d(TAG, "onGetRoot : clientPackageName=" + clientPackageName + ",clientUid=" + clientUid + ",rootHints=" + rootHints);
        return new BrowserRoot("root", null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        FuLog.d(TAG, "onLoadChildren : parentId=" + parentId);
//        LiveData<List<CurrentPlay>> currentPlayList = AudioPlayManager.getCurrentPlayList(this);
//        currentPlayList.observe(this, new Observer<List<CurrentPlay>>() {
//            @Override
//            public void onChanged(List<CurrentPlay> currentPlays) {
//                FuLog.d(TAG, "onLoadChildren : onChanged=");
//                result.sendResult(new ArrayList<>());
//            }
//        });
        result.sendResult(new ArrayList<>());
//        result.detach();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FuLog.d(TAG, "onDestroy");
        lifecycle.setCurrentState(Lifecycle.State.DESTROYED);
        mediaSessionPlayer.release();
        mediaSession.setActive(false);
        mediaSession.release();
        if (becomingNoisyReceiver != null) {
            becomingNoisyReceiver.unregister();
        }
        audioNotificationManager.onDestroy();
    }

    private static class BecomingNoisyReceiver extends BroadcastReceiver {
        private Context context;
        private MediaSessionCompat.Token sessionToken;
        private IntentFilter noisyIntentFilter;
        private MediaControllerCompat controller;
        private boolean registered = false;

        public BecomingNoisyReceiver(Context context,
                                     MediaSessionCompat.Token sessionToken) throws RemoteException {
            this.context = context;
            this.sessionToken = sessionToken;

            noisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            controller = new MediaControllerCompat(context, sessionToken);
        }

        public void register() {
            if (!registered) {
                context.registerReceiver(this, noisyIntentFilter);
                registered = true;
            }
        }

        public void unregister() {
            if (registered) {
                context.unregisterReceiver(this);
                registered = false;
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            FuLog.d(TAG, "onReceive : context=" + context + ",intent=" + intent);
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                controller.getTransportControls().pause();
            }
        }
    }

//    private class NotificationBroadcastReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            stopForeground(true);
//            if (mediaSessionPlayer != null) {
//                mediaSessionPlayer.stop();
////                mediaSessionPlayer.setPlayList(new ArrayList<>());
//            }
//            if (mediaSession != null && mediaSession.isActive()) {
////                mediaSession.sendSessionEvent();
//            }
//        }
//    }
}
