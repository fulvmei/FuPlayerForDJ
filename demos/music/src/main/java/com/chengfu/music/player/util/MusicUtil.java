package com.chengfu.music.player.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.media.MediaDescriptionCompat;

import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.MediaEntity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MusicUtil {
    public static List<MediaEntity> getMusics(Context context) {
        ArrayList<MediaEntity> musics = new ArrayList<>();
        Cursor c = null;
        try {
            c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));// 路径
                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); // 歌曲名
                String album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)); // 专辑
                String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)); // 作者
                long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));// 大小
                int duration = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));// 时长
                int id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));// 歌曲的id
                // int albumId = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                MediaEntity music = new MediaEntity(id + "");
                music.title = name;
                music.subTitle = artist + "-" + album;
                music.mediaUri = Uri.parse(paths[0]);
                musics.add(music);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return musics;
    }

    static String[] paths = {
            "http://mvoice.spriteapp.cn/voice/2016/0517/573b1240d0118.mp3",
            "https://qn-live.gzstv.com/icvkuzqj/yinyue.m3u8",
            "http://mvoice.spriteapp.cn/voice/2016/1108/5821463c8ea94.mp3",
//            "http://mvoice.spriteapp.cn/voice/2016/1104/581b63392f6cb.mp3",
//            "https://qn-live.gzstv.com/icvkuzqj/yinyue.m3u8",
//            "https://qn-live.gzstv.com/icvkuzqj/yinyue.m3u8",
//            "https://qn-live.gzstv.com/icvkuzqj/yinyue.m3u8",
//            "https://qn-live.gzstv.com/icvkuzqj/yinyue.m3u8",
//            "https://qn-live.gzstv.com/icvkuzqj/yinyue.m3u8",
//            "https://qn-live.gzstv.com/icvkuzqj/yinyue.m3u8",
//            "http://mvoice.spriteapp.cn/voice/2016/1123/5834c6bc02059.mp3",
//            "http://mvoice.spriteapp.cn/voice/2016/0703/5778246106dab.mp3"
    };
    static String[] titles = {"3D潮音 - 3D环绕嗨曲",
            "贵州音乐广播",
            "电音House 耳机福利",};

    static String[] imgs = {"http://mpic.spriteapp.cn/crop/566x360/picture/2016/0517/573b1240af3da.jpg",
            "https://mstatic.gzstv.com/media/streams/images/2016/01/20/2ejVhB_USWMM_KsKg09p.jpg",
            "http://mpic.spriteapp.cn/crop/566x360/picture/2016/1104/581b633864635.jpg",};

    public static List<MediaEntity> getNetMusics(Context context) {
        ArrayList<MediaEntity> musics = new ArrayList<>();
        Bitmap[] bitmaps = getBitmaps(context);
        for (int i = 0; i < titles.length; i++) {
            MediaEntity entity = new MediaEntity("id_" + i);
            entity.mediaUri = Uri.parse(paths[i % titles.length]);
            entity.title = titles[i % titles.length];
            entity.subTitle = "未知专辑";
            entity.iconUri = Uri.parse(imgs[i % titles.length]);
//            entity.icon=bitmaps[i % titles.length];

            musics.add(entity);
        }

        return musics;
    }

    public static Bitmap[] getBitmaps(Context context) {
        AssetManager assetManager = context.getAssets();
        Bitmap bitmap1 = null, bitmap2 = null, bitmap3 = null;
        try {
            InputStream inputStream1 = assetManager.open("img1.png");
            bitmap1 = BitmapFactory.decodeStream(inputStream1);

            InputStream inputStream2 = assetManager.open("img2.png");
            bitmap2 = BitmapFactory.decodeStream(inputStream2);

            InputStream inputStream3 = assetManager.open("img3.png");
            bitmap3 = BitmapFactory.decodeStream(inputStream3);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Bitmap[]{bitmap1, bitmap2, bitmap3};
    }


    public static ArrayList<MediaDescriptionCompat> getTestMedias(int count, boolean shuffle) {
        MediaDescriptionCompat music0 = new MediaDescriptionCompat.Builder()
                .setMediaId("1")
                .setTitle("神吐槽 | 什么都懂")
                .setSubtitle("雷畅")
                .setMediaUri(Uri.parse("https://sel.gzstv.com/recordings/z1.icvkuzqj.dushi/1616972412_1616979612.m3u8"))
                .setIconUri(Uri.parse("https://mstatic.gzstv.com/media/streams/audio/images/2020/07/03/t_cFQHXrQDMxCe_150x150_exT5XhbJ_2x.jpg"))
                .build();

        MediaDescriptionCompat music1 = new MediaDescriptionCompat.Builder()
                .setMediaId("0")
                .setTitle("贵州交通广播")
                .setSubtitle("未知来源")
                .setMediaUri(Uri.parse("https://qn-live.gzstv.com/icvkuzqj/xinwen.m3u8"))
                .setIconUri(Uri.parse("https://mstatic.gzstv.com/media/streams/images/2016/01/20/2ejVhB_USWMM_KsKg09p.jpg"))
                .build();

        MediaDescriptionCompat music2 = new MediaDescriptionCompat.Builder()
                .setMediaId("2")
                .setTitle("3D潮音 - 3D环绕嗨曲")
                .setSubtitle("未知来源")
                .setMediaUri(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/0517/573b1240d0118.mp3"))
                .setIconUri(Uri.parse("http://mpic.spriteapp.cn/crop/566x360/picture/2016/0517/573b1240af3da.jpg"))
                .build();

        MediaDescriptionCompat music3 = new MediaDescriptionCompat.Builder()
                .setMediaId("3")
                .setTitle("电音House 耳机福利")
                .setSubtitle("未知来源")
                .setMediaUri(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/1108/5821463c8ea94.mp3"))
                .setIconUri(Uri.parse("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2519389522,3836335851&fm=26&gp=0.jpg"))
                .build();


        MediaDescriptionCompat music4 = new MediaDescriptionCompat.Builder()
                .setMediaId("4")
                .setTitle("感觉很放松，我最喜欢在我的兰博基尼上听这首歌，先不说，我换一下电池，还能再听几圈")
                .setSubtitle("未知来源")
                .setMediaUri(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/1123/5834c6bc02059.mp3"))
                .setIconUri(Uri.parse("http://mpic.spriteapp.cn/crop/566x360/picture/2016/0517/573b1240af3da.jpg"))
                .build();

        MediaDescriptionCompat music5 = new MediaDescriptionCompat.Builder()
                .setMediaId("5")
                .setTitle("一辈子有多少的来不及发现已失去最重要的东西 . （精神节奏）")
                .setSubtitle("未知来源")
                .setMediaUri(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/0703/5778246106dab.mp3"))
                .setIconUri(Uri.parse("http://mpic.spriteapp.cn/crop/566x360/picture/2016/0517/573b1240af3da.jpg"))
                .build();

        MediaDescriptionCompat[] musics = new MediaDescriptionCompat[]{music0, music1, music2, music3, music4, music5};
        ArrayList<MediaDescriptionCompat> temp = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("video", new Video());
            bundle.putString("str", "字符串" + i);
            MediaDescriptionCompat item = musics[i % musics.length];
            MediaDescriptionCompat newItem = new MediaDescriptionCompat.Builder()
                    .setMediaId(UUID.randomUUID().toString())
                    .setTitle(item.getTitle())
                    .setSubtitle(item.getSubtitle())
                    .setMediaUri(item.getMediaUri())
                    .setIconUri(item.getIconUri())
                    .setExtras(bundle)
                    .build();
            temp.add(newItem);
        }
        if (shuffle) {
            Collections.shuffle(temp);
        }
        return temp;
    }
}
