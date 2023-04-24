package com.stacit.stac.activities.Control;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.stacit.stac.R;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivityOcractivityBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class OCRActivity extends AppCompatActivity
{
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_SELECT_GALLERY_CODE = 1000;
    private static final int IMAGE_TAKE_CAMERA_CODE = 1001;
    private CameraSource cameraSource;
    private static final int PERMISSION = 100;
    String[] cameraPermission;
    String[] storagePermission;

    private PreferenceManager preferenceManager;

    Uri imageUri;

    private ActivityOcractivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        preferenceManager = new PreferenceManager(getApplicationContext());

        super.onCreate(savedInstanceState);
        binding = ActivityOcractivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //camera permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //camera intent onClick Listeners
        binding.aiTranslate.setOnClickListener(view -> {
            binding.surfaceViewCard.setVisibility(View.INVISIBLE);
            binding.imagePreviewCard.setVisibility(View.VISIBLE);
            binding.resultLiveEt.setVisibility(View.INVISIBLE);
            binding.resultEt.setVisibility(View.VISIBLE);
            setOnClickState(view.findViewById(R.id.aiTranslate));
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

        //gallery intent onClick Listener
        binding.liveFeed.setOnClickListener(view -> {
            binding.surfaceViewCard.setVisibility(View.INVISIBLE);
            binding.imagePreviewCard.setVisibility(View.VISIBLE);
            binding.resultLiveEt.setVisibility(View.INVISIBLE);
            binding.resultEt.setVisibility(View.VISIBLE);
            setOnClickState(view.findViewById(R.id.liveFeed));
            //gallery option clicked
            if (!checkedStoragePermission())
            {
                //storage permission not allowed, request it
                requestStoragePermission();
            }
            else
            {
                //permission allowed, select picture
                useGallery();
            }
        });

        //live recognition onClick Listener
        binding.imageBackBtn.setOnClickListener(view -> {
            binding.surfaceViewCard.setVisibility(View.VISIBLE);
            binding.imagePreviewCard.setVisibility(View.INVISIBLE);
            binding.resultLiveEt.setVisibility(View.VISIBLE);
            binding.resultEt.setVisibility(View.GONE);
            setOnClickState(view.findViewById(R.id.imageBackBtn));
            startCameraSource();
        });
    }

    private void useGallery()
    {
        //function to select image from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        //set intent type to image
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_SELECT_GALLERY_CODE);
    }


    private void setOnClickState(View view)
    {
        switch (view.getTag().toString())
        {
            case "gallery":
                binding.liveFeed.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.icon_red));
                binding.copyToChat.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.aiTranslate.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.speakText.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.imageBackBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                break;
            case "chat":
                binding.copyToChat.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.icon_red));
                binding.liveFeed.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.aiTranslate.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.speakText.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.imageBackBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                break;
            case "camera":
                binding.aiTranslate.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.icon_red));
                binding.liveFeed.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.copyToChat.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.speakText.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.imageBackBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                break;
            case "translate":
                binding.speakText.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.icon_red));
                binding.liveFeed.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.copyToChat.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.aiTranslate.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.imageBackBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                break;
            case "liveTranslate":
                binding.imageBackBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.icon_red));
                binding.liveFeed.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.copyToChat.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.aiTranslate.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.speakText.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                break;
        }
    }

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

    private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkedStoragePermission()
    {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission()
    {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkedCameraPermission()
    {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean resultExtended = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && resultExtended;
    }

    //handle permission result
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        useCamera();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        useGallery();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    //handle image result
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == IMAGE_SELECT_GALLERY_CODE)
            {
                //get image from gallery and crop it
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON) //enable image guidelines
                        .start(this);
            }
            else if (requestCode == IMAGE_TAKE_CAMERA_CODE)
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
                //set image to image view
                binding.imageIVPreview.setImageURI(resultUri);

                //get drawable bitmap for text recognition
                BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.imageIVPreview.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational())
                {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();

                    //get text from textBlock into stringBuilder
                    for (int i =0; i<items.size(); i++)
                    {
                        TextBlock myItem = items.valueAt(i);
                        stringBuilder.append(myItem.getValue());
                        stringBuilder.append("\n");
                    }
                    //set text to edit text view
                    binding.resultEt.setText(stringBuilder.toString());
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                //if there's any error, use the toast method to show it
                Exception error = result.getError();
                Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void startCameraSource()
    {
        final TextRecognizer recognizer =  new TextRecognizer.Builder(getApplicationContext()).build();

        if (!recognizer.isOperational())
        {
            Log.w("Tag", "Dependencies couldn't load");
        }
        else
        {
            cameraSource = new CameraSource.Builder(getApplicationContext(), recognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK).setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true).setRequestedFps(2.0f).build();
            binding.cameraView.getHolder().addCallback(new SurfaceHolder.Callback()
            {
                @Override
                public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder)
                {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(OCRActivity.this, new String[]{Manifest.permission.CAMERA},PERMISSION);
                        return;
                    }
                    try {
                        cameraSource.start(binding.cameraView.getHolder());
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2)
                {
                    //Release source for cameraSource

                }

                @Override
                public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder)
                {
                    cameraSource.stop();
                }
            });
            recognizer.setProcessor(new Detector.Processor<TextBlock>()
            {
                @Override
                public void release()
                {
                    //detect text from camera feed
                }

                @Override
                public void receiveDetections(@NonNull Detector.Detections<TextBlock> detections)
                {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0)
                    {
                        binding.resultLiveEt.post(() -> {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i =0; i< items.size(); i++)
                            {
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                            }
                            binding.resultLiveEt.setText(stringBuilder.toString());
                        });
                    }
                }
            });
        }
    }
}