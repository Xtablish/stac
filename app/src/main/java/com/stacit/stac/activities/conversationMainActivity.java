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
import com.google.firebase.messaging.FirebaseMessaging;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySignOutBinding;

import java.util.HashMap;

public class conversationMainActivity extends AppCompatActivity
{

    private ActivitySignOutBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivitySignOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //creating an instance of the preferenceManager class
        preferenceManager = new PreferenceManager(getApplicationContext());
        //call the getUserInfo function
        getUserInfo();
        //call to the getUserToken function to update the KEY_FCM_TOKEN variable
        getUserToken();
        //call to the signOutListener to sign the current user out
        signOutListener();
    }

    private void signOutListener()
    {
        //listens for an onClick event, if there is, a call will be made to the userSignOut function
        binding.imageSignOut.setOnClickListener(view -> userSignOut());
    }

    private void getUserInfo()
    {
        //binding the username from the local Constants class to the textView
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        //binding the user profileImage to the imageView
        byte [] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

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

    private void updateUserToken(String userToken)
    {
        //creating an instance of the fireBase fireStore and getting the userID
        FirebaseFirestore fire_store = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                fire_store.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        //updating that userID
        documentReference.update(Constants.KEY_FCM_TOKEN, userToken)
                .addOnSuccessListener(unused -> showToast("Token update success"))
                .addOnFailureListener(e -> showToast("Token update failed"));

    }

    private  void getUserToken()
    {
        //creates a fireBase messaging instance and then use that token to update the KEY_FCM_TOKEN VALUE
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateUserToken);
    }

    private void showToast(String message)
    {
        //displays a toast message to the view
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}