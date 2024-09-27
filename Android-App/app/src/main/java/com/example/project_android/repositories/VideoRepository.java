package com.example.project_android.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_android.AppDatabase;
import com.example.project_android.TokenManager;
import com.example.project_android.api.VideoApi;
import com.example.project_android.dao.VideoDao;
import com.example.project_android.entities.VideoData;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoRepository {
    private VideoApi videoApi;
    private VideoDao videoDao;

    public VideoRepository(Context context) {
        videoApi = new VideoApi();
        videoDao = AppDatabase.getInstance(context).videoDao();
    }

    public LiveData<List<VideoData>> getAllVideos() {
        MutableLiveData<List<VideoData>> allVideos = new MutableLiveData<>();
        videoApi.getAllVideos().enqueue(new Callback<List<VideoData>>() {
            @Override
            public void onResponse(Call<List<VideoData>> call, Response<List<VideoData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<VideoData> videos = response.body();
                    for (VideoData video : videos) {
                        video.setUrlForEmulator();
                    }
                    new Thread(() -> {
                        videoDao.deleteAllVideos();
                        videoDao.insertVideos(videos);
                        allVideos.postValue(videos);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<List<VideoData>> call, Throwable t) {
                Log.e("VideoRepository", "Get all videos failed: " + t.getMessage());
            }
        });
        return videoDao.getAllVideos();
    }

    public LiveData<List<VideoData>> getLimitedVideos() {
        MutableLiveData<List<VideoData>> limitedVideos = new MutableLiveData<>();
        videoApi.getLimitedVideos(limitedVideos);
        limitedVideos.observeForever(videos -> {
            if (videos != null) {
                for (VideoData video : videos) {
                video.setUrlForEmulator();
                }
                new Thread(() -> {
                    videoDao.deleteAllVideos();
                    videoDao.insertVideos(videos);
                }).start();
            }
        });
        return videoDao.getLimitedVideos();
    }

    public LiveData<VideoData> getVideoById(String videoId) {
        return videoDao.getVideoById(videoId);
    }

    public LiveData<List<VideoData>> getVideosByAuthor(String username) {
        MutableLiveData<List<VideoData>> videosByAuthor = new MutableLiveData<>();
        videoApi.getVideosByAuthor(username, videosByAuthor);
        videosByAuthor.observeForever(videos -> {
            if (videos != null) {
                for (VideoData video : videos) {
                    video.setUrlForEmulator();
                }
                new Thread(() -> {
                    videoDao.deleteAllVideos();
                    videoDao.insertVideos(videos);
                }).start();
            }
        });
        return videoDao.getVideosByAuthor(username);
    }

    public LiveData<VideoData> uploadVideo(String token, String userId, File imgFile, File videoFile, String title, String description, String author, String username, String authorImage, String uploadTime) {
        MutableLiveData<VideoData> uploadedVideo = new MutableLiveData<>();
        videoApi.uploadVideo(token, userId, imgFile, videoFile, title, description, author, username, authorImage, uploadTime, new VideoApi.UploadCallback() {
            @Override
            public void onSuccess(VideoData videoData) {
                videoData.setUrlForEmulator();
                new Thread(() -> {
                    videoDao.insertVideo(videoData);
                    uploadedVideo.postValue(videoData);
                }).start();
            }

            @Override
            public void onFailure(String error) {
                Log.e("VideoRepository", "Upload failed: " + error);
            }
        });
        return uploadedVideo;
    }

    public LiveData<VideoData> updateVideo(String token, String userId, String videoId, File imgFile, File videoFile, String title, String description) {
        MutableLiveData<VideoData> updatedVideo = new MutableLiveData<>();
        videoApi.updateVideo(token, userId, videoId, imgFile, videoFile, title, description, new VideoApi.UploadCallback() {
            @Override
            public void onSuccess(VideoData videoData) {
                videoData.setUrlForEmulator();
                new Thread(() -> {
                    videoDao.updateVideo(videoData);
                    updatedVideo.postValue(videoData);
                }).start();
            }

            @Override
            public void onFailure(String error) {
                Log.e("VideoRepository", "Update failed: " + error);
            }
        });
        return updatedVideo;
    }

    public LiveData<Boolean> deleteVideo(String token, String userId, String videoId) {
        MutableLiveData<Boolean> deleteResult = new MutableLiveData<>();
        videoApi.deleteVideo(token, userId, videoId, new VideoApi.DeleteCallback() {
            @Override
            public void onSuccess() {
                new Thread(() -> {
                    videoDao.deleteVideoById(videoId);
                    deleteResult.postValue(true);
                }).start();
            }

            @Override
            public void onFailure(String error) {
                Log.e("VideoRepository", "Delete failed: " + error);
                deleteResult.postValue(false);
            }
        });
        return deleteResult;
    }
    public void likeVideo(String videoId, String userDisplayName) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("userDisplayName", userDisplayName);

        videoApi.likeVideo("Bearer " + TokenManager.getInstance().getToken(), videoId, requestBody);
    }

    public void dislikeVideo(String videoId, String userDisplayName) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("userDisplayName", userDisplayName);

        videoApi.dislikeVideo("Bearer " + TokenManager.getInstance().getToken(), videoId, requestBody);
    }

    public void syncWithServer() {
        videoApi.getAllVideos().enqueue(new Callback<List<VideoData>>() {
            @Override
            public void onResponse(Call<List<VideoData>> call, Response<List<VideoData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<VideoData> videos = response.body();
                    for (VideoData video : videos) {
                        video.setUrlForEmulator();
                    }
                    new Thread(() -> {
                        videoDao.deleteAllVideos();
                        videoDao.insertVideos(videos);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<List<VideoData>> call, Throwable t) {
                Log.e("VideoRepository", "Sync failed: " + t.getMessage());
            }
        });
    }

    public LiveData<List<VideoData>> getRecommendations(String videoId, String userId) {
        MutableLiveData<List<VideoData>> recommendedVideos = new MutableLiveData<>();
        videoApi.getRecommendations(videoId, userId, recommendedVideos);
        return recommendedVideos;
    }

}
