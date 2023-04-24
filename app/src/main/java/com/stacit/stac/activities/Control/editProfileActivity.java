package com.stacit.stac.activities.Control;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.stacit.stac.activities.StacAI.ModelLanguages;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.EditUserProfileBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class editProfileActivity extends AppCompatActivity
{

    private EditUserProfileBinding binding;
    private PreferenceManager preferenceManager;
    private ArrayList<ModelLanguages> languageArrayList;
    private String targetLanguageCode = "en";
    private String targetLanguageTitle = "English";
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = EditUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize variables
        preferenceManager = new PreferenceManager(getApplicationContext());

        loadAvailableLanguages();
        loadCurrentInfo();
        setOnPressedListener();
        pullPreferences();
        pushPreferences();
        pushUserPreferences();
    }
    //load available languages into the list array
    private void loadAvailableLanguages()
    {
        languageArrayList = new ArrayList<>();

        List<String> languageCodeList = TranslateLanguage.getAllLanguages();
        for (String languageCode: languageCodeList)
        {
            String languageTitle = new Locale(languageCode).getDisplayLanguage();
            ModelLanguages modelLanguages = new ModelLanguages(languageCode, languageTitle);
            languageArrayList.add(modelLanguages);
        }
    }
    //creates a drop-down menu for the language pack
    private void setLanguagePref()
    {
        //create a popUpMenu for target language selection
        //will change this a dropDown menu at some point
        PopupMenu popupMenu = new PopupMenu(this, binding.textLanguage);
        for (int i=0; i<languageArrayList.size(); i++)
        {
            popupMenu.getMenu().add(Menu.NONE, i, i, languageArrayList.get(i).getLanguageTitle());
        }
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            //get clicked item position/index from the list
            int position = menuItem.getItemId();
            //get code and title of the language selected
            targetLanguageCode = languageArrayList.get(position).getLanguageCode();
            targetLanguageTitle = languageArrayList.get(position).getLanguageTitle();
            //updated the preference manager
            if (targetLanguageTitle != null && targetLanguageCode != null)
            {
                preferenceManager.putString(Constants.KEY_LANGUAGE, targetLanguageTitle);
                preferenceManager.putString(Constants.KEY_LANGUAGE_CODE, targetLanguageCode);
                //immediately update the textView
                binding.textLanguage.setText(targetLanguageTitle);
                showToast("Language changed");
            }
            else
            {
                showToast("Language not selected");
            }
            return false;
        });
    }
    //populates the view with values from the Constants class after signIn
    @SuppressLint("SetTextI18n")
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
        data.put(Constants.KEY_LANGUAGE_CODE, preferenceManager.getString(Constants.KEY_LANGUAGE_CODE));

        db.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update(data);
    }
    //function used to make a call to the Toast method
    private void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
    //updates the settings view with the appropriate user data/information
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
        binding.imageLanguageView.setOnClickListener(view -> setLanguagePref());
    }

}