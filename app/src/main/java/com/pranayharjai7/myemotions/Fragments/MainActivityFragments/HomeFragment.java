package com.pranayharjai7.myemotions.Fragments.MainActivityFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranayharjai7.myemotions.Database.DAO.EmotionDatabase;
import com.pranayharjai7.myemotions.Database.Emotion;
import com.pranayharjai7.myemotions.Database.UserProfile;
import com.pranayharjai7.myemotions.R;
import com.pranayharjai7.myemotions.Utils.Adapters.EmotionViewAdapter;
import com.pranayharjai7.myemotions.ViewModels.HomeViewModel;
import com.pranayharjai7.myemotions.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private EmotionDatabase emotionDatabase;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        observations();
    }

    private void init() {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        emotionDatabase = Room.databaseBuilder(getContext(), EmotionDatabase.class, "Emotion_db")
                .fallbackToDestructiveMigration()
                .build();
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void observations() {
        homeViewModel.getEmotionPic().observe(getViewLifecycleOwner(), bitmap -> {
            binding.emotionsImageView.setImageBitmap(bitmap);
        });

        emotionDatabase.emotionDAO().getAllEmotion().observe(getViewLifecycleOwner(), emotions -> {
            binding.emotionsRecyclerView.setAdapter(new EmotionViewAdapter(emotions));
            //updateRealtimeDatabase(emotions);
        });
    }

//    private void updateRealtimeDatabase(List<Emotion> localEmotions) {
//        firebaseDatabase.getReference("MyEmotions")
//                .child("UserProfile")
//                .child(mAuth.getCurrentUser().getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        UserProfile userProfile = snapshot.getValue(UserProfile.class);
//                        if (userProfile != null) {
//                            List<Emotion> remoteEmotions = userProfile.getEmotions();
//                            if (remoteEmotions == null) {
//                                remoteEmotions = new ArrayList<>();
//                            }
//
//                            List<Emotion> newEmotions = new ArrayList<>();
//                            if (localEmotions.isEmpty()) {
//                                userProfile.setEmotions(newEmotions);
//                            } else {
//                                newEmotions = getNewEmotionsForRealtimeDatabase(localEmotions, remoteEmotions, newEmotions);
//                                if (!newEmotions.isEmpty()) {
//                                    remoteEmotions.addAll(newEmotions);
//                                    userProfile.setEmotions(remoteEmotions);
//                                }
//                            }
//
//                            setUserProfileInRealtimeDatabase(userProfile);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
//
//    private List<Emotion> getNewEmotionsForRealtimeDatabase(List<Emotion> localEmotions, List<Emotion> remoteEmotions, List<Emotion> newEmotions) {
//        for (Emotion localEmotion : localEmotions) {
//            boolean isAlreadyExists = false;
//            for (Emotion remoteEmotion : remoteEmotions) {
//                if (remoteEmotion.getDateTime().equals(localEmotion.getDateTime())) {
//                    isAlreadyExists = true;
//                    break;
//                }
//            }
//            if (!isAlreadyExists) {
//                newEmotions.add(localEmotion);
//            }
//        }
//        return newEmotions;
//    }
//
//    private void setUserProfileInRealtimeDatabase(UserProfile userProfile) {
//        firebaseDatabase.getReference("MyEmotions")
//                .child("UserProfile")
//                .child(mAuth.getCurrentUser().getUid())
//                .setValue(userProfile);
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.clear_database_home_options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.clearAllMenuItem) {
            new AlertDialog.Builder(getContext())
                    .setCancelable(true)
                    .setTitle("Warning!")
                    .setMessage("All the history will be cleared.\nDo you want to continue?")
                    .setPositiveButton("YES", (dialog, i) -> {
                        new Thread(() -> emotionDatabase.emotionDAO().clearData()).start();
                        Toast.makeText(getContext(), "The History has been cleared!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("NO", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
