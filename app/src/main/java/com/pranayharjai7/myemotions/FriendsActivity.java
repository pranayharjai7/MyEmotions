package com.pranayharjai7.myemotions;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.pranayharjai7.myemotions.Utils.FragmentUtils;
import com.pranayharjai7.myemotions.databinding.ActivityFriendsBinding;

public class FriendsActivity extends AppCompatActivity {

    public static final String MY_FRIENDS = "MY FRIENDS";
    public static final String ADD_FRIENDS = "ADD FRIENDS";
    public static final String FRIEND_REQUESTS = "FRIEND REQUESTS";
    public static final String REMOVE_FRIENDS = "REMOVE FRIENDS";
    private ActivityFriendsBinding binding;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        binding.friendsBottomNavigationView.setBackground(null);
        if (savedInstanceState == null) {
            FragmentUtils.replaceFriendsFragment(fragmentManager, MY_FRIENDS);
        }
    }

    public void myFriendsMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
            FragmentUtils.replaceFriendsFragment(fragmentManager, MY_FRIENDS);
        }
    }

    public void addFriendsMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
            FragmentUtils.replaceFriendsFragment(fragmentManager, ADD_FRIENDS);
        }
    }

    public void friendRequestsMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
            FragmentUtils.replaceFriendsFragment(fragmentManager, FRIEND_REQUESTS);
        }
    }

    public void removeFriendsMenuItemClicked(MenuItem item) {
        if (!item.isChecked()) {
            item.setChecked(true);
            //TODO Change fragments
        }
    }
}