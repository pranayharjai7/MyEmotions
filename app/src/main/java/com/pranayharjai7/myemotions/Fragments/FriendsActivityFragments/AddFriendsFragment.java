package com.pranayharjai7.myemotions.Fragments.FriendsActivityFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranayharjai7.myemotions.Database.UserProfile;
import com.pranayharjai7.myemotions.Utils.Adapters.UserProfileViewAdapter;
import com.pranayharjai7.myemotions.Utils.Interfaces.Callback;
import com.pranayharjai7.myemotions.databinding.FragmentAddFriendsBinding;

import java.util.ArrayList;
import java.util.List;

public class AddFriendsFragment extends Fragment {

    public static final String ADD_FRIENDS_FRAGMENT = "AddFriendsFragment";
    private FragmentAddFriendsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        addAllUsersToRecyclerView();
    }

    private void addAllUsersToRecyclerView() {
        getAllUsersFromFirebase(userProfiles -> {
            if (isAdded()) {
                binding.userProfilesRecyclerView.setAdapter(
                        new UserProfileViewAdapter(userProfiles, requireContext(), ADD_FRIENDS_FRAGMENT)
                );
            }
        });
    }

    private void getAllUsersFromFirebase(Callback<List<UserProfile>> callback) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .addValueEventListener(new ValueEventListener() {
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
                        callback.onSuccess(userProfiles);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddFriendsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
