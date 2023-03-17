package com.stacit.stac.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.stacit.stac.R;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivityUserProfileBinding;

public class userProfileActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.stacit.stac.databinding.ActivityUserProfileBinding binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
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
                        startActivity(new Intent(getApplicationContext(), conversationChatActivity.class));
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

            //import user data from firebase
            binding.textUsername.setText(preferenceManager.getString(Constants.KEY_NAME));
            binding.prefAI.setText(preferenceManager.getString(Constants.KEY_AI));
            binding.prefCountry.setText(preferenceManager.getString(Constants.KEY_COUNTRY));
            binding.prefFaceID.setText(preferenceManager.getString(Constants.KEY_FACE_ID));
            binding.prefListen.setText(preferenceManager.getString(Constants.KEY_LISTEN));
            binding.prefVoice.setText(preferenceManager.getString(Constants.KEY_VOICE));
            binding.prefAI.setText(preferenceManager.getString(Constants.KEY_AI));

            //adding image to the profile view
            //decode base64 string to image
            byte[] imageBytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            binding.imageProfile.setImageBitmap(decodedImage);
        }else
        {
            Intent intent = new Intent(getApplicationContext(), signInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

}