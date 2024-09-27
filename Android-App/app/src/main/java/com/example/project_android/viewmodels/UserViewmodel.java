package com.example.project_android.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.project_android.entities.User;
import com.example.project_android.repositories.UsersRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewmodel extends AndroidViewModel {
    private UsersRepository usersRepository;
    private LiveData<List<User>> users;

    public UserViewmodel(@NonNull Application application) {
        super(application);
        usersRepository = new UsersRepository(application);
        users = usersRepository.getUsers();
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public void createUser(User user) {
        usersRepository.createUser(user, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("UserViewModel", "User created successfully: " + response.body().toString());
                } else {
                    // Handle error response
                    Log.d("UserViewModel", "Failed to create user: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Handle failure
                Log.d("UserViewModel", "Error creating user: " + t.getMessage());
            }
        });
    }

    public void deleteUser(String userId) {
        usersRepository.deleteUser(userId, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("UserViewModel", "User deleted successfully");
                } else {
                    Log.d("UserViewModel", "Failed to delete user: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("UserViewModel", "Error deleting user: " + t.getMessage());
            }
        });
    }

    public void updateUser(String userId, User user) {
        usersRepository.updateUser(userId, user, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("UserViewModel", "User updated successfully: " + response.body().toString());
                } else {
                    Log.d("UserViewModel", "Failed to update user: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("UserViewModel", "Error updating user: " + t.getMessage());
            }
        });
    }
}
