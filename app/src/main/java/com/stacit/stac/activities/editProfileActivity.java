package com.stacit.stac.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySettingsProfilePageBinding;
import com.stacit.stac.databinding.EditUserProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

public class editProfileActivity extends AppCompatActivity
{

    private EditUserProfileBinding binding;
    private String encodedImage;
    private PreferenceManager preferenceManager;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = EditUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize variables
        preferenceManager = new PreferenceManager(getApplicationContext());


        loadCurrentInfo();
        setOnPressedListener();
        pullPreferences();
        pushPreferences();
        pushUserPreferences();
    }

    private void loadCurrentInfo()
    {
        //import user data from firebase
        binding.textUsername.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.textGreeting.setText("Good morning");

        //adding image to the profile view
        //decode base64 string to image
        byte[] imageBytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        binding.imageProfileMain.setImageBitmap(decodedImage);
        binding.profileImage.setImageBitmap(decodedImage);
    }
    //this function updates the preferenceManager class to the current state of the toggle switches
    private void pushPreferences()
    {
        //update the Facial Recognition object value
        binding.securityModeToggle.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
            {
                preferenceManager.putString(Constants.KEY_SECURITY_PRIVACY, "Enabled");
            }
            else
            {
                preferenceManager.putString(Constants.KEY_SECURITY_PRIVACY, "Disabled");
            }
        });
        //update the Private Account object value
        binding.privateAccountToggle.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
            {
                preferenceManager.putString(Constants.KEY_PRIVATE_ACCOUNT, "Enabled");
            }
            else
            {
                preferenceManager.putString(Constants.KEY_PRIVATE_ACCOUNT, "Disabled");
            }
        });
        //update the Facial Recognition object value
        binding.faceIdToggle.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
            {
                preferenceManager.putString(Constants.KEY_FACE_ID, "Enabled");
            }
            else
            {
                preferenceManager.putString(Constants.KEY_FACE_ID, "Disabled");
            }
        });
        //update the AI & ML object value
        binding.AIModeToggle.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
            {
                preferenceManager.putString(Constants.KEY_AI, "Enabled");
            }
            else
            {
                preferenceManager.putString(Constants.KEY_AI, "Disabled");
            }
        });
        //update the Listening object value
        binding.listeningToggle.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
            {
                preferenceManager.putString(Constants.KEY_LISTEN, "Enabled");
            }
            else
            {
                preferenceManager.putString(Constants.KEY_LISTEN, "Disabled");
            }
        });
    }
    //update the changes from made from the toggle switches in the database
    private void pushUserPreferences()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.KEY_FACE_ID, preferenceManager.getString(Constants.KEY_FACE_ID));
        data.put(Constants.KEY_AI, preferenceManager.getString(Constants.KEY_AI));
        data.put(Constants.KEY_LISTEN, preferenceManager.getString(Constants.KEY_LISTEN));
        data.put(Constants.KEY_SECURITY_PRIVACY, preferenceManager.getString(Constants.KEY_SECURITY_PRIVACY));
        data.put(Constants.KEY_PRIVATE_ACCOUNT, preferenceManager.getString(Constants.KEY_PRIVATE_ACCOUNT));
        data.put(Constants.KEY_LANGUAGE, preferenceManager.getString(Constants.KEY_LANGUAGE));

        db.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update(data);
    }

    //function used to make a call to the Toast method
    private void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void pullPreferences()
    {
        //set the switch toggle state for Facial Recognition
        binding.faceIdToggle.setChecked(Objects.equals(preferenceManager.getString(Constants.KEY_FACE_ID), "Enabled"));
        //set the switch toggle state for Private Account
        binding.privateAccountToggle.setChecked(Objects.equals(preferenceManager.getString(Constants.KEY_PRIVATE_ACCOUNT), "Enabled"));
        //set the switch toggle state for Security and Privacy
        binding.securityModeToggle.setChecked(Objects.equals(preferenceManager.getString(Constants.KEY_SECURITY_PRIVACY), "Enabled"));
        //set the switch toggle state for AI & ML
        binding.AIModeToggle.setChecked(Objects.equals(preferenceManager.getString(Constants.KEY_AI), "Enabled"));
        //set the switch toggle state for Listening
        binding.listeningToggle.setChecked(Objects.equals(preferenceManager.getString(Constants.KEY_LISTEN), "Enabled"));
        //set current language
        binding.textLanguage.setText(preferenceManager.getString(Constants.KEY_LANGUAGE));
    }


    //listens to see if a user clicks a button
    private void setOnPressedListener(){
        binding.imageBackBtn.setOnClickListener(view -> onBackPressed());
        binding.imagePersonalInformation.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), personalInformationActivity.class)));
    }

}