package com.example.project_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_android.entities.VideoData;
import com.example.project_android.viewmodels.VideoViewModel;

import java.io.File;
import java.io.IOException;

public class EditVideo extends AppCompatActivity {

    private static final int REQUEST_THUMBNAIL_GET = 1;
    private static final int REQUEST_VIDEO_GET = 2;

    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textViewVideoDetails;
    private ImageView imageViewThumbnail;
    private Button buttonUploadThumbnail;
    private Button buttonUploadVideo;
    private Button buttonSubmitVideo;
    private TextView textViewError;

    private Uri selectedThumbnailUri;
    private Uri selectedVideoUri;
    private VideoData videoData;
    private VideoViewModel videoViewModel;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);

        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonUploadThumbnail = findViewById(R.id.buttonUploadThumbnail);
        buttonUploadVideo = findViewById(R.id.buttonUploadVideo);
        buttonSubmitVideo = findViewById(R.id.buttonSubmitVideo);
        textViewError = findViewById(R.id.textViewError);
        imageViewThumbnail = findViewById(R.id.imageViewThumbnail);
        textViewVideoDetails = findViewById(R.id.textViewVideoDetails);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating video...");
        progressDialog.setCancelable(false);

        Intent intent = getIntent();
        String videoId = intent.getStringExtra("video_id");
        if (videoId != null) {
            videoViewModel.getVideoById(videoId).observe(this, new Observer<VideoData>() {
                @Override
                public void onChanged(VideoData video) {
                    if (video != null) {
                        videoData = video;
                        populateVideoDetails(videoData);
                    } else {
                        Toast.makeText(EditVideo.this, "Error fetching video details.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        buttonUploadThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryForThumbnail();
            }
        });

        buttonUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryForVideo();
            }
        });

        buttonSubmitVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVideo();
            }
        });

        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to HomePage
                Intent intent = new Intent(EditVideo.this, HomePage.class);
                startActivity(intent);
            }
        });
    }

    private void populateVideoDetails(VideoData video) {
        editTextTitle.setText(video.getTitle());
        editTextDescription.setText(video.getDescription());
        selectedThumbnailUri = Uri.parse(video.getImg());
        selectedVideoUri = Uri.parse(video.getVideo());
        try {
            Bitmap thumbnailBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedThumbnailUri);
            imageViewThumbnail.setImageBitmap(thumbnailBitmap);
        } catch (IOException e) {
            Log.e("EditVideoActivity", "Error loading thumbnail: " + e.getMessage());
        }
        textViewVideoDetails.setText("Video File Successfully Loaded");
    }

    private void openGalleryForThumbnail() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_THUMBNAIL_GET);
    }

    private void openGalleryForVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_VIDEO_GET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (requestCode == REQUEST_THUMBNAIL_GET) {
                selectedThumbnailUri = uri;
                try {
                    Bitmap thumbnailBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedThumbnailUri);
                    imageViewThumbnail.setImageBitmap(thumbnailBitmap);
                    imageViewThumbnail.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    Log.e("EditVideoActivity", "Error loading thumbnail: " + e.getMessage());
                }
            } else if (requestCode == REQUEST_VIDEO_GET) {
                selectedVideoUri = uri;
                textViewVideoDetails.setText("Video File Successfully Loaded");
                textViewVideoDetails.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateVideo() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            textViewError.setText("Please fill all fields to upload.");
            Log.d("EditVideoActivity", "Error: Please fill all fields to upload.");
            textViewError.setVisibility(View.VISIBLE);
        } else {
            textViewError.setVisibility(View.GONE);

            String thumbnailPath = selectedThumbnailUri != null ? DataUtils.getPath(this, selectedThumbnailUri) : null;
            String videoPath = selectedVideoUri != null ? DataUtils.getPath(this, selectedVideoUri) : null;

            progressDialog.show();

            videoViewModel.updateVideo(
                    "Bearer " + TokenManager.getInstance().getToken(),
                    UserState.getLoggedInUser().getUsername(),
                    videoData.getId(),
                    thumbnailPath != null ? new File(thumbnailPath) : null,
                    videoPath != null ? new File(videoPath) : null,
                    title,
                    description
            ).observe(this, new Observer<VideoData>() {
                @Override
                public void onChanged(VideoData videoData) {
                    progressDialog.dismiss();
                    if (videoData != null) {
                        Toast.makeText(EditVideo.this, "Video successfully updated.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditVideo.this, HomePage.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(EditVideo.this, "Error updating video.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
