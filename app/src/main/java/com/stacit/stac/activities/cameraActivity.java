package com.stacit.stac.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.stacit.stac.databinding.ActivityCameraBinding;

import java.io.IOException;

public class cameraActivity extends AppCompatActivity
{
    private ActivityCameraBinding binding;
    private CameraSource cameraSource;
    private static final int PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //call to the startCameraSource function
        startCameraSource();
        setOnClickListeners();
    }

    private void setOnClickListeners()
    {
        binding.imageBackBtn.setOnClickListener(view -> onBackPressed());
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
                        ActivityCompat.requestPermissions(cameraActivity.this, new String[]{Manifest.permission.CAMERA},PERMISSION);
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
                        binding.textView.post(() -> {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i =0; i< items.size(); i++)
                            {
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                            }
                            binding.textView.setText(stringBuilder.toString());
                        });
                    }
                }
            });
        }
    }
}