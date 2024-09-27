package com.example.project_android.api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.project_android.entities.VideoData;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VideoApi {
    private ApiService apiService;

    public VideoApi() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        apiService = retrofit.create(ApiService.class);
    }

    public Call<List<VideoData>> getAllVideos() {
        return apiService.getAllVideos();
    }

    public void getLimitedVideos(MutableLiveData<List<VideoData>> videos) {
        Call<List<VideoData>> call = apiService.getVideos();
        call.enqueue(new Callback<List<VideoData>>() {
            @Override
            public void onResponse(Call<List<VideoData>> call, Response<List<VideoData>> response) {
                videos.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<VideoData>> call, Throwable t) {
                // Handle failure
            }
        });
    }
    public void getVideoById(String videoId, MutableLiveData<VideoData> video) {
        Call<VideoData> call = apiService.getVideo(videoId);
        call.enqueue(new Callback<VideoData>() {
            @Override
            public void onResponse(Call<VideoData> call, Response<VideoData> response) {
                video.postValue(response.body());
            }

            @Override
            public void onFailure(Call<VideoData> call, Throwable t) {
                // Handle failure
            }
        });
    }

    public void getVideosByAuthor(String userId, MutableLiveData<List<VideoData>> videos) {
        Call<List<VideoData>> call = apiService.getVideosByAuthor(userId);
        call.enqueue(new Callback<List<VideoData>>() {
            @Override
            public void onResponse(Call<List<VideoData>> call, Response<List<VideoData>> response) {
                videos.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<VideoData>> call, Throwable t) {
                // Handle failure
            }
        });
    }

    public void uploadVideo(String token, String userId, File imgFile, File videoFile, String title, String description, String author, String username, String authorImage, String uploadTime, UploadCallback callback) {
        RequestBody titlePart = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody authorPart = RequestBody.create(MediaType.parse("text/plain"), author);
        RequestBody usernamePart = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody authorImagePart = RequestBody.create(MediaType.parse("text/plain"), authorImage);
        RequestBody uploadTimePart = RequestBody.create(MediaType.parse("text/plain"), uploadTime);

        RequestBody imgBody = RequestBody.create(MediaType.parse("image/*"), imgFile);
        MultipartBody.Part imgPart = MultipartBody.Part.createFormData("img", imgFile.getName(), imgBody);

        RequestBody videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile);
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("video", videoFile.getName(), videoBody);

        Call<VideoData> call = apiService.createVideo(token, userId, imgPart, videoPart, titlePart, descriptionPart, authorPart, usernamePart, authorImagePart, uploadTimePart);
        call.enqueue(new Callback<VideoData>() {
            @Override
            public void onResponse(Call<VideoData> call, Response<VideoData> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Upload failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<VideoData> call, Throwable t) {
                callback.onFailure("Upload failed: " + t.getMessage());
            }
        });
    }

    public interface UploadCallback {
        void onSuccess(VideoData videoData);
        void onFailure(String error);
    }

    public void updateVideo(String token, String userId, String videoId, File imgFile, File videoFile, String title, String description, UploadCallback callback) {
        RequestBody titlePart = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description);

        MultipartBody.Part imgPart = null;
        if (imgFile != null) {
            RequestBody imgBody = RequestBody.create(MediaType.parse("image/*"), imgFile);
            imgPart = MultipartBody.Part.createFormData("img", imgFile.getName(), imgBody);
        }

        MultipartBody.Part videoPart = null;
        if (videoFile != null) {
            RequestBody videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile);
            videoPart = MultipartBody.Part.createFormData("video", videoFile.getName(), videoBody);
        }

        Call<VideoData> call = apiService.updateVideo(token, userId, videoId, imgPart, videoPart, titlePart, descriptionPart);
        call.enqueue(new Callback<VideoData>() {
            @Override
            public void onResponse(Call<VideoData> call, Response<VideoData> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Update failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<VideoData> call, Throwable t) {
                callback.onFailure("Update failed: " + t.getMessage());
            }
        });
    }

    public void deleteVideo(String token, String userId, String videoId, final DeleteCallback callback) {
        Call<Void> call = apiService.deleteVideo(token, userId, videoId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Delete failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure("Delete failed: " + t.getMessage());
            }
        });
    }

    public interface DeleteCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public void likeVideo(String token, String videoId, JsonObject requestBody) {
        Call<Void> call = apiService.likeVideo(token, videoId, requestBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("VideoApi", "Successfully liked video with ID: " + videoId);
                } else {
                    Log.e("VideoApi", "Failed to like video: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("VideoApi", "Error liking video: " + t.getMessage());
            }
        });
    }

    public void dislikeVideo(String token, String videoId, JsonObject requestBody) {
        Call<Void> call = apiService.dislikeVideo(token, videoId, requestBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("VideoApi", "Successfully disliked video with ID: " + videoId);
                } else {
                    Log.e("VideoApi", "Failed to dislike video: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("VideoApi", "Error disliking video: " + t.getMessage());
            }
        });
    }

    public void getRecommendations(String videoId, String userId, MutableLiveData<List<VideoData>> recommendedVideos) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("userId", userId);
        requestBody.addProperty("videoId", videoId);

        Call<List<VideoData>> call = apiService.getRecommendations(videoId, requestBody);
        call.enqueue(new Callback<List<VideoData>>() {
            @Override
            public void onResponse(Call<List<VideoData>> call, Response<List<VideoData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<VideoData> videos = response.body();
                    for (VideoData video : videos) {
                        video.setUrlForEmulator();
                    }
                    recommendedVideos.postValue(videos);
                } else {
                    recommendedVideos.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<VideoData>> call, Throwable t) {
                recommendedVideos.postValue(null);
            }
        });
    }
}