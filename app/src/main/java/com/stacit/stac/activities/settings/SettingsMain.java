package com.stacit.stac.activities.settings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySettingsMainBinding;

public class SettingsMain extends AppCompatActivity
{

    private ActivitySettingsMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());

        //import user data from firebase
        binding.userTextName.setText(preferenceManager.getString(Constants.KEY_NAME));

        //adding image to the profile view
        //decode base64 string to image
        byte[] imageBytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        binding.profileImage.setImageBitmap(decodedImage);

        SettingsMainOnClickListener();
    }

    private void SettingsMainOnClickListener()
    {
        binding.profileSettingsIcon.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SettingsProfile.class)));
        binding.AccountSettingsIcon.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SettingsAccount.class)));
        binding.AppearanceIcon.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SettingsAppearance.class)));
        binding.ChatsSettingsIcon.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SettingsChat.class)));
        binding.StoriesSettingsIcon.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SettingsStories.class)));
        binding.NotificationsSettingsIcon.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SettingsNotifications.class)));
        binding.PrivacySettingsIcon.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SettingsPrivacy.class)));
        binding.AboutAppSettingsIcon.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SettingsAbout.class)));
        binding.imageBackBtn.setOnClickListener(view -> onBackPressed());
    }
}