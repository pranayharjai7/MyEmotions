package com.pranayharjai7.myemotions.Fragments.LoginActivityFragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.pranayharjai7.myemotions.Database.UserProfile;
import com.pranayharjai7.myemotions.MainActivity;
import com.pranayharjai7.myemotions.R;
import com.pranayharjai7.myemotions.Utils.Enums.MoodVisibility;
import com.pranayharjai7.myemotions.ViewModels.RegisterViewModel;
import com.pranayharjai7.myemotions.databinding.FragmentRegisterUserBinding;

import java.util.ArrayList;

public class RegisterUserFragment extends Fragment {

    private FragmentRegisterUserBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private RegisterViewModel registerViewModel;

    public RegisterUserFragment() {
        super(R.layout.fragment_register_user);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        observations();
    }

    private void init() {
        registerViewModel = new ViewModelProvider(requireActivity()).get(RegisterViewModel.class);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            login();
        }
    }

    private void observations() {
        registerViewModel.getLoginActivityView().observe(getViewLifecycleOwner(), view -> {
            String username = binding.usernameRegisterEditText.getText().toString();
            String email = binding.emailAddressRegisterEditText.getText().toString();
            String password = binding.passwordRegisterEditText.getText().toString();

            if (checkRegistration(username, email, password)) {
                registerUserWithFirebaseAuth(username, email, password);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Going to Main activity after logging in user.
     */
    private void login() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    /**
     * Register user using email and password in Firebase Authentication.
     *
     * @param username
     * @param email
     * @param password
     */
    private void registerUserWithFirebaseAuth(String username, String email, String password) {
        binding.registerButton.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId =  task.getResult().getUser().getUid();
                        UserProfile userProfile = new UserProfile(userId, username, email);
                        saveProfileInRealTimeDatabase(userProfile);
                        mAuth.signOut(); //By default, registered user gets signed in
                        Toast.makeText(getContext(), "User Registered successfully!", Toast.LENGTH_SHORT).show();
                        registerViewModel.setRegisterUserFragmentView(binding.registerButton);
                    } else {
                        Toast.makeText(getContext(), "Failed to Register,Try Again!", Toast.LENGTH_SHORT).show();
                    }
                    binding.registerProgressBar.setVisibility(View.GONE);
                });
    }

    /**
     * To save user in Realtime Database.
     * @param userProfile UserProfile to be saved
     */
    private void saveProfileInRealTimeDatabase(UserProfile userProfile) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(userProfile.getUserId())
                .setValue(userProfile);
    }

    /**
     * To check if registration parameters are correct or not.
     *
     * @param username
     * @param email
     * @param password
     * @return
     */
    private boolean checkRegistration(String username, String email, String password) {
        if (username.isEmpty()) {
            binding.usernameRegisterEditText.setError("UserName is Required!");
            binding.usernameRegisterEditText.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            binding.emailAddressRegisterEditText.setError("Email is Required!");
            binding.emailAddressRegisterEditText.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            binding.passwordRegisterEditText.setError("Password is Required!");
            binding.passwordRegisterEditText.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailAddressRegisterEditText.setError("Please Enter correct Email-Address");
            binding.emailAddressRegisterEditText.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            binding.passwordRegisterEditText.setError("Password should be min 6 characters");
            binding.passwordRegisterEditText.requestFocus();
            return false;
        }

        return true;
    }
}
