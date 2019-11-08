package com.chengfu.android.fuplayer.achieve.dj.demo.videofordj;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chengfu.android.fuplayer.achieve.dj.demo.videofordj.been.Video;

public abstract class VideoPlayFragmentApi extends Fragment {

    public static final String ROUTER_PATH = "cn_gzmovement_business_comment_api_CommentListFragment";
    public static final String EXTRA_KEY_VIDEO = "extra_key_video";

    public static VideoPlayFragmentApi newInstance(@NonNull Video video) {
//        Fragment fragment = null;
//        try {
//            fragment = SCore.exposerCalledInterface(ROUTER_PATH);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (fragment != null) {
//            Bundle args = new Bundle();
//            args.putSerializable(EXTRA_KEY_ITEM, item);
//            fragment.setArguments(args);
//        }
//        return (CommentListFragmentApi) fragment;
        VideoPlayFragment fragment = new VideoPlayFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_KEY_VIDEO,video);
        fragment.setArguments(bundle);
        return fragment;
    }
}
