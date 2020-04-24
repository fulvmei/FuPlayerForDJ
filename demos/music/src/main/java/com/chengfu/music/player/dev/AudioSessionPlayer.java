package com.chengfu.music.player.dev;

import android.content.Context;
import android.database.Observable;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.ext.exo.FuExoPlayerFactory;
import com.google.android.exoplayer2.source.MediaSource;

public class AudioSessionPlayer {

    private final MediaSourceDataObserver mObserver = new MediaSourceDataObserver();
    Adapter mAdapter;

    @NonNull
    private Context mContext;
    @NonNull
    private final MediaSessionCompat mMediaSession;
    @NonNull
    private final FuPlayer mPlayer;


    public AudioSessionPlayer(@NonNull Context context, @NonNull MediaSessionCompat mediaSession) {
        mContext = context;
        mMediaSession = mediaSession;

        mPlayer = new FuExoPlayerFactory(context).create();
    }

    public void setAdapter(Adapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterAdapterDataObserver(mObserver);
        }
        final Adapter oldAdapter = mAdapter;
        mAdapter = adapter;
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }
    }

    private class MediaSourceDataObserver extends AdapterDataObserver {

    }

    public abstract static class AdapterDataObserver {
        public void onChanged() {

        }

        public void onItemRangeChanged(int positionStart, int itemCount) {

        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        }
    }

    static class AdapterDataObservable extends Observable<AdapterDataObserver> {
        public boolean hasObservers() {
            return !mObservers.isEmpty();
        }

        public void notifyChanged() {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount);
        }


        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemRangeInserted(positionStart, itemCount);
            }
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemRangeRemoved(positionStart, itemCount);
            }
        }

        public void notifyItemMoved(int fromPosition, int toPosition) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemRangeMoved(fromPosition, toPosition, 1);
            }
        }
    }


    public abstract static class Adapter {
        private final AdapterDataObservable mObservable = new AdapterDataObservable();

        public abstract int getItemCount();

        public abstract MediaSource onCreateMediaSource(int position);

        public void registerAdapterDataObserver(@NonNull AdapterDataObserver observer) {
            mObservable.registerObserver(observer);
        }

        public void unregisterAdapterDataObserver(@NonNull AdapterDataObserver observer) {
            mObservable.unregisterObserver(observer);
        }

        public final void notifyDataSetChanged() {
            mObservable.notifyChanged();
        }

        public final void notifyItemChanged(int position) {
            mObservable.notifyItemRangeChanged(position, 1);
        }

        public final void notifyItemRangeChanged(int positionStart, int itemCount) {
            mObservable.notifyItemRangeChanged(positionStart, itemCount);
        }

        public final void notifyItemInserted(int position) {
            mObservable.notifyItemRangeInserted(position, 1);
        }

        public final void notifyItemMoved(int fromPosition, int toPosition) {
            mObservable.notifyItemMoved(fromPosition, toPosition);
        }

        public final void notifyItemRangeInserted(int positionStart, int itemCount) {
            mObservable.notifyItemRangeInserted(positionStart, itemCount);
        }

        public final void notifyItemRemoved(int position) {
            mObservable.notifyItemRangeRemoved(position, 1);
        }

        public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
            mObservable.notifyItemRangeRemoved(positionStart, itemCount);
        }
    }
}
