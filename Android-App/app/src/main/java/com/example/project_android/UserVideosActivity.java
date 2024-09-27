package com.example.project_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_android.adapters.VideosListAdapter;
import com.example.project_android.entities.VideoData;
import com.example.project_android.viewmodels.VideoViewModel;

import java.util.List;

public class UserVideosActivity extends AppCompatActivity {
    private static final String TAG = "UserVideosActivity";
    private VideoViewModel videoViewModel;
    private VideosListAdapter adapter;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_videos);

        TextView titleTextView = findViewById(R.id.user_videos_title);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            username = intent.getStringExtra("username");
            titleTextView.setText(username + "'s Videos:");
        } else {
            Toast.makeText(this, "Username not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.user_videos_recycler_view);
        adapter = new VideosListAdapter(this, new VideosListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(VideoData video) {
                Log.d(TAG, "Video ID clicked: " + video.getId());
                Intent intent = new Intent(UserVideosActivity.this, VideoScreenActivity.class);
                intent.putExtra("video_id", video.getId());
                startActivity(intent);
            }

            @Override
            public void onEditClick(VideoData video) {
                Intent intent = new Intent(UserVideosActivity.this, EditVideo.class);
                intent.putExtra("video_id", video.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(VideoData video) {
                String token = "Bearer " + TokenManager.getInstance().getToken();
                String userId = UserState.getLoggedInUser().getUsername();
                videoViewModel.deleteVideo(token, userId, video.getId()).observe(UserVideosActivity.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean success) {
                        if (success) {
                            videoViewModel.syncWithServerAfterDeletion();
                            Toast.makeText(UserVideosActivity.this, "Video successfully deleted.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserVideosActivity.this, "Error deleting video.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadUserVideos();
    }

    private void loadUserVideos() {
        videoViewModel.getVideosByAuthor(username).observe(this, new Observer<List<VideoData>>() {
            @Override
            public void onChanged(List<VideoData> videos) {
                if (videos != null) {
                    adapter.setVideos(videos);
                }
            }
        });
    }
}
