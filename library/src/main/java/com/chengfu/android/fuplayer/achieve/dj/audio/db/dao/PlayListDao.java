package com.chengfu.android.fuplayer.achieve.dj.audio.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;


import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.PlaylistEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.vo.Playlist;

import java.util.List;

@Dao
public interface PlayListDao {

//    @Query("SELECT * FROM playlist WHERE id==:id")
//    LiveData<List<PlaylistEntity>> queryById(int id);
//
//    @Query("SELECT * FROM playlist WHERE id IN (:playListIds)")
//    LiveData<List<PlaylistEntity>> queryByIds(List<Integer> playListIds);
//
//    @Query("SELECT * FROM playlist WHERE id IN (:playListIds)")
//    LiveData<List<PlaylistEntity>> queryByIds(int... playListIds);

    @Query("SELECT * FROM playlist")
    LiveData<List<PlaylistEntity>> queryAll();
//
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PlaylistEntity playlist);
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    long[] insertAll(List<PlaylistEntity> list);
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    long[] insertAll(PlaylistEntity... playlists);
//
//    @Update
//    int update(PlaylistEntity playlist);
//
//    @Update
//    int updateAll(List<PlaylistEntity> list);
//
//    @Update
//    int updateAll(PlaylistEntity... playlists);
//
//    @Delete
//    int delete(PlaylistEntity playlist);
//
//    @Delete
//    int deleteAll(List<PlaylistEntity> list);
//
//    @Delete
//    int deleteAll(PlaylistEntity... playlists);
//
//
    @Transaction
    @Query("Select * From playlist")
    LiveData<List<Playlist>> getPlaylist();

//    @Transaction
//    @Query("Select * From playlist")
//    LiveData<Playlist> getPlaylist(String id);
}
