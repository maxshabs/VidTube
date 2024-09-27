package com.example.project_android.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.project_android.entities.User;

import java.util.List;

@Dao
public interface UserDao {
    // Insert a single user
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    // Update a user
    @Update
    void updateUser(User user);

    // Delete a user
    @Delete
    void deleteUser(User user);

    // Fetch a user by ID
    @Query("SELECT * FROM users WHERE _id = :userId")
    LiveData<User> getUserById(String userId);

    // Fetch a user by Username
    @Query("SELECT * FROM users WHERE username = :username")
    LiveData<User> getUserByUsername(String username);

    // Fetch a user by Username and Password
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    LiveData<User> getUserByUsernameAndPassword(String username, String password);

    // Fetch all users
    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();
}
