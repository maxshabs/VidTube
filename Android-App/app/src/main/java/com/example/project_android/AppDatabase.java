package com.example.project_android;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import android.content.Context;

import com.example.project_android.dao.CommentDao;
import com.example.project_android.dao.UserDao;
import com.example.project_android.dao.VideoDao;
import com.example.project_android.entities.CommentData;
import com.example.project_android.entities.User;
import com.example.project_android.entities.VideoData;

@Database(entities = {VideoData.class, User.class, CommentData.class} , version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    public abstract VideoDao videoDao();
    public abstract UserDao userDao();
    public abstract CommentDao commentDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}