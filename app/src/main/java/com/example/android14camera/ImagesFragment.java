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

public class ImagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private final List<GalleryActivity.MediaUri> imageUris;
    private final OnMediaSelectedListener listener;

    public interface OnMediaSelectedListener {
        void onMediaSelected(Uri uri, String type);
    }

    public ImagesFragment(List<GalleryActivity.MediaUri> imageUris, OnMediaSelectedListener listener) {
        this.imageUris = imageUris;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_images, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewImages);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        galleryAdapter = new GalleryAdapter(imageUris, (uri, type) -> listener.onMediaSelected(uri, type));
        recyclerView.setAdapter(galleryAdapter);
        return view;
    }
}
