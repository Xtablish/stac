package com.stacit.stac.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.stacit.stac.databinding.ActivityGetStartedBinding;

public class getStartedActivity extends AppCompatActivity {

    private ActivityGetStartedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetStartedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getStartedListener();
    }

    private void getStartedListener(){
        binding.btnGetStarted.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), signInActivity.class)));
    }
}