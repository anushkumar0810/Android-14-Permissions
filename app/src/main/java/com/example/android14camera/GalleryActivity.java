package com.example.android14camera;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MEDIA_SELECTOR = 200;
    private final List<MediaUri> imageUris = new ArrayList<>();
    private final List<MediaUri> videoUris = new ArrayList<>();
    ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        TextView manage = findViewById(R.id.manage);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        loadMedia();

        ImagesFragment imagesFragment = new ImagesFragment(imageUris, this::onMediaSelected);
        VideosFragment videosFragment = new VideosFragment(videoUris, this::onMediaSelected);

        adapter.addFragment(imagesFragment, "Images");
        adapter.addFragment(videosFragment, "Videos");

        manage.setOnClickListener(v -> showBottomSheetDialog());

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



    @SuppressLint("MissingInflatedId")
    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_media_selector, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView selectMoreMedia = bottomSheetView.findViewById(R.id.select_more_media);
        TextView changeSettings = bottomSheetView.findViewById(R.id.change_settings);
        TextView cancel = bottomSheetView.findViewById(R.id.cancel);

        selectMoreMedia.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            openMediaSelector();

        });

        changeSettings.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            openAppSettings();

        });

        cancel.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void openMediaSelector() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/* video/*");
        startActivityForResult(intent, REQUEST_CODE_MEDIA_SELECTOR);
    }


    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MEDIA_SELECTOR && resultCode == RESULT_OK && data != null) {
            Uri selectedMediaUri = data.getData();
            String mediaType = getContentResolver().getType(selectedMediaUri);
            if (mediaType != null) {
                if (mediaType.startsWith("image")) {
                    imageUris.add(new MediaUri(selectedMediaUri, "image"));
                } else if (mediaType.startsWith("video")) {
                    videoUris.add(new MediaUri(selectedMediaUri, "video"));
                }
                // Notify fragments to update the views
                updateFragments();
            }
        }
    }

    private void updateFragments() {
        // Method to notify the fragments to update their views
        // You can use a ViewModel or a similar approach to communicate between the activity and fragments
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
