package com.stacit.stac.activities.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.stacit.stac.R;
import com.stacit.stac.databinding.ActivitySettingsProfileAboutBinding;

public class SettingsProfileAbout extends AppCompatActivity
{

    private ActivitySettingsProfileAboutBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsProfileAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (binding.inputUserDescription.getText().toString().isEmpty())
        {
            binding.imageCancelBtn.setOnClickListener(view -> onBackPressed());
        }
    }
}