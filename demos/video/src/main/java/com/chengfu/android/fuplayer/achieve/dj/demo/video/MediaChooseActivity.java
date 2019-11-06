package com.chengfu.android.fuplayer.achieve.dj.demo.video;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.chengfu.android.fuplayer.achieve.dj.demo.video.adapter.MediaGroupListAdapter;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.bean.MediaGroup;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.bean.Video;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.ui.video.VideoPlayerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MediaChooseActivity extends AppCompatActivity implements ExpandableListView.OnChildClickListener {

    private ExpandableListView expandableListView;

    private MediaGroupListAdapter mediaGroupListAdapter;
    private List<MediaGroup> mediaGroupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_choose);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        expandableListView = findViewById(R.id.expandableListView);

        expandableListView.setOnChildClickListener(this);

        mediaGroupList = getMediaGroupList();
        mediaGroupListAdapter = new MediaGroupListAdapter(mediaGroupList);

        expandableListView.setAdapter(mediaGroupListAdapter);

        mediaGroupListAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<MediaGroup> getMediaGroupList() {
        List<MediaGroup> mediaGroups = new ArrayList<>();
        JSONArray ja = null;
        try {
            ja = new JSONArray(getMediaGroupListString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (ja != null) {
            for (int i = 0; i < ja.length(); i++) {
                try {
                    mediaGroups.add(parsedMediaGroup(ja.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return mediaGroups;
    }

    private MediaGroup parsedMediaGroup(JSONObject jo) {
        MediaGroup mediaGroup = null;
        if (jo != null) {
            mediaGroup = new MediaGroup();
            mediaGroup.setName(jo.optString("name"));
            mediaGroup.setMediaList(parsedMediaList(jo.optJSONArray("media_list")));
        }
        return mediaGroup;
    }

    private List<Video> parsedMediaList(JSONArray ja) {
        List<Video> mediaList = null;
        if (ja != null) {
            mediaList = new ArrayList<>();
            for (int i = 0; i < ja.length(); i++) {
                try {
                    mediaList.add(parsedMedia(ja.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return mediaList;
    }

    private Video parsedMedia(JSONObject jo) {
        Video media = null;
        if (jo != null) {
            media = new Video();
            media.setName(jo.optString("name"));
            media.setPath(jo.optString("path"));
            media.setType(jo.optString("type"));
            media.setTag(jo.optString("tag"));
        }
        return media;
    }

    private String getMediaGroupListString() {
        InputStream inputStream = null;
        String media_list = null;
        try {
            inputStream = getAssets().open("media_list.json");
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            media_list = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return media_list;
    }


    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                int childPosition, long id) {
        Video media = mediaGroupList.get(groupPosition).getMediaList().get(childPosition);
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra("id", media.getTag());
        startActivity(intent);
        return true;
    }
}
