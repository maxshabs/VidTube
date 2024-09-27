package com.example.project_android.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.project_android.entities.CommentData;

import java.util.List;

@Dao
public interface CommentDao {

    @Query("SELECT * FROM comments WHERE videoId = :videoId")
    List<CommentData> getCommentsForVideo(String videoId);

    @Query("SELECT * FROM comments WHERE _id = :commentId")
    CommentData getCommentById(String commentId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertComment(CommentData comment);

    @Update
    void updateComment(CommentData comment);

    @Delete
    void deleteComment(CommentData comment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertComments(List<CommentData> comments);

    @Query("SELECT * FROM comments WHERE videoId = :videoId")
    LiveData<List<CommentData>> getCommentsForVideoLive(String videoId);
}
