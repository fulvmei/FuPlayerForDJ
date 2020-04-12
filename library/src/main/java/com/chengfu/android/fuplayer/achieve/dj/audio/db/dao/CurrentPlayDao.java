package com.chengfu.android.fuplayer.achieve.dj.audio.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;


import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.QueueItemEntity;
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

    @Query("SELECT * FROM QueueItemEntity WHERE position >= :index")
    List<QueueItemEntity> queryUpIndex(int index);

//    @Query("SELECT * FROM QueueItemEntity WHERE playing")
//    QueueItemEntity queryCurrentPlaying();

    @Query("SELECT * FROM QueueItemEntity")
    LiveData<List<QueueItemEntity>> queryAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(QueueItemEntity entity);

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    long insert(CurrentPlayEntity entity, int index);
//
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(List<QueueItemEntity> list);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(QueueItemEntity... entities);

    @Update
    int update(QueueItemEntity entity);

    @Update
    int updateAll(List<QueueItemEntity> list);

    @Update
    int updateAll(QueueItemEntity... entities);

    @Delete
    int delete(QueueItemEntity entity);

    @Delete
    int deleteAll(List<QueueItemEntity> list);

    @Delete
    int deleteAll(QueueItemEntity... entities);

    @Query("DELETE  FROM QueueItemEntity")
    void deleteAll();

    @Transaction
    @Query("Select * From QueueItemEntity ORDER BY position")
    LiveData<List<CurrentPlay>> getCurrentPlayList();

}
