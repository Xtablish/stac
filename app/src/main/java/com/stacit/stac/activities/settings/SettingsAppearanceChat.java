package com.stacit.stac.activities.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.stacit.stac.R;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivitySettingsAppearanceChatBinding;

public class SettingsAppearanceChat extends AppCompatActivity {
    private ActivitySettingsAppearanceChatBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsAppearanceChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        showChatBubbleColor();
        showWallpaper();

        binding.imageCancelBtn.setOnClickListener(view -> onBackPressed());
        binding.settingsAppearanceSelectChatColor.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SettingsAppearanceSelectChatColor.class)));
        binding.settingsAppearanceSelectChatWallpaper.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SettingsAppearanceSelectWallpaper.class)));
        binding.textAppearanceResetChatWallpaper.setOnClickListener(view -> resetWallpaper());
        binding.textAppearanceResetChatBubbleColor.setOnClickListener(view -> resetChatColor());
    }

    private void resetChatColor()
    {
        int chatBubbleColor = ContextCompat.getColor(getApplicationContext(), R.color.chat_bubble_default);
        preferenceManager.putString(Constants.KEY_CHAT_COLOR_SELECTION, "chat_default_color");
        preferenceManager.putString(Constants.KEY_CHAT_BUBBLE_COLOR, Integer.toString(chatBubbleColor));

        showChatBubbleColor();
    }

    private void resetWallpaper()
    {
        int backgroundWallpaper = ContextCompat.getColor(getApplicationContext(), R.color.chat_color_one);
        preferenceManager.putString(Constants.KEY_WALLPAPER_SELECTION, "chatColor1");
        preferenceManager.putString(Constants.KEY_WALLPAPER, Integer.toString(backgroundWallpaper));

        showWallpaper();
    }

    private void showChatBubbleColor()
    {
       if (preferenceManager.getString(Constants.KEY_CHAT_BUBBLE_COLOR) != null)
       {
           Drawable chatBubbleDrawable = binding.settingsChatColor.getBackground();
           chatBubbleDrawable = DrawableCompat.wrap(chatBubbleDrawable);
           //the color is a direct color int and not a color resource
           DrawableCompat.setTint(chatBubbleDrawable, Integer.parseInt(preferenceManager.getString(Constants.KEY_CHAT_BUBBLE_COLOR)));
           binding.settingsChatColor.setBackground(chatBubbleDrawable);

           Drawable chatBubblePreviewDrawable = binding.textSentMessage.getBackground();
           chatBubblePreviewDrawable = DrawableCompat.wrap(chatBubblePreviewDrawable);
           //the color is a direct color int and not a color resource
           DrawableCompat.setTint(chatBubblePreviewDrawable, Integer.parseInt(preferenceManager.getString(Constants.KEY_CHAT_BUBBLE_COLOR)));
           binding.textSentMessage.setBackground(chatBubblePreviewDrawable);

       }
    }

    private void showWallpaper()
    {
        if (preferenceManager.getString(Constants.KEY_WALLPAPER_SELECTION) != null)
        {
            Drawable previewDrawable = binding.viewBackground.getBackground();
            previewDrawable = DrawableCompat.wrap(previewDrawable);
            //the color is a direct color int and not a color resource
            DrawableCompat.setTint(previewDrawable, Integer.parseInt(preferenceManager.getString(Constants.KEY_WALLPAPER)));
            binding.viewBackground.setBackground(previewDrawable);

        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        showWallpaper();
        showChatBubbleColor();
    }
}