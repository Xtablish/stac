package com.stacit.stac.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.stacit.stac.R;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivityHomeBinding;

public class homeActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.stacit.stac.databinding.ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN))
        {
            //on click listener for the navigation bar
            binding.bottomNavMenu.setOnItemSelectedListener(item -> {
                switch (item.getItemId())
                {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), homeActivity.class));
                        break;
                    case R.id.chat:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        break;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), userProfileActivity.class));
                        break;
                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(), settingsActivity.class));
                        break;
                }
                return true;
            });
        }
    }
}