package com.chengfu.music.player.ui.player;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chengfu.music.player.R;

import java.util.List;

public class RecentPlayListAdapter extends RecyclerView.Adapter<RecentPlayListAdapter.ViewHolder> {


    private List<MediaDescriptionCompat> list;
    private long activeQueueItemId = MediaSessionCompat.QueueItem.UNKNOWN_ID;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClick(View v,MediaDescriptionCompat item);
    }

    public void setData(List<MediaDescriptionCompat> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setActiveQueueItemId(long activeQueueItemId) {
        this.activeQueueItemId = activeQueueItemId;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<MediaDescriptionCompat> getList() {
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
        MediaDescriptionCompat item = list.get(position);

        holder.img.setImageBitmap(item.getIconBitmap());
        holder.title.setText(item.getTitle());
        holder.subTitle.setText(item.getSubtitle());

//        if (item.getQueueId() == activeQueueItemId) {
//            holder.playing.setVisibility(View.VISIBLE);
//        } else {
//            holder.playing.setVisibility(View.INVISIBLE);
//        }
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
                        onItemClickListener.onClick(view, list.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

}
