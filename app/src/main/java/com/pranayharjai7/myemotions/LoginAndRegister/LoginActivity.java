package com.pranayharjai7.myemotions.LoginAndRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.pranayharjai7.myemotions.MainActivity;
import com.pranayharjai7.myemotions.databinding.ActivityLoginBinding;

import java.util.concurrent.atomic.AtomicBoolean;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            login();
        }
    }

    private void login() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void loginButtonClicked(View view) {

        String email = binding.emailAddressLoginEditText.getText().toString();
        String password = binding.passwordLoginEditText.getText().toString();

        if (!checkLogin(email, password)) {
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show();
                        login();
                        binding.progressBar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, "Failed to Login!! Please Check Again!", Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void googleLoginButtonClicked(View view) {
    }

    public void facebookLoginButtonClicked(View view) {
    }

    public void twitterLoginButtonClicked(View view) {
    }

    public void signupButtonClicked(View view) {
        String username = binding.usernameRegisterEditText.getText().toString();
        String email = binding.emailAddressRegisterEditText.getText().toString();
        String password = binding.passwordRegisterEditText.getText().toString();

        if (checkRegistration(username, email, password)) {
            registerUserWithFirebase(username, email, password);
        }
    }

    private void registerUserWithFirebase(String username, String email, String password) {
        binding.progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        task.getResult().getUser()
                                .updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(username).build());
                        Toast.makeText(this, "User Registered successfully!", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        loginMenuButtonClicked(binding.loginMenuButton);
                        binding.progressBar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, "Failed to Register,Try Again!", Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

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


    public void loginMenuButtonClicked(View view) {
        changeMenu(view, binding.signupMenuButton);

    }

    public void signupMenuButtonClicked(View view) {
        changeMenu(view, binding.loginMenuButton);
    }

    private void changeMenu(View view1, View view2) {
        ((Button) view1).setTextColor(Color.parseColor("#8838F8"));
        ((Button) view2).setTextColor(Color.parseColor("#FFAAAAAA"));
        binding.loginMenuDivider.setX(((Button) view1).getX() + 30);
        if (view1.equals(binding.loginMenuButton)) {
            binding.loginLayout.setEnabled(true);
            binding.loginLayout.setVisibility(View.VISIBLE);
            binding.signupLayout.setEnabled(false);
            binding.signupLayout.setVisibility(View.GONE);
        } else {
            binding.loginLayout.setEnabled(false);
            binding.loginLayout.setVisibility(View.GONE);
            binding.signupLayout.setEnabled(true);
            binding.signupLayout.setVisibility(View.VISIBLE);
        }
    }
}