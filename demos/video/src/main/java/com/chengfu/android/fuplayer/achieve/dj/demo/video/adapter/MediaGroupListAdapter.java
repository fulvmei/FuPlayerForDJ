package com.chengfu.android.fuplayer.achieve.dj.demo.video.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.chengfu.android.fuplayer.achieve.dj.demo.video.R;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.bean.MediaGroup;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.bean.Video;

import java.util.List;

public class MediaGroupListAdapter extends BaseExpandableListAdapter {

    private List<MediaGroup> mediaGroupList;

    public MediaGroupListAdapter(List<MediaGroup> mediaGroupList) {
        this.mediaGroupList = mediaGroupList;
    }

    @Override
    public int getGroupCount() {
        return mediaGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getGroup(groupPosition).getMediaList().size();
    }

    @Override
    public MediaGroup getGroup(int groupPosition) {
        return mediaGroupList.get(groupPosition);
    }

    @Override
    public Video getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).getMediaList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_group, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvTitle = convertView.findViewById(R.id.label_group_normal);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.tvTitle.setText(getGroup(groupPosition).getName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_child, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.label_expand_child);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.tvTitle.setText(getChild(groupPosition, childPosition).getName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class GroupViewHolder {
        TextView tvTitle;
    }

    private static class ChildViewHolder {
        TextView tvTitle;
    }
}
