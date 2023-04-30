package com.stacit.stac.activities.Control;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.stacit.stac.R;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySignInBinding;

import java.util.HashMap;

public class signInActivity extends AppCompatActivity
{
    //making instances of local classes
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;
    //default onCreate method that's called when this activity is created
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN))
        {
            Intent intent = new Intent(getApplicationContext(), conversationMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }

    //onClick Listener for what to happen after the signIn btn is clicked
    private void setListener()
    {
        binding.textCreateNewAccount.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), signUpActivity.class)));
        binding.btnSignIn.setOnClickListener(view -> {
            if (isValidSignInDetails())
            {
                signIn();
            }
        });
    }
    //check to see if the account has every object in the constant class
    private void updateOlderAccounts(String userID)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                AggregateQuerySnapshot snapshot = task.getResult();
                if (snapshot.getCount() < 12 || snapshot.getCount() < 17)
                {
                    updateUser(userID);
                }
            }
        });
    }
    //function to update older user accounts with new objects
    private void updateUser(String userID)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.KEY_NIGHT_MODE, "Enabled");
        data.put(Constants.KEY_SECURITY_PRIVACY, "Enabled");
        data.put(Constants.KEY_PRIVATE_ACCOUNT, "Enabled");
        data.put(Constants.KEY_NOTIFICATION, "Enabled");
        data.put(Constants.KEY_LANGUAGE_CODE, "EN");
        data.put(Constants.KEY_TYPING, "false");

        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(userID)
                .set(data, SetOptions.merge());
    }
    //checks the input validation and then checks the database for the signIn details entered
    private void signIn(){
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    //updating older accounts
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    updateOlderAccounts(document.getId());

                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0)
                    {
                        //getting data from the database and putting them in the Constants class using the preferenceManager class
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        preferenceManager.putString(Constants.KEY_PROFILE_HOLDER, documentSnapshot.getString(Constants.KEY_IMAGE));
                        preferenceManager.putString(Constants.KEY_LANGUAGE, documentSnapshot.getString(Constants.KEY_LANGUAGE));
                        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                        preferenceManager.putString(Constants.KEY_PASSWORD, documentSnapshot.getString(Constants.KEY_PASSWORD));
                        preferenceManager.putString(Constants.KEY_SECURITY_PRIVACY, documentSnapshot.getString(Constants.KEY_SECURITY_PRIVACY));
                        preferenceManager.putString(Constants.KEY_THEME_SELECTED, "System");

                        int chatBubbleColor = ContextCompat.getColor(getApplicationContext(), R.color.chat_bubble_default);
                        preferenceManager.putString(Constants.KEY_CHAT_COLOR_SELECTION, "chat_default_color");
                        preferenceManager.putString(Constants.KEY_CHAT_BUBBLE_COLOR, Integer.toString(chatBubbleColor));

                        int backgroundWallpaper = ContextCompat.getColor(getApplicationContext(), R.color.chat_color_one);
                        preferenceManager.putString(Constants.KEY_WALLPAPER_SELECTION, "chatColor1");
                        preferenceManager.putString(Constants.KEY_WALLPAPER, Integer.toString(backgroundWallpaper));

                        //starting a new intent for the conversation tab if the user has an account
                        Intent intent = new Intent(getApplicationContext(), conversationMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else
                    {
                        //displays a toast message saying the user was unable to sign in
                        loading(false);
                        showToast("Unable to sign in");
                    }
                });
    }
    //makes calls to the Toast method to show messages on the screen
    private void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
    //this is an input validation function for the input fields
    private Boolean isValidSignInDetails()
    {
        if (binding.inputEmail.getText().toString().trim().isEmpty())
        {
            showToast("Enter email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches())
        {
            showToast("Invalid email");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty())
        {
            showToast("Enter password");
            return false;
        } else
        {
            return true;
        }
    }
    //loading bar visibility function
    private void loading(Boolean isLoading)
    {
        if (isLoading)
        {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.btnSignIn.setVisibility(View.INVISIBLE);
        } else
        {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.btnSignIn.setVisibility(View.VISIBLE);
        }
    }

}