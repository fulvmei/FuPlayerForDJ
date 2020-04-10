package com.chengfu.music.player.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.MediaEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
                music.title=name;
                music.sub_title=artist+"-"+album;
                music.media_uri= Uri.parse(paths[0]);
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

    static  String []paths={"http://mvoice.spriteapp.cn/voice/2016/0517/573b1240d0118.mp3",
            "http://mvoice.spriteapp.cn/voice/2016/1108/5821463c8ea94.mp3",
            "http://mvoice.spriteapp.cn/voice/2016/1104/581b63392f6cb.mp3",
            "http://mvoice.spriteapp.cn/voice/2016/1123/5834c6bc02059.mp3",
            "http://mvoice.spriteapp.cn/voice/2016/0703/5778246106dab.mp3"};
   static String []titles={"3D潮音 - 3D环绕嗨曲",
            "电音House 耳机福利",
            "爱过的人我已不再拥有，错过的人是否可回首 . （治愈女声）",
            "感觉很放松，我最喜欢在我的兰博基尼上听这首歌，先不说，我换一下电池，还能再听几圈",
            "一辈子有多少的来不及发现已失去最重要的东西 . （精神节奏）"};

    public static List<MediaEntity> getNetMusics(Context context) {
        ArrayList<MediaEntity> musics = new ArrayList<>();

        for (int i=0;i<titles.length;i++){
            MediaEntity entity=new MediaEntity("id_"+i);
            entity.media_uri=Uri.parse(paths[i]);
            entity.title=titles[i];
            entity.sub_title="未知专辑";
            musics.add(entity);
        }

        return musics;
    }

}
