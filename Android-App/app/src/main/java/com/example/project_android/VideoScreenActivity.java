package com.example.project_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.project_android.adapters.CommentsAdapter;
import com.example.project_android.adapters.VideoAdapter;
import com.example.project_android.entities.CommentData;
import com.example.project_android.entities.User;
import com.example.project_android.entities.VideoData;
import com.example.project_android.viewmodels.CommentViewModel;
import com.example.project_android.viewmodels.VideoViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VideoScreenActivity extends AppCompatActivity {

    private static final String TAG = "VideoScreenActivity";

    private RecyclerView relatedVideosRecyclerView;
    private VideoAdapter videoAdapter;
    private CommentsAdapter commentsAdapter;
    private VideoData currentVideo;
    private List<VideoData> originalVideoList;
    private NestedScrollView nestedScrollView;
    private VideoViewModel videoViewModel;
    private CommentViewModel commentViewModel;
    private String profilePicture;
    private String currentlyDisplayedVideoId = null; // Variable to track the currently displayed video ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_screen);

        // Initialize VideoViewModel
        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);

        // Initialize CommentViewModel
        commentViewModel = new ViewModelProvider(this).get(CommentViewModel.class);

        // Initialize NestedScrollView
        nestedScrollView = findViewById(R.id.nested_scroll_view);

        // Initialize comments RecyclerView with an empty list
        RecyclerView commentsRecyclerView = findViewById(R.id.comments_recycler_view);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsAdapter = new CommentsAdapter(this, Collections.emptyList());
        commentsRecyclerView.setAdapter(commentsAdapter);

        // Initialize adapter and set it to RecyclerView
        videoAdapter = new VideoAdapter(new ArrayList<>(), this::playSelectedVideo);
        relatedVideosRecyclerView = findViewById(R.id.related_videos_recycler_view);
        relatedVideosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        relatedVideosRecyclerView.setAdapter(videoAdapter);

        // Initialize the comment input section
        LinearLayout commentInputSection = findViewById(R.id.comment_input_section);
        ImageView userImageInput = findViewById(R.id.user_image_input);
        TextView displayNameTextView = findViewById(R.id.display_name_text_view);
        EditText commentInput = findViewById(R.id.comment_input);
        Button submitCommentButton = findViewById(R.id.submit_comment_button);

        if (UserState.isLoggedIn()) {
            commentInputSection.setVisibility(View.VISIBLE);
            User loggedInUser = UserState.getLoggedInUser();
            profilePicture = loggedInUser.getProfilePicture().replace("localhost", "10.0.2.2");
            displayNameTextView.setText(loggedInUser.getDisplayName());

            // Load user image
            loadUserProfileImage(profilePicture, userImageInput);
        } else {
            commentInputSection.setVisibility(View.GONE);
        }

        // Get the video ID from the intent
        String videoId = getIntent().getStringExtra("video_id");
        Log.d(TAG, "Received video ID: " + videoId);
        if (videoId != null) {
            videoViewModel.getAllVideos().observe(this, new Observer<List<VideoData>>() {
                @Override
                public void onChanged(List<VideoData> videos) {
                    if (!isFinishing() && videos != null) {
                        originalVideoList = videos;
                        VideoData selectedVideo = findVideoById(videoId);
                        if (selectedVideo != null) {
                            // Only update if this is a new video
                            if (!videoId.equals(currentlyDisplayedVideoId)) {
                                displayVideoDetails(selectedVideo);
                                currentlyDisplayedVideoId = videoId;  // Update currently displayed video ID
                                observeComments(selectedVideo.getId());
                            }
                        }
                    }
                }
            });
        }

        // Set up the show comments button
        Button showCommentsButton = findViewById(R.id.show_comments_button);
        View commentsContainer = findViewById(R.id.comments_container);
        Button hideCommentsButton = findViewById(R.id.close_comments_button);

        showCommentsButton.setOnClickListener(v -> {
            findViewById(R.id.content_container).setVisibility(View.GONE);
            commentsContainer.setVisibility(View.VISIBLE);
        });

        hideCommentsButton.setOnClickListener(v -> {
            findViewById(R.id.content_container).setVisibility(View.VISIBLE);
            commentsContainer.setVisibility(View.GONE);
        });

        // Handle comment submission
        submitCommentButton.setOnClickListener(v -> {
            if (UserState.isLoggedIn()) {
                String commentText = commentInput.getText().toString().trim();
                String displayName = UserState.getLoggedInUser().getDisplayName();
                String userImage = profilePicture;
                if (!commentText.isEmpty()) {
                    addComment(displayName, commentText, userImage);
                    commentInput.setText("");
                }
            } else {
                runOnUiThread(() -> Toast.makeText(VideoScreenActivity.this, "You need to be logged in to comment.", Toast.LENGTH_SHORT).show());
            }
        });

        // Initialize like and dislike buttons
        ImageButton likeButton = findViewById(R.id.like_button);
        ImageButton dislikeButton = findViewById(R.id.dislike_button);

        likeButton.setOnClickListener(v -> handleLikeDislike(true));
        dislikeButton.setOnClickListener(v -> handleLikeDislike(false));

        // Set click listeners for author name and image
        TextView authorName = findViewById(R.id.author_name);
        ImageView authorImage = findViewById(R.id.author_image);

        View.OnClickListener onAuthorClickListener = v -> navigateToUserVideos(currentVideo.getUsername());
        authorName.setOnClickListener(onAuthorClickListener);
        authorImage.setOnClickListener(onAuthorClickListener);
    }

    private void handleLikeDislike(boolean isLike) {
        if (currentVideo != null) {
            User loggedInUser = UserState.getLoggedInUser();
            if (loggedInUser != null) {
                String displayName = loggedInUser.getDisplayName();
                if (isLike) {
                    if (currentVideo.getLikes().contains(displayName)) {
                        currentVideo.getLikes().remove(displayName);
                        videoViewModel.likeVideo(currentVideo.getId(), displayName);
                    } else {
                        currentVideo.getLikes().add(displayName);
                        currentVideo.getDislikes().remove(displayName);
                        videoViewModel.likeVideo(currentVideo.getId(), displayName);
                    }
                } else {
                    if (currentVideo.getDislikes().contains(displayName)) {
                        currentVideo.getDislikes().remove(displayName);
                        videoViewModel.dislikeVideo(currentVideo.getId(), displayName);
                    } else {
                        currentVideo.getDislikes().add(displayName);
                        currentVideo.getLikes().remove(displayName);
                        videoViewModel.dislikeVideo(currentVideo.getId(), displayName);
                    }
                }
                updateLikeDislikeButtonColors();
            } else {
                Toast.makeText(this, "You need to be logged in to like or dislike.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateLikeDislikeButtonColors() {
        ImageButton likeButton = findViewById(R.id.like_button);
        ImageButton dislikeButton = findViewById(R.id.dislike_button);

        User loggedInUser = UserState.getLoggedInUser();
        if (loggedInUser != null) {
            String displayName = loggedInUser.getDisplayName();
            if (currentVideo.getLikes().contains(displayName)) {
                likeButton.setColorFilter(getResources().getColor(R.color.like_green));
            } else {
                likeButton.setColorFilter(null);
            }

            if (currentVideo.getDislikes().contains(displayName)) {
                dislikeButton.setColorFilter(getResources().getColor(R.color.dislike_red));
            } else {
                dislikeButton.setColorFilter(null);
            }
        } else {
            likeButton.setColorFilter(null);
            dislikeButton.setColorFilter(null);
        }
    }

    private void addComment(String displayName, String commentText, String userImage) {
        if (currentVideo != null) {
            User loggedInUser = UserState.getLoggedInUser();
            if (loggedInUser != null) {
                CommentData newComment = new CommentData();
                newComment.setText(commentText);
                newComment.setUsername(loggedInUser.getUsername());
                newComment.setDisplayName(loggedInUser.getDisplayName());
                newComment.setDate(getCurrentTime());
                newComment.setImg(userImage);
                newComment.setVideoId(currentVideo.getId());

                Log.d(TAG, "Creating comment with username: " + loggedInUser.getUsername() + " and displayName: " + loggedInUser.getDisplayName());

                commentViewModel.createComment(newComment);

                // Add the comment to the adapter immediately
                commentsAdapter.updateComments(Collections.singletonList(newComment));

                // Refresh the comments list after adding the new comment
                observeComments(currentVideo.getId());

            } else {
                Log.e(TAG, "User is not logged in");
            }
        } else {
            Log.e(TAG, "Current video is null");
        }
    }

    private void displayVideoDetails(VideoData video) {
        Log.e(TAG, "Displaying video");
        currentVideo = video;
        TextView titleTextView = findViewById(R.id.video_title);
        TextView viewsTextView = findViewById(R.id.video_views);
        TextView uploadTimeTextView = findViewById(R.id.video_uploadtime);
        TextView descriptionTextView = findViewById(R.id.video_description);
        TextView authorTextView = findViewById(R.id.author_name);
        ImageView authorImageView = findViewById(R.id.author_image);
        VideoView videoView = findViewById(R.id.video_view);

        titleTextView.setText(video.getTitle());
        viewsTextView.setText(video.getViews() + " views");
        uploadTimeTextView.setText(DataUtils.getTimeAgo(video.getUploadTime()));
        descriptionTextView.setText(video.getDescription());
        authorTextView.setText(video.getAuthor());

        // Load author image
        loadImage(video.getAuthorImage(), authorImageView);

        // Load video
        if (video.getVideo().startsWith("data:video")) {
            // Base64 encoded video
            String base64Video = video.getVideo().split(",")[1];
            byte[] decodedBytes = android.util.Base64.decode(base64Video, android.util.Base64.DEFAULT);

            // Create a temporary file to store the decoded video
            try {
                File tempFile = File.createTempFile("video", "mp4", getCacheDir());
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(decodedBytes);
                fos.close();

                Uri videoUri = Uri.fromFile(tempFile);
                videoView.setVideoURI(videoUri);

                // Add media controls to the VideoView
                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);

                videoView.setOnPreparedListener(mp -> {
                    mediaController.setAnchorView(videoView);
                    videoView.start();
                });

                videoView.start();
            } catch (IOException e) {
                Log.e(TAG, "Error loading video: " + e.getMessage());
            }
        } else if (video.getVideo().startsWith("http://") || video.getVideo().startsWith("https://")) {
            // Use Glide to load video
            Glide.with(this)
                    .asFile()
                    .load(video.getVideo())
                    .into(new CustomTarget<File>() {
                        @Override
                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                            Uri videoUri = Uri.fromFile(resource);
                            videoView.setVideoURI(videoUri);

                            // Add media controls to the VideoView
                            MediaController mediaController = new MediaController(VideoScreenActivity.this);
                            mediaController.setAnchorView(videoView);
                            videoView.setMediaController(mediaController);

                            videoView.setOnPreparedListener(mp -> {
                                mediaController.setAnchorView(videoView);
                                videoView.start();
                            });

                            videoView.start();
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Handle placeholder if needed
                        }
                    });
        } else {
            Uri videoUri = Uri.parse(video.getVideo());
            videoView.setVideoURI(videoUri);

            // Add media controls to the VideoView
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            videoView.setOnPreparedListener(mp -> {
                mediaController.setAnchorView(videoView);
                videoView.start();
            });

            videoView.start();
        }

        // Load and display comments
        observeComments(video.getId());

        // Update related videos
        fetchAndDisplayRecommendations(video);

        // Update the like and dislike button colors
        updateLikeDislikeButtonColors();
    }

    private void observeComments(String videoId) {
        commentViewModel.getComments(videoId).observe(this, comments -> {
            if (comments != null) {
                commentsAdapter.updateComments(reverseComments(comments));
            } else {
                commentsAdapter.updateComments(Collections.emptyList());
            }
        });
    }

    private void loadImage(String path, ImageView imageView) {
        try {
            if (path.startsWith("data:image")) {
                // Base64 encoded image
                String base64Image = path.split(",")[1];
                byte[] decodedString = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);
                Log.d(TAG, "Loaded base64 image.");
            } else if (path.startsWith("http://") || path.startsWith("https://")) {
                // URL
                Glide.with(imageView.getContext())
                        .load(path)
                        .into(imageView);
                Log.d(TAG, "Loaded image from URL: " + path);
            } else {
                // Check if the path is a drawable resource
                int resId = getResources().getIdentifier(path, "drawable", getPackageName());
                if (resId != 0) {
                    imageView.setImageResource(resId);
                    Log.d(TAG, "Loaded drawable resource: " + path);
                } else if (path.startsWith("content://") || path.startsWith("file://")) {
                    // Load from URI
                    Uri uri = Uri.parse(path);
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    Log.d(TAG, "Loaded image from URI: " + path);
                } else {
                    // Load from local file path
                    Converters converter = new Converters();
                    Bitmap bitmap = converter.toBitmap(path);
                    imageView.setImageBitmap(bitmap);
                    Log.d(TAG, "Loaded image from local file path: " + path);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage());
        }
    }

    private void loadUserProfileImage(String profilePicture, ImageView userImageInput) {
        if (profilePicture.startsWith("http://") || profilePicture.startsWith("https://")) {
            Glide.with(userImageInput.getContext())
                    .load(profilePicture)
                    .into(userImageInput);
            Log.d(TAG, "Loaded image from URL: " + profilePicture);
        } else {
            Converters converter = new Converters();
            Bitmap bitmap = converter.toBitmap(profilePicture);
            userImageInput.setImageBitmap(bitmap);
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        return sdf.format(new Date());
    }

    private List<CommentData> reverseComments(List<CommentData> comments) {
        List<CommentData> reversedComments = new ArrayList<>(comments);
        Collections.reverse(reversedComments);
        return reversedComments;
    }

    private void playSelectedVideo(VideoData selectedVideo) {
        // Check if the video is already being displayed to avoid redundant updates
        if (!isFinishing() && !selectedVideo.getId().equals(currentlyDisplayedVideoId)) {
            displayVideoDetails(selectedVideo);
            currentlyDisplayedVideoId = selectedVideo.getId();
            resetScrollPosition();
        }
    }

    private void updateRelatedVideos(VideoData currentVideo) {
        if (originalVideoList != null) {
            List<VideoData> filteredVideos = new ArrayList<>();
            for (VideoData video : originalVideoList) {
                if (!video.getId().equals(currentVideo.getId())) {
                    filteredVideos.add(video);
                }
            }
            videoAdapter.updateVideoList(filteredVideos);
        } else {
            Log.e(TAG, "originalVideoList is null when trying to update related videos.");
        }
    }

    private void resetScrollPosition() {
        // Reset the scroll position to the top
        nestedScrollView.scrollTo(0, 0);
    }

    private VideoData findVideoById(String id) {
        if (originalVideoList != null) {
            for (VideoData video : originalVideoList) {
                if (video.getId() != null && video.getId().equals(id)) {
                    return video;
                }
            }
        }
        return null;
    }

    private void navigateToUserVideos(String username) {
        Intent intent = new Intent(VideoScreenActivity.this, UserVideosActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void fetchAndDisplayRecommendations(VideoData currentVideo) {
        String videoId = currentVideo.getId();
        User loggedInUser = UserState.getLoggedInUser();
        String userId = loggedInUser != null ? loggedInUser.getId() : "null";

        videoViewModel.getRecommendations(videoId, userId).observe(this, recommendedVideos -> {
            if (recommendedVideos != null) {
                videoAdapter.updateVideoList(recommendedVideos);
            } else {
                Log.e(TAG, "Failed to fetch recommended videos");
            }
        });
    }
}
