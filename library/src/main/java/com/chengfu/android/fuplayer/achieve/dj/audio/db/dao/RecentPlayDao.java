package com.chengfu.android.fuplayer.achieve.dj.audio.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;


import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.RecentPlayEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.vo.RecentPlay;

import java.util.List;

@Dao
public interface RecentPlayDao {

    @Query("SELECT * FROM RecentPlayEntity")
    LiveData<List<RecentPlayEntity>> queryAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(RecentPlayEntity entity);
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    long[] insertAll(List<RecentPlayEntity> list);
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    long[] insertAll(RecentPlayEntity... entities);
//
//    @Update
//    int update(RecentPlayEntity entity);
//
//    @Update
//    int updateAll(List<RecentPlayEntity> list);
//
//    @Update
//    int updateAll(RecentPlayEntity... entities);
//
//    @Delete
//    int delete(RecentPlayEntity entity);
//
//    @Delete
//    int deleteAll(List<RecentPlayEntity> list);
//
//    @Delete
//    int deleteAll(RecentPlayEntity... entities);
//
//    @Query("DELETE  FROM recent_play")
//    void deleteAll();

    @Transaction
    @Query("Select * From RecentPlayEntity ORDER BY modifiedTime DESC")
    LiveData<List<RecentPlay>> getRecentPlayList();

    @Transaction
    @Query("Select * From RecentPlayEntity ORDER BY modifiedTime DESC LIMIT :limit")
    LiveData<List<RecentPlay>> getRecentPlayList(int limit);
}
