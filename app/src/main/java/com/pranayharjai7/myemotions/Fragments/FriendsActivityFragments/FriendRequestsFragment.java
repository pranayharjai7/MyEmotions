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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranayharjai7.myemotions.Database.UserProfile;
import com.pranayharjai7.myemotions.Utils.Adapters.UserProfileViewAdapter;
import com.pranayharjai7.myemotions.Utils.Interfaces.Callback;
import com.pranayharjai7.myemotions.databinding.FragmentFriendRequestsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendRequestsFragment extends Fragment {

    public static final String FRIEND_REQUESTS_FRAGMENT = "FriendRequestsFragment";
    private FragmentFriendRequestsBinding binding;
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
        addFriendRequestsToRecyclerView();
    }

    private void addFriendRequestsToRecyclerView() {
        getFriendRequestsUIds(friendRequestsUIds -> {
            getFriendRequestsUserProfiles(friendRequestsUIds, friendRequestsUserProfiles -> {
                if (isAdded()) {
                    binding.friendRequestsRecyclerView.setAdapter(
                            new UserProfileViewAdapter(friendRequestsUserProfiles, requireContext(), FRIEND_REQUESTS_FRAGMENT)
                    );
                }
            });
        });
    }

    private void getFriendRequestsUIds(Callback<List<String>> callback) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(mAuth.getCurrentUser().getUid())
                .child("friendRequests")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> friendRequestsUIds = new ArrayList<>();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String friendRequestsUId = userSnapshot.getKey();
                            friendRequestsUIds.add(friendRequestsUId);
                        }
                        callback.onSuccess(friendRequestsUIds);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getFriendRequestsUserProfiles(List<String> friendRequestsUIds, Callback<List<UserProfile>> callback) {
        DatabaseReference userProfileRef = firebaseDatabase.getReference("MyEmotions").child("UserProfile");
        List<UserProfile> userProfiles = new ArrayList<>();
        AtomicInteger requestsToProcess = new AtomicInteger(friendRequestsUIds.size());
        for (String friendRequestUId : friendRequestsUIds) {
            userProfileRef.child(friendRequestUId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    UserProfile userProfile = new UserProfile(friendRequestUId, username, email);
                    userProfiles.add(userProfile);
                    if (requestsToProcess.decrementAndGet() == 0) {
                        callback.onSuccess(userProfiles);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (requestsToProcess.decrementAndGet() == 0) {
                        callback.onSuccess(userProfiles);
                    }
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFriendRequestsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
