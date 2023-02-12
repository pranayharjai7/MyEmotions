package com.pranayharjai7.myemotions;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.pranayharjai7.myemotions.LoginAndRegister.LoginActivity;
import com.pranayharjai7.myemotions.Utils.AnimationUtils;
import com.pranayharjai7.myemotions.Utils.ImageUtils;
import com.pranayharjai7.myemotions.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int ALL_PERMISSIONS_CODE = 101;
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private boolean isAllFabVisible;
    private Bitmap sampledImage = null;
    RecognizeEmotions recognizeEmotions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mainBottomNavigationView.setBackground(null);
        isAllFabVisible = binding.cameraButton.getVisibility() == View.VISIBLE;
        permissions();
        mAuth = FirebaseAuth.getInstance();

        recognizeEmotions = new RecognizeEmotions(getApplicationContext());
    }


    public void mainConstraintLayoutClicked(View view) {
        if (isAllFabVisible) {
            AnimationUtils.animateCloseRecordEmotionButton(binding);
            isAllFabVisible = !isAllFabVisible;
        }
    }

    public void recordEmotionButtonClicked(View view) {
        if (isAllFabVisible) {
            AnimationUtils.animateCloseRecordEmotionButton(binding);
        } else {
            AnimationUtils.animateOpenRecordEmotionButton(binding);
        }
        isAllFabVisible = !isAllFabVisible;
    }

    public void cameraButtonClicked(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
        recordEmotionButtonClicked(view);
    }

    public void galleryButtonClicked(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        galleryLauncher.launch(intent);
        recordEmotionButtonClicked(view);
    }

    private ActivityResultLauncher cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    sampledImage = (Bitmap) result.getData().getExtras().get("data");
                    if (sampledImage != null) {
                        Bitmap picWithEmotions = recognizeEmotions.recognizeEmotions(sampledImage);
                        binding.thumbnailImageView.setImageBitmap(picWithEmotions);
                    }
                }
            });

    private ActivityResultLauncher galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri selectedImageUri = result.getData().getData();
                    sampledImage = ImageUtils.getImage(selectedImageUri, getContentResolver());
                    if (sampledImage != null) {
                        Bitmap picWithEmotions = recognizeEmotions.recognizeEmotions(sampledImage);
                        binding.thumbnailImageView.setImageBitmap(picWithEmotions);
                    }
                }
            });

    public void homeMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
        }
    }

    public void statsMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
        }
    }

    public void mapsMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
        }
    }

    public void moreMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
            //TODO
            mAuth.signOut();
            Toast.makeText(this, "Logged Out!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


    /**
     * To get camera and read/write external storage permissions.
     */
    private void permissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET
            }, ALL_PERMISSIONS_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ALL_PERMISSIONS_CODE) {
            if (grantResults.length != 0) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED
                        || grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Oops!! You didn't allow permissions!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}