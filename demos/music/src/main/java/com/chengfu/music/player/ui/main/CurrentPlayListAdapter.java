package com.chengfu.music.player.ui.main;

import android.support.v4.media.session.MediaSessionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chengfu.android.fuplayer.achieve.dj.audio.db.vo.CurrentPlay;
import com.chengfu.music.player.R;

import java.util.List;

public class CurrentPlayListAdapter extends RecyclerView.Adapter<CurrentPlayListAdapter.ViewHolder> {


    private List<MediaSessionCompat.QueueItem> list;
    private long activeQueueItemId = MediaSessionCompat.QueueItem.UNKNOWN_ID;
    private View.OnClickListener onItemClickListener;

    public void setData(List<MediaSessionCompat.QueueItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setActiveQueueItemId(long activeQueueItemId) {
        this.activeQueueItemId = activeQueueItemId;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<MediaSessionCompat.QueueItem> getList() {
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_current_play, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaSessionCompat.QueueItem item = list.get(position);

        holder.img.setImageBitmap(item.getDescription().getIconBitmap());
        holder.title.setText(item.getDescription().getTitle());
        holder.subTitle.setText(item.getDescription().getSubtitle());

        if (item.getQueueId() == activeQueueItemId) {
            holder.playing.setVisibility(View.VISIBLE);
        } else {
            holder.playing.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;
        TextView subTitle;
        View playing;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.title);
            subTitle = itemView.findViewById(R.id.subTitle);
            playing = itemView.findViewById(R.id.playing);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(view);
                    }
                }
            });
        }
    }

}
