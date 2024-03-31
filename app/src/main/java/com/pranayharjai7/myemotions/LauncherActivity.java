package com.pranayharjai7.myemotions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pranayharjai7.myemotions.databinding.ActivityLauncherBinding;

public class LauncherActivity extends AppCompatActivity {

    private ActivityLauncherBinding binding;
    private boolean layoutClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Runnable loginActivityRunnable = () -> {
            if (!layoutClicked) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        };
        binding.launcherConstraintLayout.postDelayed(loginActivityRunnable, 1500);
    }

    public void launcherConstraintLayoutClicked(View view) {
        layoutClicked = true;
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}