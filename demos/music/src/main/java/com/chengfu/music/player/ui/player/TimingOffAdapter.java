package com.chengfu.music.player.ui.player;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chengfu.android.fuplayer.achieve.dj.audio.player.TimingOff;
import com.chengfu.music.player.R;

import java.util.List;

public class TimingOffAdapter extends RecyclerView.Adapter<TimingOffAdapter.ViewHolder> {

    private List<TimingOff> list;
    private OnCheckedChangeListener onCheckedChangeListener;
    private int selectedPos;

    public interface OnCheckedChangeListener {
        void onCheckedChanged(View v, TimingOff item);
    }

    public void setData(List<TimingOff> list) {
        this.list = list;
        selectedPos = 0;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isChecked()) {
                    selectedPos = i;
                    return;
                }
            }
        }
        notifyDataSetChanged();
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return onCheckedChangeListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public List<TimingOff> getList() {
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timingoff, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimingOff item = list.get(position);
        if(item.getMode()==TimingOff.TIMING_OFF_MODE_OFF){
            holder.tv.setText("不开启");
        }else if(item.getMode()==TimingOff.TIMING_OFF_MODE_ONE){
            holder.tv.setText("听完当前音频");
        }else {
            holder.tv.setText(item.getSecond()+"秒");
        }
        holder.rb.setChecked(item.isChecked());
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        RadioButton rb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
            rb = itemView.findViewById(R.id.rb);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedPos != getAdapterPosition()) {
                        list.get(selectedPos).setChecked(false);
                        notifyItemChanged(selectedPos);
                        selectedPos=getAdapterPosition();
                        list.get(selectedPos).setChecked(true);
                        notifyItemChanged(selectedPos);

                        if (onCheckedChangeListener != null) {
                            onCheckedChangeListener.onCheckedChanged(view, list.get(selectedPos));
                        }

                    }
                }
            });
        }
    }

}
