package com.example.project_android.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project_android.Converters;
import com.example.project_android.DataUtils;
import com.example.project_android.R;
import com.example.project_android.entities.VideoData;

import java.io.InputStream;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<VideoData> videoList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private static final String TAG = "VideoAdapter";

    public interface OnItemClickListener {
        void onItemClick(VideoData videoData);
    }

    public VideoAdapter(List<VideoData> videoList, OnItemClickListener onItemClickListener) {
        this.videoList = videoList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_related_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoData videoData = videoList.get(position);

        holder.titleTextView.setText(videoData.getTitle());
        holder.authorViewsUploadTimeTextView.setText(
                context.getString(R.string.video_author_views_uploadtime,
                        videoData.getAuthor(), videoData.getViews(), DataUtils.getTimeAgo(videoData.getUploadTime()))
        );

        // Load video thumbnail
        loadImage(videoData.getImg(), holder.thumbnailImageView);

        // Load author image
        loadImage(videoData.getAuthorImage(), holder.authorImageView);

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(videoData));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void updateVideoList(List<VideoData> newVideoList) {
        this.videoList = newVideoList;
        notifyDataSetChanged();
    }

    public List<VideoData> getVideoList() {
        return videoList;
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        TextView titleTextView;
        TextView authorViewsUploadTimeTextView;
        ImageView authorImageView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.video_thumbnail);
            titleTextView = itemView.findViewById(R.id.video_title);
            authorViewsUploadTimeTextView = itemView.findViewById(R.id.video_author_views_uploadtime);
            authorImageView = itemView.findViewById(R.id.author_image);
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
            }
            else if (path.startsWith("http://") || path.startsWith("https://")) {
                // URL
                Glide.with(imageView.getContext())
                        .load(path)
                        .into(imageView);
                Log.d(TAG, "Loaded image from URL: " + path);
            }
            else {
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
                    Converters converter = new Converters();
                    Bitmap bitmap = converter.toBitmap(path);
                    imageView.setImageBitmap(bitmap);
                    Log.d(TAG, "Loaded image from local file path: " + path);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage());
        }
    }
}
