package com.example.project_android;

import android.graphics.Bitmap;

import com.example.project_android.entities.User;

public class UserState {
    private static User loggedInUser;

    private UserState() {
        // Private constructor to prevent instantiation
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static void logOut() {
        loggedInUser = null;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }
}
