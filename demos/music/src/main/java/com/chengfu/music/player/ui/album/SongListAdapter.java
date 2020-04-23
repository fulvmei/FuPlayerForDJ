package com.chengfu.music.player.ui.album;

import android.graphics.Color;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chengfu.music.player.R;

import java.text.MessageFormat;
import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {
    private List<MediaDescriptionCompat> list;
    private String activeMediaId = "";
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        default void onItemClick(View v, MediaDescriptionCompat item) {
        }

        default void onMenuClick(View v, MediaDescriptionCompat item) {
        }
    }

    public void setData(List<MediaDescriptionCompat> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setActiveMediaId(String activeMediaId) {
        this.activeMediaId = activeMediaId;
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

        holder.index.setText(MessageFormat.format("{0}", position + 1));
        holder.title.setText(item.getTitle());
        holder.subTitle.setText(item.getSubtitle());

        if (TextUtils.equals(activeMediaId, item.getMediaId())) {
            holder.index.setVisibility(View.INVISIBLE);
            holder.playFlag.setVisibility(View.VISIBLE);
            holder.title.setTextColor(Color.parseColor("#D81E06"));
        } else {
            holder.index.setVisibility(View.VISIBLE);
            holder.playFlag.setVisibility(View.INVISIBLE);
            holder.title.setTextColor(Color.parseColor("#1A1F24"));
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView index;
        TextView title;
        TextView subTitle;
        View playFlag;
        View menu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            index = itemView.findViewById(R.id.index);
            title = itemView.findViewById(R.id.title);
            subTitle = itemView.findViewById(R.id.subTitle);
            playFlag = itemView.findViewById(R.id.playFlag);
            menu = itemView.findViewById(R.id.menu);

            itemView.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, list.get(getAdapterPosition()));
                }
            });

            menu.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onMenuClick(view, list.get(getAdapterPosition()));
                }
            });
        }
    }

}
