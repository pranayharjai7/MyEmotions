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
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranayharjai7.myemotions.Database.DAO.EmotionDatabase;
import com.pranayharjai7.myemotions.Database.Emotion;
import com.pranayharjai7.myemotions.Utils.AnimationUtils;
import com.pranayharjai7.myemotions.Utils.DateTimeUtils;
import com.pranayharjai7.myemotions.Utils.FragmentUtils;
import com.pranayharjai7.myemotions.Utils.ImageUtils;
import com.pranayharjai7.myemotions.Utils.Interfaces.OnRealtimeEmotionsLoadedCallback;
import com.pranayharjai7.myemotions.ViewModels.HomeViewModel;
import com.pranayharjai7.myemotions.databinding.ActivityMainBinding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int ALL_PERMISSIONS_CODE = 101;
    public static final String HOME = "HOME";
    public static final String STATS = "STATS";
    public static final String MAPS = "MAPS";
    private ActivityMainBinding binding;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private HomeViewModel homeViewModel;
    private EmotionDatabase emotionDatabase;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
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
        binding.mainSideNavigationView.setNavigationItemSelectedListener(this);
        isAllFabVisible = binding.cameraButton.getVisibility() == View.VISIBLE;
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        recognizeEmotions = new RecognizeEmotions(getApplicationContext());
        emotionDatabase = Room.databaseBuilder(this, EmotionDatabase.class, "Emotion_db")
                .fallbackToDestructiveMigration()
                .build();

        if (savedInstanceState == null) {
            FragmentUtils.replaceMainFragment(fragmentManager, HOME);
            binding.mainSideNavigationView.setCheckedItem(R.id.nav_home);
        }

        syncRealtimeEmotionDatabase();
        syncLocalEmotionDatabase();
    }

    private void syncRealtimeEmotionDatabase() {
        emotionDatabase.emotionDAO().getUserEmotions(mAuth.getCurrentUser().getUid()).observe(
                this, localEmotions -> {
                    //This function call uses a callback method to retrieve data from firebase
                    getRealtimeEmotionsForSyncing(realtimeEmotions -> {
                                if (localEmotions != null && realtimeEmotions != null) {
                                    List<Emotion> missingEmotions = findMissingEmotions(localEmotions, realtimeEmotions);

                                    if (!missingEmotions.isEmpty()) {
                                        DatabaseReference emotionsRef = firebaseDatabase.getReference("MyEmotions")
                                                .child("UserProfile")
                                                .child(mAuth.getCurrentUser().getUid())
                                                .child("emotions");

                                        for (Emotion missingEmotion : missingEmotions) {
                                            Map<String, Object> emotionMap = new HashMap<>();
                                            emotionMap.put("emotion", missingEmotion.getEmotion());
                                            emotionsRef.child(missingEmotion.getDateTime())
                                                    .setValue(emotionMap);
                                        }
                                    }
                                }
                            }
                    );
                }
        );

    }

    private void syncLocalEmotionDatabase() {
        emotionDatabase.emotionDAO().getUserEmotions(mAuth.getCurrentUser().getUid()).observe(
                this, localEmotions -> {
                    //This function call uses a callback method to retrieve data from firebase
                    getRealtimeEmotionsForSyncing(realtimeEmotions -> {
                        if (localEmotions != null && realtimeEmotions != null) {
                            List<Emotion> missingEmotions = findMissingEmotions(realtimeEmotions, localEmotions);

                            if (!missingEmotions.isEmpty()) {
                                new Thread(() -> emotionDatabase.emotionDAO().insertAllEmotions(missingEmotions)).start();
                            }
                        } else if (localEmotions == null && realtimeEmotions != null) {
                            new Thread(() -> emotionDatabase.emotionDAO().insertAllEmotions(realtimeEmotions)).start();
                        }
                    });
                });

    }


    private void getRealtimeEmotionsForSyncing(OnRealtimeEmotionsLoadedCallback callback) {
        List<Emotion> realtimeEmotions = new ArrayList<>();
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(mAuth.getCurrentUser().getUid())
                .child("emotions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Object> emotionsMap = (Map<String, Object>) snapshot.getValue();
                        if (emotionsMap != null) {
                            for (Map.Entry<String, Object> entry : emotionsMap.entrySet()) {
                                String dateTime = entry.getKey();
                                String recordedEmotion = ((Map<String, Object>) entry.getValue()).get("emotion").toString();
                                Emotion emotion = new Emotion();
                                emotion.setUserId(mAuth.getCurrentUser().getUid());
                                emotion.setDateTime(dateTime);
                                emotion.setEmotion(recordedEmotion);
                                realtimeEmotions.add(emotion);
                            }
                        }
                        callback.onRealtimeEmotionsLoaded(realtimeEmotions);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private List<Emotion> findMissingEmotions(List<Emotion> emotionList1, List<Emotion> emotionList2) {
        List<Emotion> missingEmotions = new ArrayList<>();
        for (Emotion emotion1 : emotionList1) {
            boolean found = false;
            for (Emotion emotion2 : emotionList2) {
                if (emotion2.getDateTime().equals(emotion1.getDateTime())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                missingEmotions.add(emotion1);
            }
        }
        return missingEmotions;
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
            emotion1.setUserId(mAuth.getCurrentUser().getUid());
            emotion1.setEmotion(emotion);
            emotion1.setDateTime(DateTimeUtils.convertDateAndTimeToString(LocalDateTime.now()));
            emotionDatabase.emotionDAO().insertNewEmotion(emotion1);

            Map<String, Object> emotionMap = new HashMap<>();
            emotionMap.put("emotion", emotion1.getEmotion());

            firebaseDatabase.getReference("MyEmotions")
                    .child("UserProfile")
                    .child(mAuth.getCurrentUser().getUid())
                    .child("emotions")
                    .child(emotion1.getDateTime())
                    .setValue(emotionMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            addLatestEmotionToUserProfile(emotion1);
                        } else {
                            Toast.makeText(this, "Couldn't upload emotion", Toast.LENGTH_SHORT).show();
                        }
                    });
        }).start();

        FragmentUtils.replaceMainFragment(fragmentManager, HOME);
    }

    private void addLatestEmotionToUserProfile(Emotion emotion) {
        DatabaseReference ref = firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(mAuth.getCurrentUser().getUid());
        ref.child("latestEmotion").setValue(emotion.getEmotion());
        ref.child("latestEmotionDateTime").setValue(emotion.getDateTime());

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

    public void homeMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
            FragmentUtils.replaceMainFragment(fragmentManager, HOME);
        }
    }

    public void statsMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
            FragmentUtils.replaceMainFragment(fragmentManager, STATS);
        }
    }

    public void mapsMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
            FragmentUtils.replaceMainFragment(fragmentManager, MAPS);
        }
    }

    public void moreMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            binding.mainDrawerLayout.openDrawer(GravityCompat.END);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    public void homeNavigationMenuButtonClicked(@NonNull MenuItem item) {
        homeMenuItemClicked(binding.mainBottomNavigationView.getMenu().findItem(R.id.homeItem));
        binding.mainDrawerLayout.closeDrawer(GravityCompat.END);
    }

    public void friendsNavigationMenuButtonClicked(@NonNull MenuItem item) {
        binding.mainDrawerLayout.closeDrawer(GravityCompat.END);
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
        //TODO create an activity to Show friends, to accept or reject received friend requests, Delete Friends
    }

    public void settingsNavigationMenuButtonClicked(@NonNull MenuItem item) {
        binding.mainDrawerLayout.closeDrawer(GravityCompat.END);
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void logoutNavigationMenuButtonClicked(@NonNull MenuItem item) {
        mAuth.signOut();
        Toast.makeText(this, "Logged Out!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * To get camera and read/write external storage permissions.
     */
    private void permissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, ALL_PERMISSIONS_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ALL_PERMISSIONS_CODE) {
            if (grantResults.length != 0) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED
                        || grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED
                        || grantResults[4] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Oops!! You didn't allow permissions!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.mainDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.mainDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mainSideNavigationView.setCheckedItem(R.id.nav_home);
    }
}