package com.stacit.stac.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySignInBinding;

public class signInActivity extends AppCompatActivity {


    //making instances of local classes
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    //default onCreate method that's called when this activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN))
        {
            Intent intent = new Intent(getApplicationContext(), userProfileActivity.class);
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


    //checks the input validation and then checks the database for the signIn details entered
    private void signIn(){
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0)
                    {
                        //getting data from the database and putting them in the Constants class using the preferenceManager class
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        preferenceManager.putString(Constants.KEY_FACE_ID, documentSnapshot.getString(Constants.KEY_FACE_ID));
                        preferenceManager.putString(Constants.KEY_LANGUAGE, documentSnapshot.getString(Constants.KEY_LANGUAGE));
                        preferenceManager.putString(Constants.KEY_LISTEN, documentSnapshot.getString(Constants.KEY_LISTEN));
                        preferenceManager.putString(Constants.KEY_VOICE, documentSnapshot.getString(Constants.KEY_VOICE));
                        preferenceManager.putString(Constants.KEY_AI, documentSnapshot.getString(Constants.KEY_AI));
                        preferenceManager.putString(Constants.KEY_COUNTRY, documentSnapshot.getString(Constants.KEY_COUNTRY));

                        //starting a new intent for the conversation tab if the user has an account
                        Intent intent = new Intent(getApplicationContext(), homeActivity.class);
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