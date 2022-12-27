package com.pranayharjai7.myemotions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pranayharjai7.myemotions.databinding.ActivityLauncherBinding;

public class LauncherActivity extends AppCompatActivity {

    private ActivityLauncherBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = new Intent(this, MainActivity.class);

        Runnable runnable = () -> {
            startActivity(intent);
            finish();
        };

        binding.LauncherImage.postDelayed(runnable, 1000);
    }

    public void launcherConstraintLayoutClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}