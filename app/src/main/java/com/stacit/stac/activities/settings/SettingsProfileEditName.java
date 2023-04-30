package com.stacit.stac.activities.settings;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySettingsProfileEditNameBinding;

public class SettingsProfileEditName extends AppCompatActivity {
    private ActivitySettingsProfileEditNameBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsProfileEditNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        binding.imageCancelBtn.setOnClickListener(view -> onBackPressed());

        checkName();
        updateName();
    }

    private void updateName()
    {

    }

    private void checkName()
    {
        String firstname = "";
        String lastname = "";
        if (preferenceManager.getString(Constants.KEY_NAME) != null)
        {
            String signUpName = preferenceManager.getString(Constants.KEY_NAME);
            String[] splitName = signUpName.trim().split("\\s+");

            for (int i = 0; i<splitName.length; i++)
            {
                if (i == 0)
                {
                    firstname = splitName[i];
                }
                else if (i == 1)
                {
                    lastname = splitName[i];
                }
            }

            binding.inputFirstName.setHint(firstname);
            binding.inputLastName.setHint(lastname);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}