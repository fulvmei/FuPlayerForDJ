package com.chengfu.music.player.ui.main;

import android.support.v4.media.MediaDescriptionCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.chengfu.android.fuplayer.achieve.dj.audio.DataBaseManager;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.AudioDatabase;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.RecentPlayEntity;
import com.chengfu.music.player.MainActivity;
import com.chengfu.music.player.R;

import java.util.List;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.ViewHolder> {

    private List<MediaDescriptionCompat> list;

    public void setData(List<MediaDescriptionCompat> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    public List<MediaDescriptionCompat> getList() {
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaDescriptionCompat item = list.get(position);
        holder.img.setImageBitmap(item.getIconBitmap());
        holder.title.setText(item.getTitle());
        holder.subTitle.setText(item.getSubtitle());
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;
        TextView subTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.title);
            subTitle = itemView.findViewById(R.id.subTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AudioDatabase.getInstance(view.getContext()).recentPlayDao().insert(new RecentPlayEntity(list.get(getAdapterPosition()).getMediaId(), 0));
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

    private void showPopupMenu(final View view, final MediaDescriptionCompat media) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popup_song, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.popup_song_play) {
                    MainActivity.audioPlayClient.addToCurrentPlay(media);
                } else if (id == R.id.popup_song_add_to_playlist) {

                } else if (id == R.id.popup_song_delete) {

                }
                return true;
            }
        });
    }
}
