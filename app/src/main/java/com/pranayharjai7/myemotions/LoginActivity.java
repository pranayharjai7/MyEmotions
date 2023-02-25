package com.pranayharjai7.myemotions;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.pranayharjai7.myemotions.Fragments.LoginActivityFragments.LoginUserFragment;
import com.pranayharjai7.myemotions.Fragments.LoginActivityFragments.RegisterUserFragment;
import com.pranayharjai7.myemotions.ViewModels.LoginViewModel;
import com.pranayharjai7.myemotions.ViewModels.RegisterViewModel;
import com.pranayharjai7.myemotions.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private LoginViewModel loginViewModel;
    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init(savedInstanceState);
        observations();
    }

    private void init(Bundle savedInstanceState) {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        if (savedInstanceState == null) {
            replaceFragment("LOGIN");
        }
    }

    private void observations() {
        registerViewModel.getRegisterUserFragmentView().observe(this, view -> loginMenuButtonClicked(binding.loginMenuButton));
    }

    public void loginButtonClicked(View view) {
        loginViewModel.setView(view);
    }

    public void googleLoginButtonClicked(View view) {
    }

    public void facebookLoginButtonClicked(View view) {
    }

    public void twitterLoginButtonClicked(View view) {
    }

    public void registerButtonClicked(View view) {
        registerViewModel.setLoginActivityView(view);
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
     * TO change menu to Login.
     *
     * @param view
     */
    public void loginMenuButtonClicked(View view) {
        replaceFragment("LOGIN");
        changeMenu(view, binding.signupMenuButton);
    }

    /**
     * To change menu to Register.
     *
     * @param view
     */
    public void signupMenuButtonClicked(View view) {
        replaceFragment("REGISTER");
        changeMenu(view, binding.loginMenuButton);
    }

    private void replaceFragment(String fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.fade_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.fade_out  // popExit
        );

        switch (fragment) {
            case "LOGIN": {
                transaction.replace(R.id.loginFragmentContainerView, LoginUserFragment.class, null);
                break;
            }
            case "REGISTER": {
                transaction.replace(R.id.loginFragmentContainerView, RegisterUserFragment.class, null);
                break;
            }
            default: {
                transaction.replace(R.id.loginFragmentContainerView, LoginUserFragment.class, null);
            }
        }

        transaction.setReorderingAllowed(true)
                //.addToBackStack(fragment)
                .commit();
    }

    private void changeMenu(View view1, View view2) {
        ((Button) view1).setTextColor(Color.parseColor("#8838F8"));
        ((Button) view2).setTextColor(Color.parseColor("#FFAAAAAA"));
        binding.loginMenuDivider.setX(view1.getX() + 30);
    }
}