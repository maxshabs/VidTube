package com.example.project_android.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.project_android.entities.CommentData;
import com.example.project_android.repositories.CommentRepository;

import java.util.List;

public class CommentViewModel extends AndroidViewModel {
    private CommentRepository commentRepository;

    public CommentViewModel(@NonNull Application application) {
        super(application);
        commentRepository = new CommentRepository(application);
    }

    public LiveData<List<CommentData>> getComments(String videoId) {
        return commentRepository.getComments(videoId);
    }

    public void createComment(CommentData commentData) {
        commentRepository.createComment(commentData);
    }

    public void updateComment(CommentData commentData) {
        commentRepository.updateComment(commentData);
    }

    public void deleteComment(CommentData commentData) {
        commentRepository.deleteComment(commentData);
    }

    public void likeComment(String commentId, String userDisplayName) {
        commentRepository.likeComment(commentId, userDisplayName);
    }

    public void dislikeComment(String commentId, String userDisplayName) {
        commentRepository.dislikeComment(commentId, userDisplayName);
    }
}
