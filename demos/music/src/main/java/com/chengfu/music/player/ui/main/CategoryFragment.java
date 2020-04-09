package com.chengfu.music.player.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class CategoryFragment extends Fragment {
    public static CategoryFragment newInstance() {
        
        Bundle args = new Bundle();
        
        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
