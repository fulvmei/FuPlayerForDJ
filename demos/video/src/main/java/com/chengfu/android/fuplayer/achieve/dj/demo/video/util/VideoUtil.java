package com.chengfu.android.fuplayer.achieve.dj.demo.video.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;


import com.chengfu.android.fuplayer.achieve.dj.demo.video.APP;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.bean.Video;

import java.util.ArrayList;
import java.util.List;

public class VideoUtil {

    public static List<Video> getVideoList() {
        List<Video> list = new ArrayList<>();

        list.add(new Video(
                "你欠缺的也许并不是能力",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2016/06/22/SBP8G92E3_hd.mp4",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2016/11/b/a/c36e048e284c459686133e66a79e2eba.jpg",
                "1",
                "1"));

        list.add(new Video(
                "坚持与放弃",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2015/08/27/SB13F5AGJ_sd.mp4",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2016/11/0/4/e4c8836bfe154d76a808da38d0733304.jpg",
                "1",
                "2"));

        list.add(new Video(
                "不想从被子里出来",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2018/01/12/SD70VQJ74_sd.mp4",
                "http://open-image.nosdn.127.net/57baaaeaad4e4fda8bdaceafdb9d45c2.jpg",
                "1",
                "3"));

        list.add(new Video(
                "不耐烦的中国人?",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2017/05/31/SCKR8V6E9_hd.mp4",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2016/11/e/9/ac655948c705413b8a63a7aaefd4cde9.jpg",
                "1",
                "4"));

        list.add(new Video(
                "神奇的珊瑚",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2016/11/e/4/75bc6c5227314e63bbfd5d9f0c5c28e4.jpg",
                "1",
                "5"));

        list.add(new Video(
                "怎样经营你的人脉",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2018/04/19/SDEQS1GO6_hd.mp4",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2018/3/b/c/9d451a2da3cf42b0a049ba3e249222bc.jpg",
                "1",
                "6"));

        list.add(new Video(
                "怎么才能不畏将来",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2018/01/25/SD82Q0AQE_hd.mp4",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2018/1/c/8/1aec3637270f465faae52713a7c191c8.jpg",
                "2",
                "7"));

        list.add(new Video(
                "音乐和艺术如何改变世界",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2017/12/04/SD3SUEFFQ_hd.mp4",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2017/12/2/8/f30dd5f2f09c405c98e7eb6c06c89928.jpg",
                "2",
                "8"));
        return list;
    }

    public static Video getVideo( String id) {
        List<Video> list = getVideoList();
        list.addAll(getLocalVideoList(APP.application));
        for (Video video : list) {
            if (video.getTag().equals(id)) {
                return video;
            }
        }
        return null;
    }


    public static List<Video> getAudiooList() {
        List<Video> list = new ArrayList<>();

        list.add(new Video(
                "3D潮音 - 3D环绕嗨曲",
                "http://mvoice.spriteapp.cn/voice/2016/0517/573b1240d0118.mp3",
                "http://mpic.spriteapp.cn/crop/566x360/picture/2016/0517/573b1240af3da.jpg",
                null,
                null));

        list.add(new Video(
                "电音House   耳机福利",
                "http://mvoice.spriteapp.cn/voice/2016/1108/5821463c8ea94.mp3",
                "http://mpic.spriteapp.cn/crop/566x360/picture/2016/1108/5821463c3fad8.jpg",
                null,
                null));

        list.add(new Video(
                "爱过的人我已不再拥有，错过的人是否可回首 . （治愈女声）",
                "http://mvoice.spriteapp.cn/voice/2016/1104/581b63392f6cb.mp3",
                "http://mpic.spriteapp.cn/crop/566x360/picture/2016/1104/581b633864635.jpg",
                null,
                null));

        list.add(new Video(
                "感觉很放松，我最喜欢在我的兰博基尼上听这首歌，先不说，...",
                "http://mvoice.spriteapp.cn/voice/2016/1123/5834c6bc02059.mp3",
                "http://mpic.spriteapp.cn/crop/566x360/picture/2016/1123/5834c6bbdcce7.jpg",
                null,
                null));

        list.add(new Video(
                "一辈子有多少的来不及发现已失去最重要的东西 . （精神节奏）",
                "http://mvoice.spriteapp.cn/voice/2016/0703/5778246106dab.mp3",
                "http://mpic.spriteapp.cn/crop/566x360/picture/2016/0703/57782460908e4.jpg",
                null,
                null));

        list.add(new Video(
                "陪你度过漫长岁月。（达尔文）",
                "http://mvoice.spriteapp.cn/voice/2017/0515/591969966204f.mp3",
                "http://mpic.spriteapp.cn/crop/566x360/picture/2017/0515/5919699622800.jpg",
                null,
                null));

        list.add(new Video(
                "应广大百友要求！要我媳妇把整首《天空之城》清唱完毕！在...",
                "http://mvoice.spriteapp.cn/voice/2016/0423/571ac24dab840.mp3",
                "http://mpic.spriteapp.cn/crop/566x360/picture/2016/0423/571ac24da061b.jpg",
                null,
                null));

        list.add(new Video(
                "我是肌无力患者，由于力气不足唱的不好，我真心送给天下母...",
                "http://mvoice.spriteapp.cn/voice/2017/0108/5871ba43667c6.mp3",
                "http://mpic.spriteapp.cn/crop/566x360/picture/2017/0108/5871ba41e0681.jpg",
                null,
                null));

        return list;
    }


    public static List<Video> getLocalVideoList(Context context) {
        List<Video> videoList = new ArrayList<>();
        // MediaStore.Video.Thumbnails.DATA:视频缩略图的文件路径
        String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID};
        // 视频其他信息的查询条件
        String[] mediaColumns = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME};

        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media
                        .EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null);

        if (cursor == null) {
            return videoList;
        }
        if (cursor.moveToFirst()) {
            do {
                Video info = new Video();
                int id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Video.Media._ID));
                Cursor thumbCursor = context.getContentResolver().query(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                                + "=" + id, null, null);
                if (thumbCursor.moveToFirst()) {
                    info.setImage(thumbCursor.getString(thumbCursor
                            .getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
                }
                info.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media
                        .DATA)));
                info.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video
                        .Media.DISPLAY_NAME)));
                info.setTag(info.getName());
                videoList.add(info);
            } while (cursor.moveToNext());
        }
        return videoList;
    }
}
