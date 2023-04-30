package com.stacit.stac.activities.settings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.stacit.stac.R;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySettingsProfileEditPhotoBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SettingsProfileEditPhoto extends AppCompatActivity
{

    private ActivitySettingsProfileEditPhotoBinding binding;
    private PreferenceManager preferenceManager;
    private String imageHolder = null;
    private String encodedImage;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int IMAGE_TAKE_CAMERA_CODE = 1001;
    String[] cameraPermission;
    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsProfileEditPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        imageHolder = preferenceManager.getString(Constants.KEY_PROFILE_HOLDER);
        setProfileImage(preferenceManager.getString(Constants.KEY_IMAGE));

        if (encodedImage == null)
        {
            binding.imageCancelBtn.setOnClickListener(view -> onBackPressed());
        }

        //camera permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //camera intent onClick Listeners
        binding.imageCameraBtn.setOnClickListener(view -> {
            //camera option clicked
            if (!checkedCameraPermission())
            {
                //camera permission not allowed, request it
                requestCameraPermission();
            }
            else
            {
                //permission allowed, take picture
                useCamera();
            }
        });

        SettingsProfileEditProfileOnClickListener();
        imageGallerySelect();
        cameraSelect();
        avatarSelect();
    }

    private void SettingsProfileEditProfileOnClickListener()
    {
        binding.imageTextBtn.setOnClickListener(view -> textImageProfile());
    }

    private void textImageProfile()
    {
        binding.imageProfile.setImageResource(R.color.send_message_bg);
    }

    private void setProfileImage(String profileImage)
    {
        byte[] imageBytes = Base64.decode(profileImage, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        binding.imageProfile.setImageBitmap(decodedImage);
    }

    private void imageGallerySelect()
    {
        binding.imageGalleryBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void cameraSelect()
    {

    }

    private void avatarSelect()
    {
        binding.profileAvatar1.setOnClickListener(view -> {
            encodedImage = encodeImage( BitmapFactory.decodeResource(getResources(),R.drawable.profile_one));
            showSelection(binding.profileAvatar1);
            makeUpdate();
        });
        binding.profileAvatar2.setOnClickListener(view -> {
            encodedImage = encodeImage( BitmapFactory.decodeResource(getResources(),R.drawable.profile_two));
            showSelection(binding.profileAvatar2);
            makeUpdate();
        });
        binding.profileAvatar3.setOnClickListener(view -> {
            encodedImage = encodeImage( BitmapFactory.decodeResource(getResources(),R.drawable.profile_three));
            showSelection(binding.profileAvatar3);
            makeUpdate();
        });
        binding.profileAvatar4.setOnClickListener(view -> {
            encodedImage = encodeImage( BitmapFactory.decodeResource(getResources(),R.drawable.profile_four));
            showSelection(binding.profileAvatar4);
            makeUpdate();
        });
        binding.profileAvatar5.setOnClickListener(view -> {
            encodedImage = encodeImage( BitmapFactory.decodeResource(getResources(),R.drawable.profile_five));
            showSelection(binding.profileAvatar5);
            makeUpdate();
        });
        binding.profileAvatar6.setOnClickListener(view -> {
            encodedImage = encodeImage( BitmapFactory.decodeResource(getResources(),R.drawable.profile_six));
            showSelection(binding.profileAvatar6);
            makeUpdate();
        });
        binding.profileAvatar7.setOnClickListener(view -> {
            encodedImage = encodeImage( BitmapFactory.decodeResource(getResources(),R.drawable.profile_seven));
            showSelection(binding.profileAvatar7);
            makeUpdate();
        });
        binding.profileAvatar8.setOnClickListener(view -> {
            encodedImage = encodeImage( BitmapFactory.decodeResource(getResources(),R.drawable.profile_eight));
            showSelection(binding.profileAvatar8);
            makeUpdate();
        });
        binding.profileAvatar9.setOnClickListener(view -> {
            encodedImage = encodeImage( BitmapFactory.decodeResource(getResources(),R.drawable.profile_nine));
            showSelection(binding.profileAvatar9);
            makeUpdate();
        });
        binding.profileAvatar10.setOnClickListener(view -> {
            encodedImage = encodeImage( BitmapFactory.decodeResource(getResources(),R.drawable.profile_ten));
            showSelection(binding.profileAvatar10);
            makeUpdate();
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void showSelection(View view)
    {
        switch (view.getId())
        {
            case R.id.profileAvatar1:
                binding.profileAvatar1Select.setVisibility(View.VISIBLE);
                binding.profileAvatar2Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar3Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar4Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar5Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar6Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar7Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar8Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar9Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.profileAvatar2:
                binding.profileAvatar1Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar2Select.setVisibility(View.VISIBLE);
                binding.profileAvatar3Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar4Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar5Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar6Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar7Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar8Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar9Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.profileAvatar3:
                binding.profileAvatar1Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar2Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar3Select.setVisibility(View.VISIBLE);
                binding.profileAvatar4Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar5Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar6Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar7Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar8Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar9Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.profileAvatar4:
                binding.profileAvatar1Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar2Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar3Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar4Select.setVisibility(View.VISIBLE);
                binding.profileAvatar5Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar6Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar7Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar8Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar9Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.profileAvatar5:
                binding.profileAvatar1Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar2Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar3Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar4Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar5Select.setVisibility(View.VISIBLE);
                binding.profileAvatar6Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar7Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar8Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar9Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.profileAvatar6:
                binding.profileAvatar1Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar2Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar3Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar4Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar5Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar6Select.setVisibility(View.VISIBLE);
                binding.profileAvatar7Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar8Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar9Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.profileAvatar7:
                binding.profileAvatar1Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar2Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar3Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar4Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar5Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar6Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar7Select.setVisibility(View.VISIBLE);
                binding.profileAvatar8Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar9Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.profileAvatar8:
                binding.profileAvatar1Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar2Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar3Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar4Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar5Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar6Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar7Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar8Select.setVisibility(View.VISIBLE);
                binding.profileAvatar9Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.profileAvatar9:
                binding.profileAvatar1Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar2Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar3Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar4Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar5Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar6Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar7Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar8Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar9Select.setVisibility(View.VISIBLE);
                binding.profileAvatar10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.profileAvatar10:
                binding.profileAvatar1Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar2Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar3Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar4Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar5Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar6Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar7Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar8Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar9Select.setVisibility(View.INVISIBLE);
                binding.profileAvatar10Select.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void updateProfileImage()
    {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(Constants.KEY_USER_ID)
                .update(Constants.KEY_IMAGE, encodedImage);
    }

    private void makeUpdate()
    {
        if (encodedImage != null)
        {
            preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
            setProfileImage(preferenceManager.getString(Constants.KEY_IMAGE));
            binding.imageChangeBtn.setVisibility(View.VISIBLE);
            binding.imageCancelBtn.setOnClickListener(view -> {
                if (imageHolder != null)
                {
                    preferenceManager.putString(Constants.KEY_IMAGE, imageHolder);
                    binding.imageChangeBtn.setVisibility(View.INVISIBLE);
                }
                onBackPressed();
            });
            binding.imageChangeBtn.setOnClickListener(view -> {
                updateProfileImage();
                binding.imageChangeBtn.setVisibility(View.INVISIBLE);
            });
        }
        else
        {
            binding.imageChangeBtn.setVisibility(View.INVISIBLE);
            binding.imageCancelBtn.setOnClickListener(view -> {
                if (imageHolder != null)
                {
                    preferenceManager.putString(Constants.KEY_IMAGE, imageHolder);
                    binding.imageChangeBtn.setVisibility(View.INVISIBLE);
                }
                onBackPressed();
            });
        }
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            encodedImage = encodeImage(bitmap);
                            makeUpdate();
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    //this function  is used to launch the camera activity, so the user can take a picture for the text recognition
    private void useCamera()
    {
        //function to take image using the camera, the image is also save to storage
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "scan_doc_image"); //set the tile of the image
        values.put(MediaStore.Images.Media.DESCRIPTION, "Text recognition image"); //set the image description
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_TAKE_CAMERA_CODE);
    }

    //this function is used to request camera permission from the user
    private void requestCameraPermission()
    {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    //this function is used to check if the app already has permission to use the camera
    private boolean checkedCameraPermission()
    {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean resultExtended = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && resultExtended;
    }

    //handle permission result by overriding the onRequestPermissionResult function
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted && writeStorageAccepted) {
                    useCamera();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //handle image result by overriding the onActivityResult function
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == IMAGE_TAKE_CAMERA_CODE)
            {
                //take image using camera and crop it
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON) //enable image guidelines
                        .start(this);
            }
        }
        //get cropped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                Uri resultUri = result.getUri(); //get image uri
                encodedImage = resultUri.toString();
                makeUpdate();
                //set image to image view
                binding.imageProfile.setImageURI(resultUri);

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                //if there's any error, use the toast method to show it
                Exception error = result.getError();
                Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}