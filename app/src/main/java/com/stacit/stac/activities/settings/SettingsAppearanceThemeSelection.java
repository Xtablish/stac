package com.stacit.stac.activities.settings;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySettingsAppearanceThemeSelectionBinding;

public class SettingsAppearanceThemeSelection extends AppCompatActivity {
    private ActivitySettingsAppearanceThemeSelectionBinding binding;
    private PreferenceManager preferenceManager;
    private String themeSelected = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsAppearanceThemeSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        binding.imageCancelBtn.setOnClickListener(view -> onBackPressed());
        showCurrentTheme();
        selectTheme();
    }

    private void selectTheme()
    {
        binding.settingsAppearanceThemeSystemIcon.setOnClickListener(view -> {
            themeSelected = "System";
            showThemeSelection(themeSelected);
            updateTheme();
        });
        binding.textAppearanceThemeSystem.setOnClickListener(view -> {
            themeSelected = "System";
            showThemeSelection(themeSelected);
            updateTheme();
        });

        binding.settingsAppearanceThemeLightIcon.setOnClickListener(view -> {
            themeSelected = "light";
            showThemeSelection(themeSelected);
            updateTheme();
        });
        binding.textAppearanceThemeLight.setOnClickListener(view -> {
            themeSelected = "Light";
            showThemeSelection(themeSelected);
            updateTheme();
        });

        binding.settingsAppearanceThemeDarkIcon.setOnClickListener(view -> {
            themeSelected = "Dark";
            showThemeSelection(themeSelected);
            updateTheme();
        });
        binding.textAppearanceThemeDark.setOnClickListener(view -> {
            themeSelected = "Dark";
            showThemeSelection(themeSelected);
            updateTheme();
        });
    }

    private void showThemeSelection(String selection)
    {
        switch (selection)
        {
            case "System":
                binding.settingsAppearanceThemeSystemSelect.setVisibility(View.VISIBLE);
                binding.settingsAppearanceThemeLightSelect.setVisibility(View.INVISIBLE);
                binding.settingsAppearanceThemeDarkSelect.setVisibility(View.INVISIBLE);
                break;
            case "Light":
                binding.settingsAppearanceThemeSystemSelect.setVisibility(View.INVISIBLE);
                binding.settingsAppearanceThemeLightSelect.setVisibility(View.VISIBLE);
                binding.settingsAppearanceThemeDarkSelect.setVisibility(View.INVISIBLE);
                break;
            case "Dark":
                binding.settingsAppearanceThemeSystemSelect.setVisibility(View.INVISIBLE);
                binding.settingsAppearanceThemeLightSelect.setVisibility(View.INVISIBLE);
                binding.settingsAppearanceThemeDarkSelect.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void updateTheme()
    {
        preferenceManager.putString(Constants.KEY_THEME_SELECTED, themeSelected);
    }

    private void showCurrentTheme()
    {
        if (preferenceManager.getString(Constants.KEY_THEME_SELECTED) != null)
        {
            showThemeSelection(preferenceManager.getString(Constants.KEY_THEME_SELECTED));
        }
    }
}