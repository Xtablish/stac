package com.stacit.stac.activities.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.stacit.stac.databinding.ActivitySettingsAboutBinding;

public class SettingsAbout extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.stacit.stac.databinding.ActivitySettingsAboutBinding binding = ActivitySettingsAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageCancelBtn.setOnClickListener(view -> onBackPressed());
    }
}