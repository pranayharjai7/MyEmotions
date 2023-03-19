package com.pranayharjai7.myemotions;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranayharjai7.myemotions.Utils.Enums.MoodVisibility;
import com.pranayharjai7.myemotions.Utils.Interfaces.Callback;
import com.pranayharjai7.myemotions.databinding.ActivitySettingsBinding;

import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ActivitySettingsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private String selectedMoodVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{MoodVisibility.PUBLIC.toString(), MoodVisibility.FRIENDS.toString(), MoodVisibility.ONLYME.toString()});
        binding.moodVisibilitySpinner.setAdapter(adapter);

        getUserMoodVisibilityFromDatabase(moodVisibility -> {
            if (moodVisibility != null) {
                binding.moodVisibilitySpinner.setSelection(adapter.getPosition(moodVisibility));
            } else {
                binding.moodVisibilitySpinner.setEnabled(false);
            }
        });

        binding.moodVisibilitySpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedMoodVisibility = parent.getItemAtPosition(position).toString();
    }

    public void setMoodVisibilityButtonClicked(View view) {
        if (selectedMoodVisibility != null) {
            setUserMoodVisibilityInDatabase(selectedMoodVisibility);
        }
    }

    private void getUserMoodVisibilityFromDatabase(Callback<String> callback) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Object> userProfileMap = (Map<String, Object>) snapshot.getValue();
                        String moodVisibility = (String) userProfileMap.get("moodVisibility");
                        callback.onSuccess(moodVisibility);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setUserMoodVisibilityInDatabase(String selectedMoodVisibility) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(mAuth.getCurrentUser().getUid())
                .child("moodVisibility")
                .setValue(selectedMoodVisibility)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Mood Visibility Modified", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}