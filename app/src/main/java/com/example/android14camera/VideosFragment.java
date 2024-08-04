package com.example.android14camera;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VideosFragment extends Fragment {

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private final List<GalleryActivity.MediaUri> videoUris;
    private final OnMediaSelectedListener listener;

    public interface OnMediaSelectedListener {
        void onMediaSelected(Uri uri, String type);
    }

    public VideosFragment(List<GalleryActivity.MediaUri> videoUris, OnMediaSelectedListener listener) {
        this.videoUris = videoUris;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewVideos);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        galleryAdapter = new GalleryAdapter(videoUris, (uri, type) -> listener.onMediaSelected(uri, type));
        recyclerView.setAdapter(galleryAdapter);
        return view;
    }
}
