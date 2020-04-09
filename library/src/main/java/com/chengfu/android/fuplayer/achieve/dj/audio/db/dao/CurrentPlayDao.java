package com.chengfu.android.fuplayer.achieve.dj.audio.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;


import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.CurrentPlayEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.vo.CurrentPlay;

import java.util.List;

@Dao
public interface CurrentPlayDao {

//    @Query("SELECT * FROM current_play WHERE audio_id ==:id")
//    LiveData<List<CurrentPlayEntity>> queryById(int id);

//    @Query("SELECT * FROM current_play WHERE audio_id IN (:playListIds)")
//    LiveData<List<CurrentPlayEntity>> queryByIds(List<Integer> playListIds);
//
//    @Query("SELECT * FROM current_play WHERE audio_id IN (:playListIds)")
//    LiveData<List<CurrentPlayEntity>> queryByIds(int... playListIds);

    @Query("SELECT * FROM current_play WHERE play_order >= :index")
    List<CurrentPlayEntity> queryUpIndex(int index);

    @Query("SELECT * FROM current_play WHERE playing")
    CurrentPlayEntity queryCurrentPlaying();

    @Query("SELECT * FROM current_play")
    LiveData<List<CurrentPlayEntity>> queryAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CurrentPlayEntity entity);

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    long insert(CurrentPlayEntity entity, int index);
//
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(List<CurrentPlayEntity> list);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(CurrentPlayEntity... entities);

    @Update
    int update(CurrentPlayEntity entity);

    @Update
    int updateAll(List<CurrentPlayEntity> list);

    @Update
    int updateAll(CurrentPlayEntity... entities);

    @Delete
    int delete(CurrentPlayEntity entity);

    @Delete
    int deleteAll(List<CurrentPlayEntity> list);

    @Delete
    int deleteAll(CurrentPlayEntity... entities);

    @Query("DELETE  FROM current_play")
    void deleteAll();

    @Transaction
    @Query("Select * From current_play ORDER BY play_order")
    LiveData<List<CurrentPlay>> getCurrentPlayList();

}
