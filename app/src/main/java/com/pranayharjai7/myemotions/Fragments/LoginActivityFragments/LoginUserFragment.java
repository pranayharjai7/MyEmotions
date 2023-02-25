package com.pranayharjai7.myemotions.Fragments.LoginActivityFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.pranayharjai7.myemotions.MainActivity;
import com.pranayharjai7.myemotions.R;
import com.pranayharjai7.myemotions.ViewModels.LoginViewModel;
import com.pranayharjai7.myemotions.databinding.FragmentLoginUserBinding;

public class LoginUserFragment extends Fragment {

    private FragmentLoginUserBinding binding;
    private FirebaseAuth mAuth;
    private LoginViewModel loginViewModel;

    public LoginUserFragment() {
        super(R.layout.fragment_login_user);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        observations();
    }

    private void init() {
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            login();
        }
    }

    private void observations() {
        loginViewModel.getView().observe(getViewLifecycleOwner(), view -> {
            String email = binding.emailAddressLoginEditText.getText().toString();
            String password = binding.passwordLoginEditText.getText().toString();

            if (checkLogin(email, password)) {
                loginUserWithFirebaseAuth(email, password);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginUserBinding.inflate(inflater, container, false);
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
     * Login using email and password in Firebase Authentication.
     *
     * @param email
     * @param password
     */
    private void loginUserWithFirebaseAuth(String email, String password) {
        binding.loginProgressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Logged in!", Toast.LENGTH_SHORT).show();
                        login();
                    } else {
                        Toast.makeText(getContext(), "Failed to Login!! Please Check Again!", Toast.LENGTH_SHORT).show();
                    }
                    binding.loginProgressBar.setVisibility(View.GONE);
                });
    }

    /**
     * To check if login parameters are correct or not.
     *
     * @param email
     * @param password
     * @return
     */
    private boolean checkLogin(String email, String password) {

        if (email.isEmpty()) {
            binding.emailAddressLoginEditText.setError("Email is Required!");
            binding.emailAddressLoginEditText.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            binding.passwordLoginEditText.setError("Password is Required!");
            binding.passwordLoginEditText.requestFocus();
            return false;
        }

        return true;
    }
}
