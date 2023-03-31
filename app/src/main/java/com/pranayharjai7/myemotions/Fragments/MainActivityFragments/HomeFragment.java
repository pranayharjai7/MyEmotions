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
import com.google.firebase.database.FirebaseDatabase;
import com.pranayharjai7.myemotions.Database.DAO.EmotionDatabase;
import com.pranayharjai7.myemotions.R;
import com.pranayharjai7.myemotions.Utils.Adapters.EmotionViewAdapter;
import com.pranayharjai7.myemotions.ViewModels.HomeViewModel;
import com.pranayharjai7.myemotions.databinding.FragmentHomeBinding;

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

        if (isAdded()) {
            emotionDatabase.emotionDAO().getUserEmotions(mAuth.getCurrentUser().getUid()).observe(getViewLifecycleOwner(),
                    emotions -> binding.emotionsRecyclerView.setAdapter(new EmotionViewAdapter(emotions, requireContext())));
        }
    }

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
                        deleteFromDatabase();
                    })
                    .setNegativeButton("NO", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteFromDatabase() {
        firebaseDatabase.getReference("MyEmotions") //deleting from Firebase
                .child("UserProfile")
                .child(mAuth.getCurrentUser().getUid())
                .child("emotions")
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //deleting from local database
                        new Thread(() -> emotionDatabase.emotionDAO().clearUserData(mAuth.getCurrentUser().getUid())).start();
                        Toast.makeText(getContext(), "The History has been cleared!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Couldn't delete, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
