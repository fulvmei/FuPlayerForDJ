package com.chengfu.android.fuplayer.achieve.dj.demo.video.util;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class MediaSourceUtil {

//    public static MediaSource getMediaSource(Context context, String path) {
//
//        Uri uri = Uri.parse(path);
//
//        int contentType = Util.inferContentType(uri);
//        DefaultDataSourceFactory dataSourceFactory =
//                new DefaultDataSourceFactory(context,
//                        Util.getUserAgent(context, context.getPackageName()), new DefaultBandwidthMeter());
//
//
////        String scheme = uri.getScheme();
////        if (scheme != null && scheme.contains("rtmp")) {
////            return new ExtractorMediaSource(uri, new RtmpDataSourceFactory(), new DefaultExtractorsFactory(), null, null);
////        }
//
//        switch (contentType) {
////            case C.TYPE_DASH:
////                DefaultDashChunkSource.Factory factory = new DefaultDashChunkSource.Factory(dataSourceFactory);
////                return new DashMediaSource(uri, dataSourceFactory, factory, null, null);
////            case C.TYPE_SS:
////                DefaultSsChunkSource.Factory ssFactory = new DefaultSsChunkSource.Factory(dataSourceFactory);
////                return new SsMediaSource(uri, dataSourceFactory, ssFactory, null, null);
////            case C.TYPE_HLS:
////                return new HlsMediaSource(uri, dataSourceFactory, null, null);
//
//            case C.TYPE_OTHER:
//            default:
//                // This is the MediaSource representing the media to be played.
//                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//                return new ExtractorMediaSource(uri,
//                        dataSourceFactory, extractorsFactory, null, null);
//        }
//    }
}
