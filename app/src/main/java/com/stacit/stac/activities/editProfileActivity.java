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
import com.stacit.stac.databinding.EditUserProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

        OnButtonPressed();
        loadCurrentInfo();
        setOnPressedListener();
    }

    private void OnButtonPressed()
    {
        binding.imageBackBtn.setOnClickListener(view -> onBackPressed());
    }

    private void loadCurrentInfo()
    {
        //adding image to the profile view
        //decode base64 string to image
        byte[] imageBytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        binding.imageProfileMain.setImageBitmap(decodedImage);
        binding.imageProfileMain.setImageBitmap(decodedImage);
    }

    //function used to make a call to the Toast method
    private void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void updateUserInformation()
    {
        loading(true);
        //creating an instance of the fireBase fireStore and getting the userID
        FirebaseFirestore fire_store = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                fire_store.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        //updating username
        documentReference.update(Constants.KEY_NAME, binding.inputName.getText().toString().trim())
                .addOnFailureListener(e -> showToast("Update failed"));
        //updating email address
        documentReference.update(Constants.KEY_EMAIL, binding.inputEmail.getText().toString().trim())
                .addOnFailureListener(e -> showToast("Update failed"));
        //updating password
        documentReference.update(Constants.KEY_PASSWORD, binding.inputNewPassword.getText().toString().trim())
                .addOnFailureListener(e -> showToast("Update failed"));
        //updating profile picture
        documentReference.update(Constants.KEY_IMAGE, encodedImage)
                .addOnFailureListener(e -> showToast("Update failed"));
        loading(false);
    }

    //controls the view for the progress bar
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.btnUpdate.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.btnUpdate.setVisibility(View.VISIBLE);
        }
    }

    //listens to see if a user clicks a button
    private void setOnPressedListener(){
        binding.btnUpdate.setOnClickListener(view -> {
            if (isValidUpdateDetails()){
                updateUserInformation();
            }
        });
        //call to the pickImage method fo the user to select their profile picture
        binding.imageProfileMain.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    //used to encode the profile image into a string to be stored in the database
    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    //allows the user to pick a profile image for their account
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfileMain.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    //input validation for the sign-up page
    private Boolean isValidUpdateDetails(){
        if(encodedImage == null){
            showToast("Select profile image");
            return false;
        } else if (Objects.equals(preferenceManager.getString(Constants.KEY_NAME), binding.inputName.getText().toString().trim())) {
            showToast("Check input field");
            return false;
        } else if (Objects.equals(preferenceManager.getString(Constants.KEY_EMAIL), binding.inputEmail.getText().toString().trim())) {
            showToast("Check email field");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Enter valid email");
            return false;
        } else if (Objects.equals(preferenceManager.getString(Constants.KEY_PASSWORD), binding.inputNewPassword.getText().toString().trim())) {
            showToast("Enter new password");
            return false;
        } else if (!Objects.equals(preferenceManager.getString(Constants.KEY_PASSWORD), binding.inputOldPassword.getText().toString().trim())) {
            showToast("Enter recent password");
            return false;
        } else if (binding.inputNewPassword.getText().toString().equals(binding.inputOldPassword.getText().toString())) {
            showToast("Change password");
            return false;
        } else {
            return true;
        }

    }
}