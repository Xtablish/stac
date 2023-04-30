package com.stacit.stac.activities.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stacit.stac.activities.Control.signInActivity;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySettingsAccountBinding;

import java.util.HashMap;

public class SettingsAccount extends AppCompatActivity {
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.stacit.stac.databinding.ActivitySettingsAccountBinding binding = ActivitySettingsAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        binding.settingsAboutSignOutAccount.setOnClickListener(view -> userSignOut());
        binding.inputEmail.setHint(preferenceManager.getString(Constants.KEY_EMAIL));

        if (binding.inputEmail.getText().toString().isEmpty())
        {
            binding.imageCancelBtn.setOnClickListener(view -> onBackPressed());
        }
        binding.settingsAboutDeleteAccount.setOnClickListener(view -> deleteAccount());
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

    private void deleteAccount()
    {

    }

    private void showToast(String message)
    {
        //displays a toast message to the view
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            View view = getCurrentFocus();
            if (view instanceof EditText)
            {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY()))
                {
                    view.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}