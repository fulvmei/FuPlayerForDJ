package com.chengfu.android.fuplayer.achieve.dj.audio.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.AudioEntity;

import java.util.List;

@Dao
public interface AudioDao {

//    @Query("SELECT * FROM audio WHERE id==:id")
//    LiveData<List<AudioEntity>> queryById(long id);
//
//    @Query("SELECT * FROM audio WHERE id IN (:playListIds)")
//    LiveData<List<AudioEntity>> queryByIds(List<Integer> playListIds);
//
//    @Query("SELECT * FROM audio WHERE id IN (:playListIds)")
//    LiveData<List<AudioEntity>> queryByIds(long... playListIds);
//
//    @Query("SELECT * FROM audio WHERE id IN (:playListIds)")
//    List<AudioEntity> queryByIdsSyn(long... playListIds);

    @Query("SELECT * FROM audio")
    LiveData<List<AudioEntity>> queryAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(AudioEntity playlist);
//
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(List<AudioEntity> list);
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    long[] insertAll(AudioEntity... playlists);
//
//    @Update(onConflict = OnConflictStrategy.REPLACE)
//    int update(AudioEntity playlist);
//
//    @Update(onConflict = OnConflictStrategy.REPLACE)
//    int updateAll(List<AudioEntity> list);
//
//    @Update(onConflict = OnConflictStrategy.REPLACE)
//    int updateAll(AudioEntity... playlists);
//
    @Delete
    int delete(AudioEntity playlist);
//
//    @Delete
//    int deleteAll(List<AudioEntity> list);
//
//    @Delete
//    int deleteAll(AudioEntity... playlists);


}
