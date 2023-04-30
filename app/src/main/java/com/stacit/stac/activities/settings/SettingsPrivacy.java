package com.stacit.stac.activities.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.stacit.stac.R;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySettingsPrivacyBinding;

public class SettingsPrivacy extends AppCompatActivity {
    private ActivitySettingsPrivacyBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsPrivacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        binding.imageCancelBtn.setOnClickListener(view -> onBackPressed());
    }
}