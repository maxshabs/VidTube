package com.example.project_android.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.project_android.Converters;

import java.util.List;

@Entity(tableName = "comments")
public class CommentData {
    @PrimaryKey
    @NonNull
    private String _id;
    private String text;
    private String username;
    private String displayName;
    private String date;
    private String img;
    private boolean isLiked;
    private boolean isDisliked;
    private String videoId;

    @TypeConverters(Converters.class)
    private List<String> likes;

    @TypeConverters(Converters.class)
    private List<String> dislikes;

    // Default constructor
    public CommentData() {
    }

    // Constructor with parameters
    public CommentData(@NonNull String _id, String text, String username, String displayName, String date, String img, String videoId, List<String> likes, List<String> dislikes) {
        this._id = _id;
        this.text = text;
        this.username = username;
        this.displayName = displayName;
        this.date = date;
        this.img = img;
        this.isLiked = false;
        this.isDisliked = false;
        this.videoId = videoId; // Initialize the videoId field
        this.likes = likes;
        this.dislikes = dislikes;
    }

    // Getters and Setters
    @NonNull
    public String getId() {
        return _id;
    }

    public void setId(@NonNull String _id) {
        this._id = _id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isDisliked() {
        return isDisliked;
    }

    public void setDisliked(boolean disliked) {
        isDisliked = disliked;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<String> getDislikes() {
        return dislikes;
    }

    public void setDislikes(List<String> dislikes) {
        this.dislikes = dislikes;
    }
    public void setUrlForEmulator() {
        if (this.img != null) {
            this.img = this.img.replace("localhost", "10.0.2.2");
        }
    }
    public void setUrlForDevice() {
        if (this.img != null) {
            this.img = this.img.replace("10.0.2.2", "localhost");
        }
    }
}
