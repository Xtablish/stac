package com.stacit.stac.activities.settings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySettingsProfileBinding;

public class SettingsProfile extends AppCompatActivity
{

    private ActivitySettingsProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());

        //import user data from firebase
        binding.textUsername.setText(preferenceManager.getString(Constants.KEY_NAME));

        //adding image to the profile view
        //decode base64 string to image
        byte[] imageBytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        binding.imageProfile.setImageBitmap(decodedImage);

        SettingsProfileSetOnClickListener();
    }

    private void SettingsProfileSetOnClickListener()
    {
        binding.imageBackBtn.setOnClickListener(view -> onBackPressed());
        binding.settingsProfileAboutIcon.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SettingsProfileAbout.class)));
        binding.settingsProfileNameIcon.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SettingsProfileEditName.class)));
        binding.textEditProfileImage.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SettingsProfileEditPhoto.class)));
    }
}