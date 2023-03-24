package com.stacit.stac.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySettingsProfilePageBinding;

import java.util.HashMap;
import java.util.Objects;

public class settingsProfileActivity extends AppCompatActivity {
    //private members initialization
    private ActivitySettingsProfilePageBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsProfilePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize variables
        preferenceManager = new PreferenceManager(getApplicationContext());

        //import user data from firebase
        binding.textUsername.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.textUserEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));

        //adding image to the profile view
        //decode base64 string to image
        byte[] imageBytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        binding.imageProfileMain.setImageBitmap(decodedImage);
        binding.profileImage.setImageBitmap(decodedImage);

        //calling functions
        pushPreferences();
        editProfileListener();
        pullPreferences();
        pushUserPreferences();
    }
    //this is the control apparatus onClick listener for the settings page
    private void editProfileListener()
    {
        binding.textEditProfile.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), editProfileActivity.class)));
        binding.logoutIcon.setOnClickListener(view -> userSignOut());
        binding.imageBackBtn.setOnClickListener(view -> onBackPressed());
    }
    //used to change the state of the toggle switches on the settings page
    private void pullPreferences()
    {
        //set the switch toggle state for Facial Recognition
        binding.faceIdToggle.setChecked(Objects.equals(preferenceManager.getString(Constants.KEY_FACE_ID), "Enabled"));
        //set the switch toggle state for Notifications
        binding.notificationsToggle.setChecked(Objects.equals(preferenceManager.getString(Constants.KEY_NOTIFICATION), "Enabled"));
        //set the switch toggle state for Night Mode
        binding.nightModeToggle.setChecked(Objects.equals(preferenceManager.getString(Constants.KEY_NIGHT_MODE), "Enabled"));
        //set the switch toggle state for Private Account
        binding.privateAccountToggle.setChecked(Objects.equals(preferenceManager.getString(Constants.KEY_PRIVATE_ACCOUNT), "Enabled"));
        //set the switch toggle state for Security and Privacy
        binding.securityModeToggle.setChecked(Objects.equals(preferenceManager.getString(Constants.KEY_SECURITY_PRIVACY), "Enabled"));
        //set the switch toggle state for AI & ML
        binding.AIModeToggle.setChecked(Objects.equals(preferenceManager.getString(Constants.KEY_AI), "Enabled"));
        //set the switch toggle state for Listening
        binding.listeningToggle.setChecked(Objects.equals(preferenceManager.getString(Constants.KEY_LISTEN), "Enabled"));
    }
    //this function updates the preferenceManager class to the current state of the toggle switches
    private void pushPreferences()
    {
        //update the Facial Recognition object value
        if (binding.securityModeToggle.isChecked())
        {
            preferenceManager.putString(Constants.KEY_SECURITY_PRIVACY, "Enabled");
        }
        else
        {
            preferenceManager.putString(Constants.KEY_SECURITY_PRIVACY, "Disabled");
        }
        //update the Notification object value
        if (binding.notificationsToggle.isChecked())
        {
            preferenceManager.putString(Constants.KEY_NOTIFICATION, "Enabled");
        }
        else
        {
            preferenceManager.putString(Constants.KEY_NOTIFICATION, "Disabled");
        }
        //update the Night Mode object value
        if (binding.nightModeToggle.isChecked())
        {
            preferenceManager.putString(Constants.KEY_NIGHT_MODE, "Enabled");
        }
        else
        {
            preferenceManager.putString(Constants.KEY_NIGHT_MODE, "Disabled");
        }
        //update the Private Account object value
        if (binding.privateAccountToggle.isChecked())
        {
            preferenceManager.putString(Constants.KEY_PRIVATE_ACCOUNT, "Enabled");
        }
        else
        {
            preferenceManager.putString(Constants.KEY_PRIVATE_ACCOUNT, "Disabled");
        }
        //update the Security and Privacy object value
        if (binding.faceIdToggle.isChecked())
        {
            preferenceManager.putString(Constants.KEY_FACE_ID, "Enabled");
        }
        else
        {
            preferenceManager.putString(Constants.KEY_FACE_ID, "Disabled");
        }
        //update the AI & ML object value
        if (binding.AIModeToggle.isChecked())
        {
            preferenceManager.putString(Constants.KEY_AI, "Enabled");
        }
        else
        {
            preferenceManager.putString(Constants.KEY_AI, "Disabled");
        }
        //update the Listening object value
        if (binding.listeningToggle.isChecked())
        {
            preferenceManager.putString(Constants.KEY_LISTEN, "Enabled");
        }
        else
        {
            preferenceManager.putString(Constants.KEY_LISTEN, "Disabled");
        }
    }
    //update the changes from made from the toggle switches in the database
    private void pushUserPreferences()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.KEY_FACE_ID, preferenceManager.getString(Constants.KEY_FACE_ID));
        data.put(Constants.KEY_AI, preferenceManager.getString(Constants.KEY_AI));
        data.put(Constants.KEY_LISTEN, preferenceManager.getString(Constants.KEY_LISTEN));
        data.put(Constants.KEY_NIGHT_MODE, preferenceManager.getString(Constants.KEY_NIGHT_MODE));
        data.put(Constants.KEY_SECURITY_PRIVACY, preferenceManager.getString(Constants.KEY_SECURITY_PRIVACY));
        data.put(Constants.KEY_PRIVATE_ACCOUNT, preferenceManager.getString(Constants.KEY_PRIVATE_ACCOUNT));
        data.put(Constants.KEY_NOTIFICATION, preferenceManager.getString(Constants.KEY_NOTIFICATION));

        db.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update(data);
    }
    //this function signs the user out of the application
    private void userSignOut()
    {
        //create an instance of the database and the find the userID, then delete their current FCM token
        showToast("Signing you out " + preferenceManager.getString(Constants.KEY_NAME));
        FirebaseFirestore fire_store = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                fire_store.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );

        HashMap<String, Object> updateUserStatus = new HashMap<>();
        updateUserStatus.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updateUserStatus)
                .addOnSuccessListener(unused ->
                {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), signInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("Something went wrong"));
    }
    //this function takes a string and displays a toast message
    private void showToast(String message)
    {
        //displays a toast message to the view
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}