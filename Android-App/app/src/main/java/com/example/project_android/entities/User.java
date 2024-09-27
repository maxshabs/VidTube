package com.example.project_android.entities;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    private String _id;
    private String username;
    private String password;
    private String displayName;
    private String profilePicture;



    public User(String username, String password, String displayName, String profilePicture) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.profilePicture = profilePicture;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setUrlForEmulator() {
        if (this.profilePicture != null) {
            this.profilePicture = this.profilePicture.replace("localhost", "10.0.2.2");
        }
    }
}
