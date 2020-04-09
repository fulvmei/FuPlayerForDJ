package com.chengfu.music.player.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class MyFragment extends Fragment {

    public static MyFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MyFragment fragment = new MyFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
