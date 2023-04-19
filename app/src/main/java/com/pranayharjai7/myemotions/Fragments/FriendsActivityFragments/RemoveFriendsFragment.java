package com.pranayharjai7.myemotions.Fragments.FriendsActivityFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

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
import com.pranayharjai7.myemotions.databinding.FragmentRemoveFriendsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoveFriendsFragment extends Fragment {

    public static final String REMOVE_FRIENDS_FRAGMENT = "RemoveFriendsFragment";
    private FragmentRemoveFriendsBinding binding;
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
        addMyFriendsToRecyclerView();
    }

    private void addMyFriendsToRecyclerView() {
        getMyFriendUIds(friendUIds -> {
            getFriendUserProfiles(friendUIds, friendUserProfiles -> {
                if (isAdded()) {
                    UserProfileViewAdapter adapter = new UserProfileViewAdapter(friendUserProfiles, requireContext(), REMOVE_FRIENDS_FRAGMENT);
                    binding.removeFriendsRecyclerView.setAdapter(adapter);
                    binding.removeFriendsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            adapter.setFilter(newText);
                            return true;
                        }
                    });
                }
            });
        });
    }

    private void getMyFriendUIds(Callback<List<String>> callback) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(mAuth.getCurrentUser().getUid())
                .child("friends")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> friendUIds = new ArrayList<>();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String friendUId = userSnapshot.getKey();
                            friendUIds.add(friendUId);
                        }
                        callback.onSuccess(friendUIds);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getFriendUserProfiles(List<String> friendUIds, Callback<List<UserProfile>> callback) {
        DatabaseReference userProfileRef = firebaseDatabase.getReference("MyEmotions").child("UserProfile");
        List<UserProfile> friendProfiles = new ArrayList<>();
        AtomicInteger requestsToProcess = new AtomicInteger(friendUIds.size());
        for (String friendUId : friendUIds) {
            userProfileRef.child(friendUId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    UserProfile friendProfile = new UserProfile(friendUId, username, email);
                    friendProfiles.add(friendProfile);
                    if (requestsToProcess.decrementAndGet() == 0) {
                        callback.onSuccess(friendProfiles);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (requestsToProcess.decrementAndGet() == 0) {
                        callback.onSuccess(friendProfiles);
                    }
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRemoveFriendsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
