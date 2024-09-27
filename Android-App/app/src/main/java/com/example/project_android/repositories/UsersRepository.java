package com.example.project_android.repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_android.TokenManager;
import com.example.project_android.UserState;
import com.example.project_android.api.ApiService;
import com.example.project_android.api.RetrofitClient;

import com.example.project_android.AppDatabase;
import com.example.project_android.dao.UserDao;
import com.example.project_android.entities.User;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersRepository {
    private ApiService apiService;
    String temptoken = TokenManager.getInstance().getToken();
    private static final String TAG = "MyActivity";


    public UsersRepository(Application application) {
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<List<User>> getUsers() {
        MutableLiveData<List<User>> users = new MutableLiveData<>();
        apiService.getUsers("Bearer " + temptoken).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> userList = response.body();
                    users.setValue(userList);
                    if (userList != null) {
                        for (User user : userList) {
                            user.setUrlForEmulator();
                            Log.d(TAG, "User: " + user.getUsername() + ", " + user.getPassword());
                        }
                    }
                } else {
                    // Handle error
                    Log.e(TAG, "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // Handle failure
                Log.e(TAG, "Failure: " + t.getMessage());
            }
        });
        return users;
    }

    public LiveData<User> getUser( String userId) {
        MutableLiveData<User> user = new MutableLiveData<>();
        apiService.getUser("Bearer " + temptoken, userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user.setValue(response.body());
                } else {
                    // Handle error
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Handle failure
            }
        });
        return user;
    }

    public void createUser(User user, Callback<User> callback) {
        apiService.createUser(user).enqueue(callback);
    }

    public void signInUser(User user, SignInCallback callback) {
        apiService.signInUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //adding user to state
                    UserState.setLoggedInUser(response.body());
                    // User signed in successfully, now fetch the token
                    fetchTokenAndProceed(user, callback);
                } else {
                    // Handle unsuccessful sign-in
                    callback.onSignInFailed("Failed to sign in");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onSignInFailed("Sign in failed: " + t.getMessage());
            }
        });
    }

    private void fetchTokenAndProceed(User user, SignInCallback callback) {
        apiService.generateToken(user).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonElement jsonElement = response.body();
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    if (jsonObject.has("token")) {
                        String token = jsonObject.get("token").getAsString();
                        Log.d(TAG, "Token success: " + token);
                        // Set token in TokenManager
                        TokenManager.getInstance().setToken(token);
                        // Notify success
                        callback.onSignInSuccess();
                    } else {
                        Log.e(TAG, "Token field missing in response");
                        callback.onSignInFailed("Failed to fetch token: Token field missing");
                    }
                } else {
                    // Handle error
                    String errorBody = response.errorBody() != null ? response.errorBody().toString() : "Unknown error";
                    Log.e(TAG, "Failed to fetch token: " + errorBody);
                    callback.onSignInFailed("Failed to fetch token: " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                // Handle failure
                Log.e(TAG, "Failed to fetch token: " + t.getMessage());
                callback.onSignInFailed("Failed to fetch token: " + t.getMessage());
            }
        });
    }

    public interface SignInCallback {
        void onSignInSuccess();
        void onSignInFailed(String errorMsg);
    }

    public void updateUser( String userId, User user, Callback<User> callback) {
        apiService.updateUser("Bearer " + temptoken, userId, user).enqueue(callback);
    }

    public void deleteUser( String userId, Callback<Void> callback) {
        apiService.deleteUser("Bearer " + temptoken, userId).enqueue(callback);
    }
}
