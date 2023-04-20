package com.stacit.stac.activities.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stacit.stac.activities.models.ChatMessage;
import com.stacit.stac.databinding.ItemContainerReceivedMessageBinding;
import com.stacit.stac.databinding.ItemContainerSentMessageBinding;

import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final Bitmap receiverProfileImage;
    private final String senderID;
    private final List<ChatMessage> chatMessages;
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVE = 2;

    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String senderID)
    {
        this.receiverProfileImage = receiverProfileImage;
        this.senderID = senderID;
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if (viewType == VIEW_TYPE_SENT)
        {
            return new SendMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }else
        {
            return new ReceivedMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        if (getItemViewType(position) == VIEW_TYPE_SENT)
        {
            ((SendMessageViewHolder) holder).setUserData(chatMessages.get(position));
        }else
        {
            ((ReceivedMessageViewHolder) holder).setUserData(chatMessages.get(position), receiverProfileImage);
        }
    }

    @Override
    public int getItemCount()
    {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (chatMessages.get(position).senderID.equals(senderID))
        {
            return VIEW_TYPE_SENT;
        }else
        {
            return VIEW_TYPE_RECEIVE;
        }
    }

    static class SendMessageViewHolder extends RecyclerView.ViewHolder
    {
        private final ItemContainerSentMessageBinding binding;

        SendMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding)
        {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setUserData(ChatMessage chatMessage)
        {
            binding.textDate.setText(chatMessage.date);
            binding.textDate.setVisibility(View.VISIBLE);

            binding.textTime.setText(chatMessage.time);
            binding.textMessage.setText(chatMessage.message);
        }
    }

    static  class ReceivedMessageViewHolder extends RecyclerView.ViewHolder
    {
        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding)
        {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setUserData(ChatMessage chatMessage, Bitmap receivedProfileImage)
        {
            binding.textMessage.setText(chatMessage.message);
            binding.textDate.setText(chatMessage.date);
            binding.textTime.setText(chatMessage.time);
            binding.imageProfile.setImageBitmap(receivedProfileImage);

            binding.textDate.setText(chatMessage.date);
            binding.textDate.setVisibility(View.VISIBLE);
        }
    }
}
