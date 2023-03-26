package com.stacit.stac.activities;

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

import com.google.firebase.firestore.FirebaseFirestore;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.PersonalInformationEditBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

public class personalInformationActivity extends AppCompatActivity
{
    private PersonalInformationEditBinding binding;
    //creates an instance of the Preference Manager class
    private PreferenceManager preferenceManager;
    //holds the encoded value for the user's profile image
    private String encodedImage;
    //default method that's called on creation of the activity
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        preferenceManager = new PreferenceManager(getApplicationContext());
        super.onCreate(savedInstanceState);
        binding = PersonalInformationEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        buttonPressedListeners();
        loadCurrentInfo();
    }
    //onClickListeners function
    private void buttonPressedListeners()
    {
        binding.imageBackBtn.setOnClickListener(view -> onBackPressed());
        binding.btnUpdate.setOnClickListener(view -> {
            if (isValidSignUpDetails()){
                updateChanges();
            }
        });
        //call to the pickImage method for the user to select their profile picture
        binding.layoutImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }
    private void loadCurrentInfo()
    {
        binding.textAppStac.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.inputName.setHint(preferenceManager.getString(Constants.KEY_NAME));
        binding.inputEmail.setHint(preferenceManager.getString(Constants.KEY_EMAIL));
        binding.inputPassword.setHint("**********");
        binding.inputConfirmPassword.setHint("**********");
        //adding image to the profile view
        //decode base64 string to image
        byte[] imageBytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        binding.imageProfile.setImageBitmap(decodedImage);
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
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    //input validation for the sign-up page
    private Boolean isValidSignUpDetails(){
        if(encodedImage == null && preferenceManager.getString(Constants.KEY_IMAGE) == null)
        {
            return false;
        } else if (Objects.equals(preferenceManager.getString(Constants.KEY_NAME), binding.inputName.getText().toString().trim()))
        {
            showToast("Enter new name");
            return false;
        } else if (Objects.equals(preferenceManager.getString(Constants.KEY_EMAIL), binding.inputEmail.getText().toString().trim()))
        {
            showToast("Enter new email");
            return false;
        } else if ( !Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches() || Objects.equals(preferenceManager.getString(Constants.KEY_EMAIL), binding.inputEmail.getText().toString().trim()))
        {
            showToast("Enter valid email");
            return false;
        } else if (Objects.equals(preferenceManager.getString(Constants.KEY_PASSWORD), binding.inputPassword.getText().toString().trim()))
        {
            showToast("Enter old password");
            return false;
        } else if (Objects.equals(preferenceManager.getString(Constants.KEY_PASSWORD), binding.inputConfirmPassword.getText().toString().trim())) {
            showToast("Enter new password");
            return false;
        }
        else
        {
            return true;
        }
    }
    //update changes
    private void updateChanges()
    {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        if (!binding.inputName.getText().toString().trim().isEmpty() || !binding.inputName.getText().toString().trim().equals(preferenceManager.getString(Constants.KEY_NAME)))
        {
            data.put(Constants.KEY_NAME, binding.inputName.getText().toString());
        }
        if (!binding.inputEmail.getText().toString().trim().isEmpty() || !binding.inputEmail.getText().toString().trim().equals(preferenceManager.getString(Constants.KEY_EMAIL)))
        {
            data.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
        }
        if (!binding.inputPassword.getText().toString().trim().isEmpty() || !binding.inputPassword.getText().toString().trim().equals(preferenceManager.getString(Constants.KEY_PASSWORD)))
        {
            data.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());
        }
        if (encodedImage == null && preferenceManager.getString(Constants.KEY_IMAGE) != null)
        {
            encodedImage = preferenceManager.getString(Constants.KEY_IMAGE);
            data.put(Constants.KEY_IMAGE, encodedImage);
        }

        db.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update(data)
                        .addOnCompleteListener(task -> showToast("Information Updated")).addOnFailureListener(e -> showToast("Update Failed"));
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
    //function used to make a call to the Toast method
    private void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
