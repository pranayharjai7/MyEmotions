package com.pranayharjai7.myemotions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranayharjai7.myemotions.Database.UserProfile;
import com.pranayharjai7.myemotions.Utils.Interfaces.Callback;
import com.pranayharjai7.myemotions.databinding.ActivityMyProfileBinding;

import java.util.Map;

public class MyProfileActivity extends AppCompatActivity {

    private ActivityMyProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        getUserProfileData(userProfile -> {
            initialiseViews(userProfile);
        });
    }

    private void getUserProfileData(Callback<UserProfile> callback) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Object> userProfileMap = (Map<String, Object>) snapshot.getValue();
                        String username = (String) userProfileMap.get("username");
                        String email = (String) userProfileMap.get("email");
                        String moodVisibility = (String) userProfileMap.get("moodVisibility");

                        UserProfile userProfile = new UserProfile(mAuth.getCurrentUser().getUid(), username, email, "", moodVisibility, "", "");
                        callback.onSuccess(userProfile);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void initialiseViews(UserProfile userProfile) {
        binding.userNameMyProfileTextView.setText(userProfile.getUsername());
        binding.emailMyProfileTextView.setText(userProfile.getEmail());
        binding.moodVisibilityMyProfileTextView.setText(userProfile.getMoodVisibility());
    }
}