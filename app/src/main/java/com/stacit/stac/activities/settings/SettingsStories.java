package com.stacit.stac.activities.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.stacit.stac.R;
import com.stacit.stac.activities.utilities.Constants;
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
        binding.settingsStoriesAccessSelect.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SettingsStoriesAccess.class)));

        //adding image to the profile view
        //decode base64 string to image
        byte[] imageBytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        binding.profileImage.setImageBitmap(decodedImage);

        deleteStories();
        updateViewReceipt();
    }

    private void deleteStories()
    {

    }

    private void updateViewReceipt()
    {

    }
}