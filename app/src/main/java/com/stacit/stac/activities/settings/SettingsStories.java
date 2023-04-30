package com.stacit.stac.activities.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.stacit.stac.R;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySettingsStoriesBinding;

public class SettingsStories extends AppCompatActivity {
    private ActivitySettingsStoriesBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsStoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        binding.imageCancelBtn.setOnClickListener(view -> onBackPressed());
    }
}