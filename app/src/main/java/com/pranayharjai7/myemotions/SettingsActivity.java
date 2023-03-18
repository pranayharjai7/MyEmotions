package com.pranayharjai7.myemotions;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.pranayharjai7.myemotions.Utils.Enums.MoodVisibility;
import com.pranayharjai7.myemotions.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init(savedInstanceState);
        observations();
    }

    private void init(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[] { MoodVisibility.PUBLIC.toString(), MoodVisibility.FRIENDS.toString(), MoodVisibility.ONLYME.toString() });
        binding.moodVisibilitySpinner.setAdapter(adapter);
    }

    private void observations() {
        binding.moodVisibilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMoodVisibility = parent.getItemAtPosition(position).toString();
                Toast.makeText(SettingsActivity.this, selectedMoodVisibility, Toast.LENGTH_SHORT).show();
                //TODO
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}