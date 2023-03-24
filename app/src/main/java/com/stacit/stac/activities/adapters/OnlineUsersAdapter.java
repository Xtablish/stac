package com.stacit.stac.activities.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stacit.stac.activities.listeners.ConversionListener;
import com.stacit.stac.activities.models.ChatMessage;
import com.stacit.stac.activities.models.User;
import com.stacit.stac.databinding.ItemOnlineContainerBinding;

import java.util.List;

public class OnlineUsersAdapter extends RecyclerView.Adapter<OnlineUsersAdapter.ConversionViewHolder>
{
    private final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;

    public OnlineUsersAdapter(List<ChatMessage> chatMessages, ConversionListener conversionListener)
    {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new OnlineUsersAdapter.ConversionViewHolder(
                ItemOnlineContainerBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OnlineUsersAdapter.ConversionViewHolder holder, int position)
    {
        holder.SetData(chatMessages.get(position));

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder
    {
        ItemOnlineContainerBinding binding;

        ConversionViewHolder(ItemOnlineContainerBinding itemOnlineContainerBinding)
        {
            super(itemOnlineContainerBinding.getRoot());
            binding = itemOnlineContainerBinding;
        }

        void SetData(ChatMessage chatMessage)
        {
            binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.conversionImage));
            binding.imageOnlineStatus.setVisibility(View.VISIBLE);
            binding.getRoot().setOnClickListener(view -> {
                User user = new User();
                user.id = chatMessage.conversionId;
                user.name = chatMessage.conversionName;
                user.image = chatMessage.conversionImage;
                conversionListener.onConversionClicked(user);
            });
        }
    }

    private Bitmap getConversionImage(String encodedImage)
    {
        byte [] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
