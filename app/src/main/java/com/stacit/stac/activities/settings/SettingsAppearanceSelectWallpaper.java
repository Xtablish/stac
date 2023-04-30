package com.stacit.stac.activities.settings;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.stacit.stac.R;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySettingsAppearanceSelectWallpaperBinding;

public class SettingsAppearanceSelectWallpaper extends AppCompatActivity {
    private ActivitySettingsAppearanceSelectWallpaperBinding binding;
    private PreferenceManager preferenceManager;
    private int backgroundColor = 0;
    private String wallpaperSelection = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsAppearanceSelectWallpaperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        binding.imageCancelBtn.setOnClickListener(view -> onBackPressed());

        setCurrent();
        wallpaperSelect();
    }

    private void wallpaperSelect()
    {
        binding.chatColor1.setOnClickListener(view -> {
            backgroundColor = ContextCompat.getColor(getApplicationContext(), R.color.chat_color_one);
            wallpaperSelection = "chatColor1";
            showSelection(binding.chatColor1);
            makeUpdate();
        });
        binding.chatColor2.setOnClickListener(view -> {
            backgroundColor = ContextCompat.getColor(getApplicationContext(), R.color.chat_color_two);
            wallpaperSelection = "chatColor2";
            showSelection(binding.chatColor2);
            makeUpdate();
        });
        binding.chatColor3.setOnClickListener(view -> {
            backgroundColor = ContextCompat.getColor(getApplicationContext(), R.color.chat_color_three);
            wallpaperSelection = "chatColor3";
            showSelection(binding.chatColor3);
            makeUpdate();
        });
        binding.chatColor4.setOnClickListener(view -> {
            backgroundColor = ContextCompat.getColor(getApplicationContext(), R.color.chat_color_four);
            wallpaperSelection = "chatColor4";
            showSelection(binding.chatColor4);
            makeUpdate();
        });
        binding.chatColor5.setOnClickListener(view -> {
            backgroundColor = ContextCompat.getColor(getApplicationContext(), R.color.chat_color_five);
            wallpaperSelection = "chatColor5";
            showSelection(binding.chatColor5);
            makeUpdate();
        });
        binding.chatColor6.setOnClickListener(view -> {
            backgroundColor = ContextCompat.getColor(getApplicationContext(), R.color.chat_color_six);
            wallpaperSelection = "chatColor6";
            showSelection(binding.chatColor6);
            makeUpdate();
        });
        binding.chatColor7.setOnClickListener(view -> {
            backgroundColor = ContextCompat.getColor(getApplicationContext(), R.color.chat_color_seven);
            wallpaperSelection = "chatColor7";
            showSelection(binding.chatColor7);
            makeUpdate();
        });
        binding.chatColor8.setOnClickListener(view -> {
            backgroundColor = ContextCompat.getColor(getApplicationContext(), R.color.chat_color_eight);
            wallpaperSelection = "chatColor8";
            showSelection(binding.chatColor8);
            makeUpdate();
        });
        binding.chatColor9.setOnClickListener(view -> {
            backgroundColor = ContextCompat.getColor(getApplicationContext(), R.color.chat_color_nine);
            wallpaperSelection = "chatColor9";
            showSelection(binding.chatColor9);
            makeUpdate();
        });
        binding.chatColor10.setOnClickListener(view -> {
            backgroundColor = ContextCompat.getColor(getApplicationContext(), R.color.chat_color_ten);
            wallpaperSelection = "chatColor10";
            showSelection(binding.chatColor10);
            makeUpdate();
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void showSelection(View view)
    {
        switch (view.getId())
        {
            case R.id.chatColor1:
                binding.chatColor1Select.setVisibility(View.VISIBLE);
                binding.chatColor2Select.setVisibility(View.INVISIBLE);
                binding.chatColor3Select.setVisibility(View.INVISIBLE);
                binding.chatColor4Select.setVisibility(View.INVISIBLE);
                binding.chatColor5Select.setVisibility(View.INVISIBLE);
                binding.chatColor6Select.setVisibility(View.INVISIBLE);
                binding.chatColor7Select.setVisibility(View.INVISIBLE);
                binding.chatColor8Select.setVisibility(View.INVISIBLE);
                binding.chatColor9Select.setVisibility(View.INVISIBLE);
                binding.chatColor10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.chatColor2:
                binding.chatColor1Select.setVisibility(View.INVISIBLE);
                binding.chatColor2Select.setVisibility(View.VISIBLE);
                binding.chatColor3Select.setVisibility(View.INVISIBLE);
                binding.chatColor4Select.setVisibility(View.INVISIBLE);
                binding.chatColor5Select.setVisibility(View.INVISIBLE);
                binding.chatColor6Select.setVisibility(View.INVISIBLE);
                binding.chatColor7Select.setVisibility(View.INVISIBLE);
                binding.chatColor8Select.setVisibility(View.INVISIBLE);
                binding.chatColor9Select.setVisibility(View.INVISIBLE);
                binding.chatColor10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.chatColor3:
                binding.chatColor1Select.setVisibility(View.INVISIBLE);
                binding.chatColor2Select.setVisibility(View.INVISIBLE);
                binding.chatColor3Select.setVisibility(View.VISIBLE);
                binding.chatColor4Select.setVisibility(View.INVISIBLE);
                binding.chatColor5Select.setVisibility(View.INVISIBLE);
                binding.chatColor6Select.setVisibility(View.INVISIBLE);
                binding.chatColor7Select.setVisibility(View.INVISIBLE);
                binding.chatColor8Select.setVisibility(View.INVISIBLE);
                binding.chatColor9Select.setVisibility(View.INVISIBLE);
                binding.chatColor10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.chatColor4:
                binding.chatColor1Select.setVisibility(View.INVISIBLE);
                binding.chatColor2Select.setVisibility(View.INVISIBLE);
                binding.chatColor3Select.setVisibility(View.INVISIBLE);
                binding.chatColor4Select.setVisibility(View.VISIBLE);
                binding.chatColor5Select.setVisibility(View.INVISIBLE);
                binding.chatColor6Select.setVisibility(View.INVISIBLE);
                binding.chatColor7Select.setVisibility(View.INVISIBLE);
                binding.chatColor8Select.setVisibility(View.INVISIBLE);
                binding.chatColor9Select.setVisibility(View.INVISIBLE);
                binding.chatColor10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.chatColor5:
                binding.chatColor1Select.setVisibility(View.INVISIBLE);
                binding.chatColor2Select.setVisibility(View.INVISIBLE);
                binding.chatColor3Select.setVisibility(View.INVISIBLE);
                binding.chatColor4Select.setVisibility(View.INVISIBLE);
                binding.chatColor5Select.setVisibility(View.VISIBLE);
                binding.chatColor6Select.setVisibility(View.INVISIBLE);
                binding.chatColor7Select.setVisibility(View.INVISIBLE);
                binding.chatColor8Select.setVisibility(View.INVISIBLE);
                binding.chatColor9Select.setVisibility(View.INVISIBLE);
                binding.chatColor10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.chatColor6:
                binding.chatColor1Select.setVisibility(View.INVISIBLE);
                binding.chatColor2Select.setVisibility(View.INVISIBLE);
                binding.chatColor3Select.setVisibility(View.INVISIBLE);
                binding.chatColor4Select.setVisibility(View.INVISIBLE);
                binding.chatColor5Select.setVisibility(View.INVISIBLE);
                binding.chatColor6Select.setVisibility(View.VISIBLE);
                binding.chatColor7Select.setVisibility(View.INVISIBLE);
                binding.chatColor8Select.setVisibility(View.INVISIBLE);
                binding.chatColor9Select.setVisibility(View.INVISIBLE);
                binding.chatColor10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.chatColor7:
                binding.chatColor1Select.setVisibility(View.INVISIBLE);
                binding.chatColor2Select.setVisibility(View.INVISIBLE);
                binding.chatColor3Select.setVisibility(View.INVISIBLE);
                binding.chatColor4Select.setVisibility(View.INVISIBLE);
                binding.chatColor5Select.setVisibility(View.INVISIBLE);
                binding.chatColor6Select.setVisibility(View.INVISIBLE);
                binding.chatColor7Select.setVisibility(View.VISIBLE);
                binding.chatColor8Select.setVisibility(View.INVISIBLE);
                binding.chatColor9Select.setVisibility(View.INVISIBLE);
                binding.chatColor10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.chatColor8:
                binding.chatColor1Select.setVisibility(View.INVISIBLE);
                binding.chatColor2Select.setVisibility(View.INVISIBLE);
                binding.chatColor3Select.setVisibility(View.INVISIBLE);
                binding.chatColor4Select.setVisibility(View.INVISIBLE);
                binding.chatColor5Select.setVisibility(View.INVISIBLE);
                binding.chatColor6Select.setVisibility(View.INVISIBLE);
                binding.chatColor7Select.setVisibility(View.INVISIBLE);
                binding.chatColor8Select.setVisibility(View.VISIBLE);
                binding.chatColor9Select.setVisibility(View.INVISIBLE);
                binding.chatColor10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.chatColor9:
                binding.chatColor1Select.setVisibility(View.INVISIBLE);
                binding.chatColor2Select.setVisibility(View.INVISIBLE);
                binding.chatColor3Select.setVisibility(View.INVISIBLE);
                binding.chatColor4Select.setVisibility(View.INVISIBLE);
                binding.chatColor5Select.setVisibility(View.INVISIBLE);
                binding.chatColor6Select.setVisibility(View.INVISIBLE);
                binding.chatColor7Select.setVisibility(View.INVISIBLE);
                binding.chatColor8Select.setVisibility(View.INVISIBLE);
                binding.chatColor9Select.setVisibility(View.VISIBLE);
                binding.chatColor10Select.setVisibility(View.INVISIBLE);
                break;
            case R.id.chatColor10:
                binding.chatColor1Select.setVisibility(View.INVISIBLE);
                binding.chatColor2Select.setVisibility(View.INVISIBLE);
                binding.chatColor3Select.setVisibility(View.INVISIBLE);
                binding.chatColor4Select.setVisibility(View.INVISIBLE);
                binding.chatColor5Select.setVisibility(View.INVISIBLE);
                binding.chatColor6Select.setVisibility(View.INVISIBLE);
                binding.chatColor7Select.setVisibility(View.INVISIBLE);
                binding.chatColor8Select.setVisibility(View.INVISIBLE);
                binding.chatColor9Select.setVisibility(View.INVISIBLE);
                binding.chatColor10Select.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void makeUpdate()
    {
        Drawable previewDrawable = binding.viewBackground.getBackground();
        previewDrawable = DrawableCompat.wrap(previewDrawable);
        //the color is a direct color int and not a color resource
        DrawableCompat.setTint(previewDrawable, backgroundColor);
        binding.viewBackground.setBackground(previewDrawable);

        preferenceManager.putString(Constants.KEY_WALLPAPER, Integer.toString(backgroundColor));
        preferenceManager.putString(Constants.KEY_WALLPAPER_SELECTION, wallpaperSelection);
    }

    private void setCurrent()
    {
        if (preferenceManager.getString(Constants.KEY_WALLPAPER_SELECTION) != null)
        {
            Drawable previewDrawable = binding.viewBackground.getBackground();
            previewDrawable = DrawableCompat.wrap(previewDrawable);
            //the color is a direct color int and not a color resource
            DrawableCompat.setTint(previewDrawable, Integer.parseInt(preferenceManager.getString(Constants.KEY_WALLPAPER)));
            binding.viewBackground.setBackground(previewDrawable);

            showLastSelection(preferenceManager.getString(Constants.KEY_WALLPAPER_SELECTION));
        }
    }

    private void showLastSelection(String selection)
    {
        switch (selection)
        {
            case "chatColor1":
                binding.chatColor1Select.setVisibility(View.VISIBLE);
                break;
            case "chatColor2":
                binding.chatColor2Select.setVisibility(View.VISIBLE);
                break;
            case "chatColor3":
                binding.chatColor3Select.setVisibility(View.VISIBLE);
                break;
            case "chatColor4":
                binding.chatColor4Select.setVisibility(View.VISIBLE);
                break;
            case "chatColor5":
                binding.chatColor5Select.setVisibility(View.VISIBLE);
                break;
            case "chatColor6":
                binding.chatColor6Select.setVisibility(View.VISIBLE);
                break;
            case "chatColor7":
                binding.chatColor7Select.setVisibility(View.VISIBLE);
                break;
            case "chatColor8":
                binding.chatColor8Select.setVisibility(View.VISIBLE);
                break;
            case "chatColor9":
                binding.chatColor9Select.setVisibility(View.VISIBLE);
                break;
            case "chatColor10":
                binding.chatColor10Select.setVisibility(View.VISIBLE);
                break;
        }
    }

}