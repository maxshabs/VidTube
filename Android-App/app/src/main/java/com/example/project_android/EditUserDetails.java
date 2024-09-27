package com.example.project_android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.project_android.api.ApiService;
import com.example.project_android.api.RetrofitClient;
import com.example.project_android.entities.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserDetails extends AppCompatActivity {

    private static final int REQUEST_IMAGE_GET = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 20;
    private static final int REQUEST_PERMISSION_READ_MEDIA_IMAGES = 3;
    private static final int REQUEST_PERMISSION_CAMERA = 40;
    private EditText editTextUsername;
    private EditText editTextDisplayName;
    private EditText editTextPassword;
    private ImageView imageViewProfile;
    private Button buttonUploadImage;
    private Button buttonTakePhoto;
    private Button buttonSaveChanges;
    private Button buttonDeleteUser;

    private Uri selectedImageUri;
    private Bitmap selectedImageBitmap;
    private String base64ProfilePicture;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_details);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextDisplayName = findViewById(R.id.editTextDisplayName);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        buttonTakePhoto = findViewById(R.id.buttonTakePhoto2);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        buttonDeleteUser = findViewById(R.id.button2);
        imageViewProfile = findViewById(R.id.imageViewProfile2);

        apiService = RetrofitClient.getApiService();

        // Load current user details
        loadUserDetails();

        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndOpenCamera();
            }
        });

        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserDetails();
            }
        });

        buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });
    }

    private void loadUserDetails() {
        User currentUser = UserState.getLoggedInUser();
        if (currentUser != null) {
            editTextUsername.setText(currentUser.getUsername());
            editTextDisplayName.setText(currentUser.getDisplayName());
        }
    }

    private void saveUserDetails() {
        String username = editTextUsername.getText().toString().trim();
        String displayName = editTextDisplayName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate inputs
        if (displayName.isEmpty()) {
            editTextDisplayName.setError("Display Name is required.");
            return;
        }
        // Validate password if it is not empty
        if (!password.isEmpty() && !isPasswordValid(password)) {
            editTextPassword.setError("Password must have at least 8 characters, 1 uppercase, 1 lowercase, 1 number, and 1 special character.");
            return;
        }

        // If username or password is empty, retain existing values from UserState
        User loggedInUser = UserState.getLoggedInUser();
        if (username.isEmpty()) {
            username = loggedInUser.getUsername();
        }
        if (password.isEmpty()) {
            password = loggedInUser.getPassword(); // If not editable, consider security implications
        }

        // Convert image to base64 if a new image is selected
        if (selectedImageUri != null) {
            base64ProfilePicture = convertImageToBase64(selectedImageUri);
            if (base64ProfilePicture == null) {
                Toast.makeText(this, "Failed to upload profile picture.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Create updated user object
        User updatedUser = new User(username, password, displayName, base64ProfilePicture);

        TokenManager tokenManager = TokenManager.getInstance();

        // Update user details via API
        apiService.updateUser("Bearer " + tokenManager.getToken(), loggedInUser.getUsername(), updatedUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User updatedUser = response.body();
                    UserState.setLoggedInUser(updatedUser); // Update UserState with updated user

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditUserDetails.this, "User details updated successfully.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditUserDetails.this, HomePage.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String errorMessage = "Failed to update user details.";
                            try {
                                JsonObject errorResponse = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                                JsonArray errors = errorResponse.getAsJsonArray("errors");
                                if (errors != null && errors.size() > 0) {
                                    errorMessage = errors.get(0).getAsString();
                                }
                            } catch (Exception e) {
                                Log.e("EditUserDetails", "Error parsing error response: " + e.getMessage());
                            }
                            Toast.makeText(EditUserDetails.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("EditUserDetails", "Error updating user details: " + t.getMessage());
                Toast.makeText(EditUserDetails.this, "Error updating user details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }

    private void deleteUser() {
        User loggedInUser = UserState.getLoggedInUser();
        if (loggedInUser != null) {
            String userId = loggedInUser.getUsername();

            // Call the API to delete the user
            TokenManager tokenManager = TokenManager.getInstance();
            apiService.deleteUser("Bearer " + tokenManager.getToken(), userId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        // Show success message on the UI thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(EditUserDetails.this, "User deleted successfully.", Toast.LENGTH_SHORT).show();
                                UserState.logOut();
                                Intent intent = new Intent(EditUserDetails.this, HomePage.class);
                                startActivity(intent);
                                finish(); // Close current activity
                            }
                        });
                    } else {
                        // Show failure message on the UI thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(EditUserDetails.this, "Failed to delete user.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Show failure message on the UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("EditUserDetails", "Error deleting user: " + t.getMessage());
                            Toast.makeText(EditUserDetails.this, "Error deleting user.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    // Camera and Permissions methods
    private void checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CAMERA);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        try {
            Intent takePictureIntent = new Intent();
            takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (Exception e) {
            Log.e("SignupActivity", "Error opening camera: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                imageViewProfile.setImageBitmap(selectedImageBitmap);
                // Convert the selected image to base64 if necessary
                base64ProfilePicture = convertImageToBase64(selectedImageUri);
                // Handle the bitmap, e.g., display in ImageView or other logic
            } catch (IOException e) {
                Log.e("EditUserDetails", "Error loading image: " + e.getMessage());
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                selectedImageBitmap = (Bitmap) data.getExtras().get("data");
                imageViewProfile.setImageBitmap(selectedImageBitmap);
                // Save the captured image URI or path
                selectedImageUri = getImageUriFromBitmap(selectedImageBitmap);
            }
        }
    }

    // Image format converters
    private String convertImageToBase64(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return "data:image/png;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            Log.e("EditUserDetails", "Error converting image to base64: " + e.getMessage());
            return null;
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "ProfilePicture", null);
        return Uri.parse(path);
    }
    private boolean isPasswordValid(String password) {
        // Password validation logic
        return password.length() >= 8 && password.matches(".*\\d.*") && password.matches(".*[a-z].*") &&
                password.matches(".*[A-Z].*") && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }
}
