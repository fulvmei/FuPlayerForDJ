package com.chengfu.android.fuplayer.achieve.dj.demo.video.ui.local;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.R;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.bean.Video;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.ui.video.VideoPlayerActivity;

import java.util.List;

public class LocalVideoListAdapter extends RecyclerView.Adapter<LocalVideoListAdapter.ViewHolder> {


    private List<Video> mediaList;


    public void setData(List<Video> dataList) {
        this.mediaList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_local_video, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, int position) {
        Video video = mediaList.get(position);
        vh.title.setText(video.getName());
        Glide.with(vh.img).load(video.getImage()).into(vh.img);
    }

    @Override
    public int getItemCount() {
        return mediaList != null ? mediaList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Video media = mediaList.get(getAdapterPosition());
            Intent intent = new Intent(itemView.getContext(), VideoPlayerActivity.class);
            intent.putExtra("id", media.getTag());
            itemView.getContext().startActivity(intent);
        }
    }
}
