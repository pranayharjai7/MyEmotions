package com.pranayharjai7.myemotions;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.google.firebase.auth.FirebaseAuth;
import com.pranayharjai7.myemotions.Database.LocalDatabase.Expression;
import com.pranayharjai7.myemotions.Database.LocalDatabase.ExpressionDatabase;
import com.pranayharjai7.myemotions.Fragments.MainActivityFragments.HomeFragment;
import com.pranayharjai7.myemotions.Fragments.MainActivityFragments.StatsFragment;
import com.pranayharjai7.myemotions.LoginAndRegister.LoginActivity;
import com.pranayharjai7.myemotions.Utils.AnimationUtils;
import com.pranayharjai7.myemotions.Utils.ImageUtils;
import com.pranayharjai7.myemotions.ViewModels.HomeViewModel;
import com.pranayharjai7.myemotions.databinding.ActivityMainBinding;

import java.time.LocalDateTime;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    private static final int ALL_PERMISSIONS_CODE = 101;
    private ActivityMainBinding binding;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private HomeViewModel homeViewModel;
    private ExpressionDatabase expressionDatabase;
    private FirebaseAuth mAuth;
    private boolean isAllFabVisible;
    private Bitmap sampledImage = null;
    RecognizeEmotions recognizeEmotions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init(savedInstanceState);
        permissions();
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
        expressionDatabase = Room.databaseBuilder(this, ExpressionDatabase.class, "Expression_db")
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

    private ActivityResultLauncher cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    sampledImage = (Bitmap) result.getData().getExtras().get("data");
                    if (sampledImage != null) {
                        Bitmap picWithEmotions = recognizeEmotions.recognizeEmotions(sampledImage);
                        String expression = recognizeEmotions.getResultEmotion();
                        recognizeEmotions.setResultEmotion(null);
                        saveInLocalDatabase(expression);
                        homeViewModel.setEmotionPic(picWithEmotions);
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
                        String expression = recognizeEmotions.getResultEmotion();
                        recognizeEmotions.setResultEmotion(null);
                        saveInLocalDatabase(expression);
                        homeViewModel.setEmotionPic(picWithEmotions);
                    }
                }
            });

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
                transaction.replace(R.id.fragmentContainerView, HomeFragment.class, null);
                break;
            }
            case "STATS": {
                transaction.replace(R.id.fragmentContainerView, StatsFragment.class, null);
                break;
            }
            default: {
                transaction.replace(R.id.fragmentContainerView, HomeFragment.class, null);
            }
        }

        transaction.setReorderingAllowed(true)
                //.addToBackStack(fragment)
                .commit();
    }

    /**
     * Save emotion data in room database.
     *
     * @param expression
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveInLocalDatabase(String expression) {
        new Thread(() -> {
            Expression expression1 = new Expression();
            expression1.setExpression(expression);
            expression1.setDateTime(LocalDateTime.now().toString());
            expressionDatabase.expressionDAO().insertNewExpression(expression1);
        }).start();

        replaceFragment("HOME");
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

    public void clearAllButtonClicked(View view) {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("Warning!")
                .setMessage("All the history will be cleared.\nDo you want to continue?")
                .setPositiveButton("YES", (dialog, i) -> {
                    new Thread(() -> expressionDatabase.expressionDAO().clearData()).start();
                    replaceFragment("HOME");
                    Toast.makeText(MainActivity.this,"The History has been cleared!",Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("NO",(dialogInterface, i) -> {
                    dialogInterface.dismiss();
                }).show();
    }
}