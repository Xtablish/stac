package com.stacit.stac.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.stacit.stac.activities.models.User;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.databinding.ConversationChatBinding;

public class conversationChatActivity extends AppCompatActivity
{

    private ConversationChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ConversationChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
        loadReceiverDetails();
    }

    private void loadReceiverDetails()
    {
        User receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textUsername.setText(receiverUser.name);
    }

    private void setListener()
    {
        binding.imageBackBtn.setOnClickListener(view -> onBackPressed());
    }
}