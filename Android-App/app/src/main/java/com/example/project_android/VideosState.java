package com.example.project_android;

import android.util.Log;

import com.example.project_android.entities.VideoData;
import java.util.ArrayList;
import java.util.List;

public class VideosState {

    private static VideosState instance;
    private List<VideoData> videoList;

    private VideosState() {
        videoList = new ArrayList<>();
    }

    public static VideosState getInstance() {
        if (instance == null) {
            instance = new VideosState();
        }
        return instance;
    }

    public List<VideoData> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<VideoData> videoList) {
        this.videoList = videoList;
    }

    public void addVideo(VideoData videoData) {
        videoList.add(videoData);
        Log.d("VideosState", "Video added successfully: " + videoData.getTitle());
    }


    public void updateVideo(VideoData updatedVideo) {
        for (int i = 0; i < videoList.size(); i++) {
            if (videoList.get(i).getId() == updatedVideo.getId()) {
                videoList.set(i, updatedVideo);
                Log.d("VideosState", "Video updated successfully: " + updatedVideo.getTitle());
                break;
            }
        }
    }

    public VideoData getVideoById(String id) {
        for (VideoData video : videoList) {
            if (video.getId().equals(id)) {
                return video;
            }
        }
        return null; // Return null if no video is found with the given ID
    }
}


