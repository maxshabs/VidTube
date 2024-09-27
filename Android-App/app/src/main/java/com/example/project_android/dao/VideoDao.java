package com.example.project_android.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.project_android.entities.VideoData;

import java.util.List;

@Dao
public interface VideoDao {
    @Query("SELECT * FROM videos")
    LiveData<List<VideoData>> getAllVideos();

    @Query("SELECT * FROM videos WHERE _id = :videoId")
    LiveData<VideoData> getVideoById(String videoId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVideo(VideoData video);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVideos(List<VideoData> videos);

    @Update
    void updateVideo(VideoData video);

    @Query("DELETE FROM videos WHERE _id = :videoId")
    void deleteVideoById(String videoId);

    @Query("DELETE FROM videos")
    void deleteAllVideos();

    @Query("SELECT * FROM videos LIMIT 20")
    LiveData<List<VideoData>> getLimitedVideos();

    @Query("SELECT * FROM videos WHERE username = :username")
    LiveData<List<VideoData>> getVideosByAuthor(String username);
}
