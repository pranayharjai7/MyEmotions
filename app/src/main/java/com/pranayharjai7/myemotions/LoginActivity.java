package com.pranayharjai7.myemotions;

import static com.pranayharjai7.myemotions.Utils.FragmentUtils.replaceLoginFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.pranayharjai7.myemotions.ViewModels.LoginViewModel;
import com.pranayharjai7.myemotions.ViewModels.RegisterViewModel;
import com.pranayharjai7.myemotions.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    public static final String LOGIN = "LOGIN";
    public static final String REGISTER = "REGISTER";
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
            replaceLoginFragment(fragmentManager, LOGIN);
        }
    }

    private void observations() {
        registerViewModel.getRegisterUserFragmentView().observe(this, view -> loginMenuButtonClicked(binding.loginMenuButton));
    }

    public void loginButtonClicked(View view) {
        loginViewModel.setView(view);
    }

    public void googleLoginButtonClicked(View view) {
        Toast.makeText(this, "Under Development, please sign up", Toast.LENGTH_SHORT).show();
    }

    public void facebookLoginButtonClicked(View view) {
        Toast.makeText(this, "Under Development, please sign up", Toast.LENGTH_SHORT).show();
    }

    public void twitterLoginButtonClicked(View view) {
        Toast.makeText(this, "Under Development, please sign up", Toast.LENGTH_SHORT).show();
    }

    public void registerButtonClicked(View view) {
        registerViewModel.setLoginActivityView(view);
    }

    /**
     * TO change menu to Login.
     *
     * @param view
     */
    public void loginMenuButtonClicked(View view) {
        replaceLoginFragment(fragmentManager, LOGIN);
        changeMenu(view, binding.signupMenuButton);
    }

    /**
     * To change menu to Register.
     *
     * @param view
     */
    public void signupMenuButtonClicked(View view) {
        replaceLoginFragment(fragmentManager, REGISTER);
        changeMenu(view, binding.loginMenuButton);
    }

    private void changeMenu(View view1, View view2) {
        ((Button) view1).setTextColor(Color.parseColor("#8838F8"));
        ((Button) view2).setTextColor(Color.parseColor("#FFAAAAAA"));
        binding.loginMenuDivider.setX(view1.getX() + 30);
    }
}