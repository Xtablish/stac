package com.stacit.stac.activities.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.stacit.stac.R;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySettingsStoriesAccessBinding;

public class SettingsStoriesAccess extends AppCompatActivity {
    private ActivitySettingsStoriesAccessBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsStoriesAccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        binding.imageCancelBtn.setOnClickListener(view -> onBackPressed());
    }
}