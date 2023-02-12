package com.pranayharjai7.myemotions.LoginAndRegister;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.pranayharjai7.myemotions.MainActivity;
import com.pranayharjai7.myemotions.databinding.ActivityLoginBinding;

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

    public void loginButtonClicked(View view) {

        String email = binding.emailAddressLoginEditText.getText().toString();
        String password = binding.passwordLoginEditText.getText().toString();

        if (checkLogin(email, password)) {
           loginUserWithFirebase(email, password);
        }
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

    /**
     * Going to Main activity after logging in user.
     */
    private void login() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Login using email and password in Firebase Authentication.
     * @param email
     * @param password
     */
    private void loginUserWithFirebase(String email, String password) {
        binding.progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show();
                        login();
                    } else {
                        Toast.makeText(this, "Failed to Login!! Please Check Again!", Toast.LENGTH_SHORT).show();
                    }
                    binding.progressBar.setVisibility(View.GONE);
                });
    }

    /**
     * Register user using email and password in Firebase Authentication.
     * @param username
     * @param email
     * @param password
     */
    private void registerUserWithFirebase(String username, String email, String password) {
        binding.progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //To add username to profile
                        task.getResult().getUser()
                                .updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(username).build());
                        Toast.makeText(this, "User Registered successfully!", Toast.LENGTH_SHORT).show();
                        mAuth.signOut(); //By default, registered user gets signed in
                        loginMenuButtonClicked(binding.loginMenuButton);
                    } else {
                        Toast.makeText(this, "Failed to Register,Try Again!", Toast.LENGTH_SHORT).show();
                    }
                    binding.progressBar.setVisibility(View.GONE);
                });
    }

    /**
     * To check if login parameters are correct or not.
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

    /**
     * To check if registration parameters are correct or not.
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


    /**
     * TO change menu to Login.
     * @param view
     */
    public void loginMenuButtonClicked(View view) {
        changeMenu(view, binding.signupMenuButton);

    }

    /**
     * To change menu to Register.
     * @param view
     */
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