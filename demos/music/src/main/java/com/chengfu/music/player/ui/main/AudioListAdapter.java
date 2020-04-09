package com.chengfu.music.player.ui.main;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.chengfu.android.fuplayer.achieve.dj.audio.AudioPlayManager;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.AudioDatabase;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.AudioEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.RecentPlayEntity;
import com.chengfu.music.player.R;

import java.util.List;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.ViewHolder> {


    private List<AudioEntity> list;

    public void setData(List<AudioEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<AudioEntity> getList() {
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AudioEntity item = list.get(position);
        holder.title.setText(item.name);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            subTitle = itemView.findViewById(R.id.subTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AudioDatabase.getInstance(view.getContext()).recentPlayDao().insert(new RecentPlayEntity(list.get(getAdapterPosition()).id, 0));
                        }
                    }).start();
                }
            });

            itemView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    showPopupMenu(view, list.get(getAdapterPosition()));
                }
            });
        }
    }

    private void showPopupMenu(final View view, final AudioEntity audio) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popup_song, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.popup_song_play) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AudioPlayManager.addToNextPlay(view.getContext(), audio);
                        }
                    }).start();
                } else if (id == R.id.popup_song_add_to_playlist) {

                } else if (id == R.id.popup_song_delete) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AudioDatabase.getInstance(view.getContext()).audioDao().delete(audio);
                        }
                    }).start();
                }
                return true;
            }
        });
    }
}
