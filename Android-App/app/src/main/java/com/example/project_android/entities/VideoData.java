package com.example.project_android.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.project_android.Converters;

import java.util.List;

@Entity(tableName = "videos")
public class VideoData {
    @PrimaryKey
    @NonNull
    private String _id;
    private String title;
    private String description;
    private String author;
    private String username;
    private String views;
    private String img;
    private String video;
    private String uploadTime;
    private String authorImage;
    @TypeConverters(Converters.class)
    private List<String> likes;
    @TypeConverters(Converters.class)
    private List<String> dislikes;

    public VideoData(String title, String description, String author,
                     String username, String views, String img, String video,
                     String uploadTime, String authorImage, List<String> likes, List<String> dislikes) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.username = username;
        this.views = views;
        this.img = img;
        this.video = video;
        this.uploadTime = uploadTime;
        this.authorImage = authorImage;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    // Getters and setters...

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(String authorImage) {
        this.authorImage = authorImage;
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
        this.img = this.img.replace("localhost", "10.0.2.2");
        this.video = this.video.replace("localhost", "10.0.2.2");
        this.authorImage = this.authorImage.replace("localhost", "10.0.2.2");
    }
}