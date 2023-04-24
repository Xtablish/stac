package com.stacit.stac.activities.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.stacit.stac.R;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySettingsProfileEditPhotoBinding;

public class SettingsProfileEditPhoto extends AppCompatActivity
{

    private ActivitySettingsProfileEditPhotoBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsProfileEditPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        //adding image to the profile view
        //decode base64 string to image
        byte[] imageBytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        binding.imageProfile.setImageBitmap(decodedImage);

        SettingsProfileEditProfileOnClickListener();
    }

    private void SettingsProfileEditProfileOnClickListener()
    {
        binding.imageTextBtn.setOnClickListener(view -> textImageProfile());
    }

    private void textImageProfile()
    {
        binding.imageProfile.setBackgroundColor(getResources().getColor(R.color.light_grey));
        
        binding.textUsernameInitials.setVisibility(View.VISIBLE);
    }
}