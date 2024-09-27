package com.example.project_android;

import android.content.Intent;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.project_android.adapters.VideosListAdapter;
import com.example.project_android.api.VideoApi;
import com.example.project_android.entities.User;
import com.example.project_android.entities.VideoData;
import com.example.project_android.viewmodels.VideoViewModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {
    private static final String TAG = "HomePage";
    private static final int EDIT_VIDEO_REQUEST_CODE = 1;

    private SwipeRefreshLayout swipeRefreshLayout;
    private VideosListAdapter adapter;
    private List<VideoData> allVideos;
    private DrawerLayout drawerLayout;
    private Button toggleModeButton;
    private ImageView toggleModeIcon;
    private Button signInButton;
    private Button signUpButton;
    private ImageView profileImage;
    private TextView welcomeMessage;
    private LinearLayout profileContainer;
    private LinearLayout authButtonsContainer;
    private Button signOutButton;
    private Button uploadVideoButton;

    private VideoViewModel videoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);

        RecyclerView listVideos = findViewById(R.id.listVideos);
        adapter = new VideosListAdapter(this, new VideosListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(VideoData video) {
                Log.d(TAG, "Video ID clicked: " + video.getId());
                Intent intent = new Intent(HomePage.this, VideoScreenActivity.class);
                intent.putExtra("video_id", video.getId());
                startActivity(intent);
            }

            @Override
            public void onEditClick(VideoData video) {
                Intent intent = new Intent(HomePage.this, EditVideo.class);
                intent.putExtra("video_id", video.getId());
                startActivityForResult(intent, EDIT_VIDEO_REQUEST_CODE);
            }

            @Override
            public void onDeleteClick(VideoData video) {
                String token = "Bearer " + TokenManager.getInstance().getToken();
                String userId = UserState.getLoggedInUser().getUsername(); // Use username as userId
                videoViewModel.deleteVideo(token, userId, video.getId()).observe(HomePage.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean success) {
                        if (success) {
                            videoViewModel.syncWithServerAfterDeletion();
                            Toast.makeText(HomePage.this, "Video successfully deleted.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomePage.this, "Error deleting video.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        listVideos.setAdapter(adapter);
        listVideos.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                videoViewModel.getLimitedVideos().observe(HomePage.this, new Observer<List<VideoData>>() {
                    @Override
                    public void onChanged(List<VideoData> videos) {
                        if (videos != null) {
                            allVideos = videos;
                            adapter.setVideos(videos);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        videoViewModel.getLimitedVideos().observe(this, new Observer<List<VideoData>>() {
            @Override
            public void onChanged(List<VideoData> videos) {
                if (videos != null) {
                    allVideos = videos;
                    adapter.setVideos(videos);
                }
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton searchButton = findViewById(R.id.search_button);
        final SearchView searchView = findViewById(R.id.searchView);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchView.getVisibility() == View.GONE) {
                    searchView.setVisibility(View.VISIBLE);
                } else {
                    searchView.setVisibility(View.GONE);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterVideos(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterVideos(newText);
                return false;
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(findViewById(R.id.sidebar))) {
                    drawerLayout.closeDrawer(findViewById(R.id.sidebar));
                } else {
                    drawerLayout.openDrawer(findViewById(R.id.sidebar));
                }
            }
        });

        LinearLayout menuHome = findViewById(R.id.menu_home);
        LinearLayout menuShorts = findViewById(R.id.menu_shorts);
        LinearLayout menuSubscriptions = findViewById(R.id.menu_subscriptions);
        LinearLayout menuYou = findViewById(R.id.menu_you);
        LinearLayout menuHistory = findViewById(R.id.menu_history);

        View.OnClickListener menuClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(findViewById(R.id.sidebar));
            }
        };

        menuHome.setOnClickListener(menuClickListener);
        menuShorts.setOnClickListener(menuClickListener);
        menuSubscriptions.setOnClickListener(menuClickListener);
        menuHistory.setOnClickListener(menuClickListener);

        toggleModeButton = findViewById(R.id.btn_toggle_mode);
        toggleModeIcon = findViewById(R.id.toggle_mode_icon);
        updateModeButtonText(); // Set initial text based on current mode
        toggleModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nightMode = AppCompatDelegate.getDefaultNightMode();
                if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                recreate();
            }
        });

        signInButton = findViewById(R.id.btn_sign_in);
        signUpButton = findViewById(R.id.btn_sign_up);
        authButtonsContainer = findViewById(R.id.auth_buttons_container);
        profileImage = findViewById(R.id.profile_image);
        profileContainer = findViewById(R.id.profile_container);
        welcomeMessage = findViewById(R.id.welcome_message);
        signOutButton = findViewById(R.id.btn_sign_out);
        uploadVideoButton = findViewById(R.id.btn_upload_video);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserState.logOut();
                authButtonsContainer.setVisibility(View.VISIBLE);
                profileContainer.setVisibility(View.GONE);
                uploadVideoButton.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        });

        menuYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserState.isLoggedIn()) {
                    Intent intent = new Intent(HomePage.this, EditUserDetails.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomePage.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        uploadVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, UploadVideo.class);
                startActivity(intent);
            }
        });

        // Check if user is logged in
        checkUserState();

        if (UserState.isLoggedIn()){
            User loggedInUser = UserState.getLoggedInUser();
            String username = loggedInUser.getUsername();
            View.OnClickListener onAuthorClickListener = v -> navigateToUserVideos(username);
            profileImage.setOnClickListener(onAuthorClickListener);
            welcomeMessage.setOnClickListener(onAuthorClickListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserState();
        videoViewModel.getLimitedVideos().observe(this, new Observer<List<VideoData>>() {
            @Override
            public void onChanged(List<VideoData> videos) {
                if (videos != null) {
                    allVideos = videos;
                    adapter.setVideos(videos);
                }
            }
        });
    }

    private void checkUserState() {
        if (UserState.isLoggedIn()) {
            User loggedInUser = UserState.getLoggedInUser();
            if (loggedInUser != null) {
                authButtonsContainer.setVisibility(View.GONE);
                profileContainer.setVisibility(View.VISIBLE);
                uploadVideoButton.setVisibility(View.VISIBLE);
                welcomeMessage.setText("Welcome " + loggedInUser.getDisplayName() + "!");

                // Replace localhost with the actual server IP address
                String profilePicture = loggedInUser.getProfilePicture().replace("localhost", "10.0.2.2");

                // load profile image
                if (profilePicture.startsWith("http://") || profilePicture.startsWith("https://")) {
                    // URL
                    Glide.with(profileImage.getContext())
                            .load(profilePicture)
                            .into(profileImage);
                    Log.d(TAG, "Loaded image from URL: " + profilePicture);
                    Log.d(TAG, "image in profileImage: " + profileImage.getDrawable());

                } else {
                    Converters converter = new Converters();
                    Bitmap bitmap = converter.toBitmap(loggedInUser.getProfilePicture());
                    profileImage.setImageBitmap(bitmap);
                }
            }
        } else {
            authButtonsContainer.setVisibility(View.VISIBLE);
            profileContainer.setVisibility(View.GONE);
            uploadVideoButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_VIDEO_REQUEST_CODE && resultCode == RESULT_OK) {
            // Update the video list
            videoViewModel.getLimitedVideos().observe(this, new Observer<List<VideoData>>() {
                @Override
                public void onChanged(List<VideoData> videos) {
                    if (videos != null) {
                        allVideos = videos;
                        adapter.setVideos(videos);
                    }
                }
            });
        }
    }

    private void filterVideos(String text) {
        if (allVideos == null) return;
        List<VideoData> filteredList = new ArrayList<>();
        for (VideoData video : allVideos) {
            if (video.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(video);
            }
        }
        adapter.setVideos(filteredList);
    }

    private void updateModeButtonText() {
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            toggleModeIcon.setImageResource(R.drawable.ic_light_mode);
        } else {
            toggleModeIcon.setImageResource(R.drawable.ic_dark_mode);
        }
    }

    private void loadImageFromLocalPath(String path, ImageView imageView) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage());
        }
    }
    private void navigateToUserVideos(String username) {
        Intent intent = new Intent(HomePage.this, UserVideosActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

}
