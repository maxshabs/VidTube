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

import com.example.project_android.api.VideoApi;
import com.example.project_android.entities.VideoData;
import com.example.project_android.viewmodels.VideoViewModel;

import java.io.File;
import java.io.IOException;

public class UploadVideo extends AppCompatActivity {

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
    private Button buttonCancel;

    private Uri selectedThumbnailUri;
    private Uri selectedVideoUri;
    private VideoViewModel videoViewModel;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonUploadThumbnail = findViewById(R.id.buttonUploadThumbnail);
        buttonUploadVideo = findViewById(R.id.buttonUploadVideo);
        buttonSubmitVideo = findViewById(R.id.buttonSubmitVideo);
        textViewError = findViewById(R.id.textViewError);
        imageViewThumbnail = findViewById(R.id.imageViewThumbnail);
        textViewVideoDetails = findViewById(R.id.textViewVideoDetails);
        Button buttonCancel = findViewById(R.id.buttonCancel);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading video...");
        progressDialog.setCancelable(false);

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
                submitVideo();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                    Log.e("UploadVideo", "Error loading thumbnail: " + e.getMessage());
                }
            } else if (requestCode == REQUEST_VIDEO_GET) {
                selectedVideoUri = uri;
                Toast.makeText(this, "Video File Successfully Loaded.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void submitVideo() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String uploadTime = DataUtils.getCurrentDateTime(); // Use DataUtils to get current date and time

        if (title.isEmpty() || description.isEmpty() || selectedThumbnailUri == null || selectedVideoUri == null) {
            Toast.makeText(this, "Please fill all fields to upload.", Toast.LENGTH_SHORT).show();
            Log.d("UploadVideo", "Error: Please fill all fields to upload.");
        } else {
            textViewError.setVisibility(View.GONE);

            String thumbnailPath = DataUtils.getPath(this, selectedThumbnailUri);
            String videoPath = DataUtils.getPath(this, selectedVideoUri);

            progressDialog.show();

            videoViewModel.uploadVideo(
                    "Bearer " + TokenManager.getInstance().getToken(),
                    UserState.getLoggedInUser().getUsername(), // Use username as userId
                    new File(thumbnailPath),
                    new File(videoPath),
                    title,
                    description,
                    UserState.getLoggedInUser().getDisplayName(),
                    UserState.getLoggedInUser().getUsername(),
                    UserState.getLoggedInUser().getProfilePicture(),
                    uploadTime
            ).observe(this, new Observer<VideoData>() {
                @Override
                public void onChanged(VideoData videoData) {
                    progressDialog.dismiss();
                    if (videoData != null) {
                        Toast.makeText(UploadVideo.this, "Video successfully uploaded to Vidtube.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UploadVideo.this, HomePage.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(UploadVideo.this, "Error uploading video.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
