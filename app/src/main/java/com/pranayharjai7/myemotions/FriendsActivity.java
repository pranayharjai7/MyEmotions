package com.pranayharjai7.myemotions;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranayharjai7.myemotions.Database.DAO.UserProfileDatabase;
import com.pranayharjai7.myemotions.Database.UserProfile;
import com.pranayharjai7.myemotions.Utils.Adapters.UserProfileViewAdapter;
import com.pranayharjai7.myemotions.databinding.ActivityFriendsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    private ActivityFriendsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private UserProfileDatabase userProfileDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init(savedInstanceState);
        observations();
    }

    private void init(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userProfileDatabase = Room.databaseBuilder(this, UserProfileDatabase.class, "UserProfile_db")
                .fallbackToDestructiveMigration()
                .build();
        addAllUsersToLocalDatabaseFromFirebase();
    }

    private void observations() {
        userProfileDatabase.userProfileDAO().getAllUserProfile().observe(this, userProfiles -> {
            Collections.sort(userProfiles, (o1, o2) -> o1.getUsername().compareTo(o2.getUsername()));
            binding.userProfilesRecyclerView.setAdapter(new UserProfileViewAdapter(userProfiles, this));
        });
    }

    private void addAllUsersToLocalDatabaseFromFirebase() {
        new Thread(() -> {
            userProfileDatabase.userProfileDAO().clearAllData();

            firebaseDatabase.getReference("MyEmotions")
                    .child("UserProfile")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<UserProfile> userProfiles = new ArrayList<>();
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String userId = userSnapshot.getKey();
                                String username = userSnapshot.child("username").getValue(String.class);
                                String email = userSnapshot.child("email").getValue(String.class);

                                UserProfile userProfile = new UserProfile(userId, username, email);
                                userProfiles.add(userProfile);
                            }
                            new Thread(() -> userProfileDatabase.userProfileDAO().insertAllUserProfiles(userProfiles)).start();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }).start();
    }
}