package com.example.project_android.api;

import androidx.room.Query;

import com.example.project_android.entities.CommentData;
import com.example.project_android.entities.User;
import com.example.project_android.entities.VideoData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @GET("api/comments")
    Call<List<CommentData>> getComments();

    @POST("api/comments")
    Call<CommentData> createComment(@Header("Authorization") String token, @Body JsonObject comment);


    @GET("comments/{id}")
    Call<CommentData> getComment(@Path("id") int commentId);

    @PATCH("api/users/{id}/comments")
    Call<CommentData> updateComment(@Header("Authorization") String token, @Path("id") String id, @Body JsonObject comment);


    @DELETE("api/users/{id}/comments")
    Call<Void> deleteComment(@Header("Authorization") String token, @Path("id") String id);

    @PATCH("api/{id}/like")
    Call<Void> likeComment(@Header("Authorization") String token, @Path("id") String commentId, @Body JsonObject displayName);

    @PATCH("api/{id}/dislike")
    Call<Void> dislikeComment(@Header("Authorization") String token, @Path("id") String commentId, @Body JsonObject displayName);

    // Video-related endpoints
    @GET("api/videos")
    Call<List<VideoData>> getVideos();

    @GET("api/allvideos")
    Call<List<VideoData>> getAllVideos();

    @GET("api/videos/{id}")
    Call<VideoData> getVideo(@Path("id") String id);

    @POST("api/videos/{id}")
    Call<List<VideoData>> getRecommendations(
            @Path("id") String videoId,
            @Body JsonObject body
    );

    @GET("api/users/{id}/videos")
    Call<List<VideoData>> getVideosByAuthor(@Path("id") String userId);

    @Multipart
    @POST("api/users/{id}/videos")
    Call<VideoData> createVideo(
            @Header("Authorization") String token,
            @Path("id") String userId,
            @Part MultipartBody.Part img,
            @Part MultipartBody.Part video,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("author") RequestBody author,
            @Part("username") RequestBody username,
            @Part("authorImage") RequestBody authorImage,
            @Part("uploadTime") RequestBody uploadTime
    );

    @Multipart
    @PATCH("api/users/{id}/videos/{pid}")
    Call<VideoData> updateVideo(
            @Header("Authorization") String token,
            @Path("id") String userId,
            @Path("pid") String videoId,
            @Part MultipartBody.Part img,
            @Part MultipartBody.Part video,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description
    );

    @DELETE("api/users/{id}/videos/{pid}")
    Call<Void> deleteVideo(
            @Header("Authorization") String token,
            @Path("id") String userId,
            @Path("pid") String videoId
    );

    @PATCH("api/videos/{id}/like")
    Call<Void> likeVideo(@Header("Authorization") String token, @Path("id") String videoId, @Body JsonObject displayName);

    @PATCH("api/videos/{id}/dislike")
    Call<Void> dislikeVideo(@Header("Authorization") String token, @Path("id") String videoId, @Body JsonObject displayName);

    // User-related endpoints
    @POST("api/users")
    Call<User> createUser(@Body User user);

    @POST("api/users/signin")
    Call<User> signInUser(@Body User user);

    @GET("api/users")
    Call<List<User>> getUsers(@Header("Authorization") String token);

    @GET("api/users/{id}")
    Call<User> getUser(@Header("Authorization") String token, @Path("id") String userId);

    @DELETE("api/users/{id}")
    Call<Void> deleteUser(@Header("Authorization") String token, @Path("id") String userId);

    @PATCH("api/users/{id}")
    Call<User> updateUser(@Header("Authorization") String token, @Path("id") String userId, @Body User user);

    @POST("api/tokens")
    Call<JsonElement> generateToken(@Body User user);
}