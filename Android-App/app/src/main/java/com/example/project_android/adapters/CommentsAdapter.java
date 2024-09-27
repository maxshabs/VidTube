package com.example.project_android.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project_android.DataUtils;
import com.example.project_android.R;
import com.example.project_android.UserState;
import com.example.project_android.entities.CommentData;
import com.example.project_android.entities.User;
import com.example.project_android.viewmodels.CommentViewModel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    private List<CommentData> commentsList;
    private Context context;
    private static final String TAG = "CommentsAdapter";
    private CommentViewModel commentViewModel;

    public CommentsAdapter(Context context, List<CommentData> commentsList) {
        this.context = context;
        this.commentsList = new ArrayList<>(commentsList);
        commentViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(CommentViewModel.class);

    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentData comment = commentsList.get(position);
        holder.displayNameTextView.setText(comment.getDisplayName());
        holder.dateTextView.setText(DataUtils.getTimeAgo(comment.getDate()));
        holder.commentTextView.setText(comment.getText());

        // Load user image
        loadImage(comment.getImg(), holder.userImageView);

        // Show/hide edit and delete buttons based on login status and comment ownership
        if (UserState.isLoggedIn() && UserState.getLoggedInUser().getDisplayName().equals(comment.getDisplayName())) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }

        holder.editButton.setOnClickListener(v -> showEditCommentDialog(comment));
        holder.deleteButton.setOnClickListener(v -> deleteComment(comment));

        holder.likeButton.setOnClickListener(v -> handleLikeDislike(comment, true, holder));
        holder.dislikeButton.setOnClickListener(v -> handleLikeDislike(comment, false, holder));

        // Update button colors based on comment state
        updateLikeDislikeButtonColors(comment, holder);
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public void updateComments(List<CommentData> newComments) {
        commentsList.clear();
        commentsList.addAll(new ArrayList<>(newComments));
        notifyDataSetChanged();
    }

    private void showEditCommentDialog(CommentData commentData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Comment");

        final EditText input = new EditText(context);
        input.setText(commentData.getText());
        input.setTextColor(context.getResources().getColor(R.color.dialog_text));
        input.setBackgroundColor(context.getResources().getColor(R.color.dialog_background));
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newCommentText = input.getText().toString().trim();
            if (!newCommentText.isEmpty()) {
                commentData.setText(newCommentText);
                Log.d(TAG, "Editing comment with ID: " + commentData.getId());
                commentViewModel.updateComment(commentData); // Update comment in ViewModel
                notifyItemChanged(commentsList.indexOf(commentData)); // Update the specific item
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (positiveButton != null && negativeButton != null) {
            positiveButton.setTextColor(context.getResources().getColor(R.color.dialog_text));
            negativeButton.setTextColor(context.getResources().getColor(R.color.dialog_text));
        }
    }


    private void deleteComment(CommentData commentData) {
        commentViewModel.deleteComment(commentData); // Delete comment in ViewModel
        int position = commentsList.indexOf(commentData);
        commentsList.remove(commentData);
        notifyItemRemoved(position); // Remove the specific item
    }


    private void handleLikeDislike(CommentData commentData, boolean isLike, CommentViewHolder holder) {
        User loggedInUser = UserState.getLoggedInUser();
        if (loggedInUser == null) {
            Toast.makeText(context, "You need to be logged in to perform this action.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userDisplayName = loggedInUser.getDisplayName();

        if (isLike) {
            if (commentData.isLiked()) {
                commentData.setLiked(false);
                commentViewModel.likeComment(commentData.getId(), userDisplayName); // Remove like on server
            } else {
                commentData.setLiked(true);
                commentData.setDisliked(false);
                commentViewModel.likeComment(commentData.getId(), userDisplayName); // Like on server
            }
        } else {
            if (commentData.isDisliked()) {
                commentData.setDisliked(false);
                commentViewModel.dislikeComment(commentData.getId(), userDisplayName); // Remove dislike on server
            } else {
                commentData.setDisliked(true);
                commentData.setLiked(false);
                commentViewModel.dislikeComment(commentData.getId(), userDisplayName); // Dislike on server
            }
        }

        commentViewModel.updateComment(commentData); // Use ViewModel to update comment
        updateLikeDislikeButtonColors(commentData, holder);
    }


    private void updateLikeDislikeButtonColors(CommentData commentData, CommentViewHolder holder) {
        if (commentData.isLiked()) {
            holder.likeButton.setColorFilter(context.getResources().getColor(R.color.like_green));
        } else {
            holder.likeButton.setColorFilter(null);
        }

        if (commentData.isDisliked()) {
            holder.dislikeButton.setColorFilter(context.getResources().getColor(R.color.dislike_red));
        } else {
            holder.dislikeButton.setColorFilter(null);
        }
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView displayNameTextView;
        TextView dateTextView;
        TextView commentTextView;
        ImageView userImageView;
        Button editButton;
        Button deleteButton;
        ImageButton likeButton;
        ImageButton dislikeButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            displayNameTextView = itemView.findViewById(R.id.comment_username);
            dateTextView = itemView.findViewById(R.id.comment_date);
            commentTextView = itemView.findViewById(R.id.comment_text);
            userImageView = itemView.findViewById(R.id.comment_user_image);
            editButton = itemView.findViewById(R.id.edit_comment_button);
            deleteButton = itemView.findViewById(R.id.delete_comment_button);
            likeButton = itemView.findViewById(R.id.like_comment_button);
            dislikeButton = itemView.findViewById(R.id.dislike_comment_button);
        }
    }

    private void loadImage(String path, ImageView imageView) {
        try {
            if (path.startsWith("data:image")) {
                // Base64 encoded image
                String base64Image = path.split(",")[1];
                byte[] decodedString = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);
                Log.d(TAG, "Loaded base64 image.");
            } else if (path.startsWith("http://") || path.startsWith("https://")) {
                // URL
                Glide.with(imageView.getContext())
                        .load(path)
                        .into(imageView);
                Log.d(TAG, "Loaded image from URL: " + path);
            } else {
                // Check if the path is a drawable resource
                int resId = imageView.getContext().getResources().getIdentifier(path, "drawable", imageView.getContext().getPackageName());
                if (resId != 0) {
                    imageView.setImageResource(resId);
                    Log.d(TAG, "Loaded drawable resource: " + path);
                } else if (path.startsWith("content://") || path.startsWith("file://")) {
                    // Load from URI
                    Uri uri = Uri.parse(path);
                    InputStream inputStream = imageView.getContext().getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    Log.d(TAG, "Loaded image from URI: " + path);
                } else {
                    // Load from local file path
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    imageView.setImageBitmap(bitmap);
                    Log.d(TAG, "Loaded image from local file path: " + path);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage());
        }
    }

}
