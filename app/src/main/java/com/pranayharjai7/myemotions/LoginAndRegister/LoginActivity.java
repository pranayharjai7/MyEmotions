package com.pranayharjai7.myemotions.LoginAndRegister;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pranayharjai7.myemotions.MainActivity;
import com.pranayharjai7.myemotions.R;
import com.pranayharjai7.myemotions.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void loginButtonClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void signupButtonClicked(View view) {
        loginMenuButtonClicked(binding.loginMenuButton);
    }

    public void loginMenuButtonClicked(View view) {
        changeMenu(view,binding.signupMenuButton);

    }

    public void signupMenuButtonClicked(View view) {
        changeMenu(view,binding.loginMenuButton);
    }

    private void changeMenu(View view1, View view2) {
        ((Button)view1).setTextColor(Color.parseColor("#8838F8"));
        ((Button)view2).setTextColor(Color.parseColor("#FFAAAAAA"));
        binding.loginMenuDivider.setX(((Button)view1).getX()+30);
        if(view1.equals(binding.loginMenuButton)) {
            binding.loginLayout.setEnabled(true);
            binding.loginLayout.setVisibility(View.VISIBLE);
            binding.signupLayout.setEnabled(false);
            binding.signupLayout.setVisibility(View.GONE);
        }
        else {
            binding.loginLayout.setEnabled(false);
            binding.loginLayout.setVisibility(View.GONE);
            binding.signupLayout.setEnabled(true);
            binding.signupLayout.setVisibility(View.VISIBLE);
        }
    }


}