package com.example.android14camera;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private List<GalleryActivity.MediaUri> mediaUris;
    private OnItemClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnItemClickListener {
        void onItemClick(Uri uri, String type);
    }

    public GalleryAdapter(List<GalleryActivity.MediaUri> mediaUris, OnItemClickListener listener) {
        this.mediaUris = mediaUris;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GalleryActivity.MediaUri mediaUri = mediaUris.get(position);

        // Reset visibility
        holder.imageView.setVisibility(View.GONE);
        holder.videoView.setVisibility(View.GONE);

        if ("image".equals(mediaUri.type)) {
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext()).load(mediaUri.uri).into(holder.imageView);
        } else if ("video".equals(mediaUri.type)) {
            holder.videoView.setVisibility(View.VISIBLE);
            holder.videoView.setVideoURI(mediaUri.uri);
            holder.videoView.seekTo(1);  // Load the first frame as a thumbnail
        }

        // Highlight selected item
        holder.itemView.setBackgroundColor(position == selectedPosition ?
                ContextCompat.getColor(holder.itemView.getContext(), R.color.selected_item_color):
                ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent));

        holder.itemView.setOnClickListener(v -> {
            // Update selected position and notify adapter to refresh the list
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            // Call the listener
            listener.onItemClick(mediaUri.uri, mediaUri.type);
        });
    }

    @Override
    public int getItemCount() {
        return mediaUris.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        VideoView videoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            videoView = itemView.findViewById(R.id.videoView);
        }
    }
}
