package com.stacit.stac.activities.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySettingsAppearanceBinding;

public class SettingsAppearance extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private ActivitySettingsAppearanceBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsAppearanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        if (preferenceManager.getString(Constants.KEY_THEME_SELECTED) != null)
        {
            binding.textAppearanceThemeDynamic.setText(preferenceManager.getString(Constants.KEY_THEME_SELECTED));
        }

        binding.imageCancelBtn.setOnClickListener(view -> onBackPressed());
        binding.settingsAppearanceThemeMore.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SettingsAppearanceThemeSelection.class)));
        binding.settingsAppearanceChatWallpaperMore.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SettingsAppearanceChat.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preferenceManager.getString(Constants.KEY_THEME_SELECTED) != null)
        {
            binding.textAppearanceThemeDynamic.setText(preferenceManager.getString(Constants.KEY_THEME_SELECTED));
        }
    }
}