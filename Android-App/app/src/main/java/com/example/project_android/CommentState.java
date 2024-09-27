package com.example.project_android;

import android.content.Context;
import com.example.project_android.entities.CommentData;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CommentState {

    private static CommentState instance;
    private Map<Integer, List<CommentData>> commentDataMap;
    private Gson gson;

    private CommentState() {
        commentDataMap = new HashMap<>();
        gson = new Gson();
    }

    public static CommentState getInstance(Context context) {
        if (instance == null) {
            instance = new CommentState();
        }
        return instance;
    }

    public List<CommentData> getCommentsForVideo(int videoId) {
        return commentDataMap.get(videoId);
    }

    public void updateCommentsForVideo(int videoId, List<CommentData> comments) {
        commentDataMap.put(videoId, comments);
    }

    public CommentData getCommentData(String id) {
        for (List<CommentData> comments : commentDataMap.values()) {
            for (CommentData comment : comments) {
                if (comment.getId() == id) {
                    return comment;
                }
            }
        }
        return null;
    }

    public void addCommentsForVideo(int videoId, List<CommentData> comments) {
        commentDataMap.put(videoId, comments);
    }

    public void updateCommentData(CommentData updatedCommentData) {
        int videoId = getVideoIdForComment(updatedCommentData.getId());
        if (videoId != -1) {
            List<CommentData> comments = commentDataMap.get(videoId);
            for (int i = 0; i < comments.size(); i++) {
                if (comments.get(i).getId() == updatedCommentData.getId()) {
                    comments.set(i, updatedCommentData);
                    break;
                }
            }
            updateCommentsForVideo(videoId, comments); // Ensure state is updated
        }
    }

    public void deleteCommentData(String commentId) {
        int videoId = getVideoIdForComment(commentId);
        if (videoId != -1) {
            List<CommentData> comments = commentDataMap.get(videoId);
            for (Iterator<CommentData> iterator = comments.iterator(); iterator.hasNext(); ) {
                CommentData comment = iterator.next();
                if (comment.getId() == commentId) {
                    iterator.remove();
                    break;
                }
            }
            updateCommentsForVideo(videoId, comments); // Ensure state is updated
        }
    }

    private int getVideoIdForComment(String commentId) {
        for (Map.Entry<Integer, List<CommentData>> entry : commentDataMap.entrySet()) {
            for (CommentData comment : entry.getValue()) {
                if (comment.getId() == commentId) {
                    return entry.getKey();
                }
            }
        }
        return -1;
    }

    public Map<Integer, List<CommentData>> getAllComments() {
        return commentDataMap;
    }
}
