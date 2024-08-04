package com.example.android14camera;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private final List<MediaUri> imageUris = new ArrayList<>();
    private final List<MediaUri> videoUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        loadMedia();

        ImagesFragment imagesFragment = new ImagesFragment(imageUris, this::onMediaSelected);
        VideosFragment videosFragment = new VideosFragment(videoUris, this::onMediaSelected);

        adapter.addFragment(imagesFragment, "Images");
        adapter.addFragment(videosFragment, "Videos");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void loadMedia() {
        loadImages();
        loadVideos();
    }

    private void loadImages() {
        String[] projection = { MediaStore.Images.Media._ID };
        Uri imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        try (Cursor cursor = getContentResolver().query(imagesUri, projection, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                    long id = cursor.getLong(idColumn);
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    imageUris.add(new MediaUri(contentUri, "image"));
                }
            }
        }
    }

    private void loadVideos() {
        String[] projection = { MediaStore.Video.Media._ID };
        Uri videosUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        try (Cursor cursor = getContentResolver().query(videosUri, projection, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                    long id = cursor.getLong(idColumn);
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                    videoUris.add(new MediaUri(contentUri, "video"));
                }
            }
        }
    }

    private void onMediaSelected(Uri uri, String type) {
        Intent resultIntent = new Intent();
        resultIntent.setData(uri);
        resultIntent.putExtra("type", type);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    static class MediaUri {
        Uri uri;
        String type;

        MediaUri(Uri uri, String type) {
            this.uri = uri;
            this.type = type;
        }
    }
}
