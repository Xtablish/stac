package com.stacit.stac.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.stacit.stac.databinding.ActivityGetStartedBinding;

public class getStartedActivity extends AppCompatActivity
{

    private ActivityGetStartedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityGetStartedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getStartedListener();
    }

    private void getStartedListener()
    {
        binding.btnSignIn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), signInActivity.class)));
        binding.btnSignUp.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), signUpActivity.class)));
    }

    private void loading(Boolean isLoading)
    {
        if (isLoading)
        {

            binding.btnSignInFrame.setVisibility(View.INVISIBLE);
            binding.btnSignUpFrame.setVisibility(View.INVISIBLE);
            binding.imageIconFrame.setVisibility(View.INVISIBLE);
            binding.textAppStac.setVisibility(View.INVISIBLE);
            binding.textAboutMessage.setVisibility(View.INVISIBLE);
        }
        else
        {
            binding.btnSignInFrame.setVisibility(View.VISIBLE);
            binding.btnSignUpFrame.setVisibility(View.VISIBLE);
            binding.imageIconFrame.setVisibility(View.VISIBLE);
            binding.textAppStac.setVisibility(View.VISIBLE);
            binding.textAboutMessage.setVisibility(View.VISIBLE);
        }
    }
}