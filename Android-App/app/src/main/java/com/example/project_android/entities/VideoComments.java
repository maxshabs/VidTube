package com.example.project_android.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.project_android.entities.CommentData;

import java.util.List;
@Entity
public class VideoComments {
    @PrimaryKey
    private String videoId;
    private List<CommentData> comments;

    // Default constructor
    public VideoComments() {
    }

    // Constructor with parameters
    public VideoComments(String videoId, List<CommentData> comments) {
        this.videoId = videoId;
        this.comments = comments;
    }

    // Getters and Setters
    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public List<CommentData> getComments() {
        return comments;
    }

    public void setComments(List<CommentData> comments) {
        this.comments = comments;
    }
}
