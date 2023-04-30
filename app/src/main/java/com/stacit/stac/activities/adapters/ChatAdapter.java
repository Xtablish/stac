package com.stacit.stac.activities.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.stacit.stac.activities.models.ChatMessage;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ChatUserDetailsViewBinding;
import com.stacit.stac.databinding.ItemContainerReceivedMessageBinding;
import com.stacit.stac.databinding.ItemContainerSentMessageBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final Bitmap receiverProfileImage;
    private final String senderID;
    public final String receiverName;
    private final List<ChatMessage> chatMessages;
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVE = 2;
    private static final int VIEW_TYPE_DEFAULT = 0;
    private final PreferenceManager preferenceManager;

    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String senderID, String receiverName, Context context)
    {
        this.receiverProfileImage = receiverProfileImage;
        this.senderID = senderID;
        this.chatMessages = chatMessages;
        this.receiverName = receiverName;
        this.preferenceManager = new PreferenceManager(context.getApplicationContext());
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
        }else if (viewType == VIEW_TYPE_RECEIVE)
        {
            return new ReceivedMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
        else
        {
            return new ReceiverDetailViewHolder(
                    ChatUserDetailsViewBinding.inflate(
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
            ((SendMessageViewHolder) holder).setChatBubbleColor(preferenceManager);
            ((SendMessageViewHolder) holder).setUserData(chatMessages.get(position));
        }
        else if (getItemViewType(position) == VIEW_TYPE_RECEIVE)
        {
            ((ReceivedMessageViewHolder) holder).setUserData(chatMessages.get(position), receiverProfileImage);
        }
        else
        {
            ((ReceiverDetailViewHolder) holder).setUserData(receiverName,receiverProfileImage);
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
        if (position == 0)
        {
            return VIEW_TYPE_DEFAULT;
        }
        else if (chatMessages.get(position).senderID.equals(senderID))
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

        void setChatBubbleColor(PreferenceManager preferenceManager)
        {
            if (preferenceManager.getString(Constants.KEY_CHAT_BUBBLE_COLOR) != null)
            {
                //chat bubble color update
                Drawable previewDrawable = binding.textMessage.getBackground();
                previewDrawable = DrawableCompat.wrap(previewDrawable);
                //the color is a direct color int and not a color resource
                DrawableCompat.setTint(previewDrawable, Integer.parseInt(preferenceManager.getString(Constants.KEY_CHAT_BUBBLE_COLOR)));
                binding.textMessage.setBackground(previewDrawable);
            }
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

    static  class ReceiverDetailViewHolder extends RecyclerView.ViewHolder
    {
        private final ChatUserDetailsViewBinding binding;

        ReceiverDetailViewHolder(ChatUserDetailsViewBinding chatUserDetailsViewBinding)
        {
            super(chatUserDetailsViewBinding.getRoot());
            binding = chatUserDetailsViewBinding;
        }

        void setUserData(String receiverName, Bitmap receivedProfileImage)
        {
            binding.textUsername.setText(receiverName);
            binding.imageProfile.setImageBitmap(receivedProfileImage);
        }
    }
}
