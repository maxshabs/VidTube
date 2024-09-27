package com.example.project_android;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.project_android.entities.User;
import com.example.project_android.repositories.UsersRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_GET = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_PERMISSION_READ_MEDIA_IMAGES = 3;
    private static final int REQUEST_PERMISSION_CAMERA = 4;

    private ImageView imageViewProfile;
    private Uri selectedImageUri;
    private TextView textViewPasswordRequirements;
    private TextView textViewPasswordMatch;
    private TextView textViewImageError;
    private TextView textViewDisplayNameError;
    private Uri cameraImageUri;
    private String base64Image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        EditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        EditText editTextDisplayName = findViewById(R.id.editTextDisplayName);
        Button buttonUploadImage = findViewById(R.id.buttonUploadImage);
        Button buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        Button buttonSignup = findViewById(R.id.buttonSignup);
        textViewPasswordRequirements = findViewById(R.id.textViewPasswordRequirements);
        textViewPasswordMatch = findViewById(R.id.textViewPasswordMatch);
        textViewImageError = findViewById(R.id.textViewImageError);
        textViewDisplayNameError = findViewById(R.id.textViewDisplayNameError);

        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SignupActivity", "Upload image button clicked");
                checkPermissionAndOpenGallery();
            }
        });

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SignupActivity", "Take photo button clicked");
                checkPermissionAndOpenCamera();
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();
                String displayName = editTextDisplayName.getText().toString().trim();

                // Check if password meets requirements
                if (!isPasswordValid(password)) {
                    textViewPasswordRequirements.setVisibility(View.VISIBLE);
                    textViewPasswordRequirements.setText("Password must have at least 8 characters, 1 uppercase, 1 lowercase, 1 number, and 1 special character.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignupActivity.this, "Invalid password, read the guidelines", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                // Check if passwords match
                if (!password.equals(confirmPassword)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                // Check if display name is filled
                if (displayName.isEmpty()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignupActivity.this, "Display name is required.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                } else {
                    textViewDisplayNameError.setVisibility(View.GONE);
                }

                // Check if image is selected
                if (selectedImageUri == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignupActivity.this, "Please choose a profile picture.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                // Copy the image to the app's internal storage
                String imageUri = copyImageToInternalStorage(selectedImageUri);
                if (imageUri == null) {
                    textViewImageError.setVisibility(View.VISIBLE);
                    textViewImageError.setText("Error saving profile picture.");
                    return;
                }

                // Convert the image to Base64
                String base64Image = convertImageToBase64(selectedImageUri);
                if (base64Image == null) {
                    textViewImageError.setVisibility(View.VISIBLE);
                    textViewImageError.setText("Error converting profile picture.");
                    return;
                }

                // Call createUser method from the repository
                Application application = getApplication();
                Log.d("SignupActivity", "Image converted to Base64: " + base64Image);
                User user = new User(username, password, displayName, base64Image);
                UsersRepository usersRepository = new UsersRepository(application);
                usersRepository.createUser(user, new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignupActivity.this, "Signed up successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {
                            // Handle error
                            Log.e("SignupActivity", "Error: " + response.code());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String errorMessage = "Error signing up";
                                    try {
                                        JsonObject errorResponse = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                                        JsonArray errors = errorResponse.getAsJsonArray("errors");
                                        if (errors != null && errors.size() > 0) {
                                            errorMessage = errors.get(0).getAsString();
                                        }
                                    } catch (Exception e) {
                                        Log.e("SignupActivity", "Error parsing error response: " + e.getMessage());
                                    }
                                    Toast.makeText(SignupActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        // Handle failure
                        Log.e("SignupActivity", "Failure: " + t.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignupActivity.this, "Failed to sign up: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    private boolean isPasswordValid(String password) {
        // Password validation logic
        return password.length() >= 8 && password.matches(".*\\d.*") && password.matches(".*[a-z].*") &&
                password.matches(".*[A-Z].*") && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }

    private void checkPermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSION_READ_MEDIA_IMAGES);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_MEDIA_IMAGES);
            } else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }

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

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_MEDIA_IMAGES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    imageViewProfile.setImageBitmap(bitmap);
                    textViewImageError.setVisibility(View.GONE);
                    Toast.makeText(SignupActivity.this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.e("SignupActivity", "Error loading image: " + e.getMessage());
                }
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageViewProfile.setImageBitmap(bitmap);
                // Save the captured image URI or path
                selectedImageUri = getImageUriFromBitmap(bitmap);
                textViewImageError.setVisibility(View.GONE);
            }
        }
    }

    private String copyImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            File directory = getFilesDir();
            File file = new File(directory, "profile_image_" + System.currentTimeMillis() + ".png");

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e("SignupActivity", "Error saving image: " + e.getMessage());
            return null;
        }
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return "data:image/png;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            Log.e("SignupActivity", "Error converting image: " + e.getMessage());
            return null;
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        // Save the bitmap to a temporary file and return its URI
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "ProfilePicture", null);
        return Uri.parse(path);
    }
}
