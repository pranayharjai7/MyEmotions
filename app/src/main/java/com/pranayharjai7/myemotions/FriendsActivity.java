package com.pranayharjai7.myemotions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.pranayharjai7.myemotions.databinding.ActivityFriendsBinding;

public class FriendsActivity extends AppCompatActivity {

    private ActivityFriendsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        binding.friendsBottomNavigationView.setBackground(null);
    }

    public void friendsMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
        }
    }

    public void addFriendsMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
            Intent intent = new Intent(this, AddFriendsActivity.class);
            startActivity(intent);
        }
    }

    public void friendRequestsMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
            Intent intent = new Intent(this, FriendRequestsActivity.class);
            startActivity(intent);
        }
    }

    public void removeFriendsMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
        }
    }
}