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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.google.firebase.auth.FirebaseAuth;
import com.pranayharjai7.myemotions.Database.Emotion;
import com.pranayharjai7.myemotions.Database.DAO.EmotionDatabase;
import com.pranayharjai7.myemotions.Fragments.MainActivityFragments.HomeFragment;
import com.pranayharjai7.myemotions.Fragments.MainActivityFragments.StatsFragment;
import com.pranayharjai7.myemotions.Utils.AnimationUtils;
import com.pranayharjai7.myemotions.Utils.ImageUtils;
import com.pranayharjai7.myemotions.ViewModels.HomeViewModel;
import com.pranayharjai7.myemotions.databinding.ActivityMainBinding;

import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity {

    private static final int ALL_PERMISSIONS_CODE = 101;
    private ActivityMainBinding binding;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private HomeViewModel homeViewModel;
    private EmotionDatabase emotionDatabase;
    private FirebaseAuth mAuth;
    private boolean isAllFabVisible;
    private Bitmap sampledImage = null;
    RecognizeEmotions recognizeEmotions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        permissions();
        init(savedInstanceState);
    }

    /**
     * Initializing variables
     *
     * @param savedInstanceState
     */
    private void init(Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding.mainBottomNavigationView.setBackground(null);
        isAllFabVisible = binding.cameraButton.getVisibility() == View.VISIBLE;
        mAuth = FirebaseAuth.getInstance();
        recognizeEmotions = new RecognizeEmotions(getApplicationContext());
        emotionDatabase = Room.databaseBuilder(this, EmotionDatabase.class, "Emotion_db")
                .fallbackToDestructiveMigration()
                .build();

        if (savedInstanceState == null) {
            replaceFragment("HOME");
        }
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

    private ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    sampledImage = (Bitmap) result.getData().getExtras().get("data");
                    if (sampledImage != null) {
                        Bitmap picWithEmotions = recognizeEmotions.recognizeEmotions(sampledImage);
                        String emotion = recognizeEmotions.getResultEmotion();
                        if (emotion != null) {
                            saveInLocalDatabase(emotion);
                        }
                        recognizeEmotions.setResultEmotion(null);
                        homeViewModel.setEmotionPic(picWithEmotions);
                    }
                }
            });

    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri selectedImageUri = result.getData().getData();
                    sampledImage = ImageUtils.getImage(selectedImageUri, getContentResolver());
                    if (sampledImage != null) {
                        Bitmap picWithEmotions = recognizeEmotions.recognizeEmotions(sampledImage);
                        String emotion = recognizeEmotions.getResultEmotion();
                        if (emotion != null) {
                            saveInLocalDatabase(emotion);
                        }
                        recognizeEmotions.setResultEmotion(null);
                        homeViewModel.setEmotionPic(picWithEmotions);
                    }
                }
            });

    /**
     * Save emotion data in room database.
     *
     * @param emotion
     */
    private void saveInLocalDatabase(String emotion) {
        new Thread(() -> {
            Emotion emotion1 = new Emotion();
            emotion1.setEmotion(emotion);
            emotion1.setDateTime(LocalDateTime.now().toString());
            emotionDatabase.emotionDAO().insertNewEmotion(emotion1);
        }).start();

        replaceFragment("HOME");
    }

    public void homeMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
            replaceFragment("HOME");
        }
    }

    public void statsMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
            replaceFragment("STATS");
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

    private void replaceFragment(String fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.fade_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.fade_out  // popExit
        );

        switch (fragment) {
            case "HOME": {
                transaction.replace(R.id.mainFragmentContainerView, HomeFragment.class, null);
                break;
            }
            case "STATS": {
                transaction.replace(R.id.mainFragmentContainerView, StatsFragment.class, null);
                break;
            }
            default: {
                transaction.replace(R.id.mainFragmentContainerView, HomeFragment.class, null);
            }
        }

        transaction.setReorderingAllowed(true)
                //.addToBackStack(fragment)
                .commit();
    }

    /**
     * To get camera and read/write external storage permissions.
     */
    private void permissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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