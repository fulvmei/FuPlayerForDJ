package com.chengfu.music.player;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chengfu.android.fuplayer.achieve.dj.audio.AudioPlayClient;
import com.chengfu.android.fuplayer.achieve.dj.audio.MusicService;
import com.chengfu.music.player.ui.album.SongListAdapter;
import com.chengfu.music.player.ui.main.SectionsPagerAdapter;
import com.chengfu.music.player.ui.player.BottomDialog;
import com.chengfu.music.player.ui.player.PlayListAdapter;
import com.chengfu.music.player.ui.widget.MyAppBarLayout;
import com.chengfu.music.player.ui.widget.SmallAudioControlView;
import com.chengfu.music.player.util.MusicUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class AlbumActivity extends AppCompatActivity {
    public static final String TAG = "AlbumActivity";

    public static AudioPlayClient audioPlayClient;
    SmallAudioControlView audioControlView;
//    Toolbar toolbar;

    RecyclerView recyclerView;
    SongListAdapter adapter;
    MyAppBarLayout appBarLayout;
    ImageView bgImg;
    ImageView bgImg2;
    Toolbar toolbar;
    TextView info;
    ViewGroup content;
    ViewGroup topTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        topTitle = findViewById(R.id.topTitle);
        content = findViewById(R.id.content);
        bgImg2 = findViewById(R.id.bgImg2);
        bgImg = findViewById(R.id.bgImg);
        info = findViewById(R.id.info);
        appBarLayout = findViewById(R.id.appBarLayout);

        appBarLayout.setOnSizeChangedListener(new MyAppBarLayout.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
//                ViewGroup.LayoutParams layoutParams = bgImg.getLayoutParams();
//                layoutParams.height=h;
//                bgImg.setLayoutParams(layoutParams);

                appBarLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getApplicationContext())
                                .load("http://pic2.sc.chinaz.com/files/pic/pic9/202004/zzpic24653.jpg")
                                .apply(RequestOptions.overrideOf(w,h).centerCrop())
                                .into(bgImg);
                    }
                },100);

//                ViewModelProviders.of().get()
            }
        });


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float alpha = (float) (verticalOffset) / (appBarLayout.getHeight() - 300);
                alpha = Math.abs(alpha);

                setAlphaAllView(topTitle, alpha);

                setAlphaAllView(content, 1.0f - alpha);
//                info.setAlpha(1.0f-alpha);
            }
        });

        toolbar = findViewById(R.id.toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                CharSequence text = info.getText();
                info.setText(text.toString() + text);
                return onOptionsItemSelected(item);
            }
        });

//  RecyclerView.Adapter
//        BaseAdapter

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new SongListAdapter();
        adapter.setData(MusicUtil.getTestMedias(20, false));
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        audioControlView = findViewById(R.id.audioControlView);

        audioControlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlbumActivity.this, AudioPlayActivity.class);
                startActivity(intent);
            }
        });

        audioPlayClient = new AudioPlayClient(this, MusicService.class);

        audioPlayClient.getConnected().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                audioControlView.setSessionToken(audioPlayClient.getMediaBrowser().getSessionToken());
            }
        });

        audioPlayClient.connect();
    }

    public static void setAlphaAllView(View view, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        if (view == null) {
            return;
        }
        if (view.getBackground() != null) {
            view.getBackground().mutate().setAlpha((int) (alpha * 255));
        }
        float alphaNum = alpha;
        view.setAlpha(alphaNum);
        //设置子view透明度
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewChild = vp.getChildAt(i);
                //调用本身（递归）
                setAlphaAllView(viewChild, alpha);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        BottomDialog dialog=new BottomDialog(this);
//        dialog.setContentView(R.layout.dialog_bottom_sheet);
//        dialog.show();
        return super.onOptionsItemSelected(item);
    }
}
