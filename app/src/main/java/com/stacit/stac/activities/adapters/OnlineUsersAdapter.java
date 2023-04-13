package com.stacit.stac.activities.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stacit.stac.activities.models.ChatMessage;
import com.stacit.stac.databinding.ItemOnlineContainerBinding;

import java.util.List;

public class OnlineUsersAdapter extends RecyclerView.Adapter<OnlineUsersAdapter.ConversionViewHolder>
{
    private final List<ChatMessage> chatMessages;

    public OnlineUsersAdapter(List<ChatMessage> chatMessages)
    {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public OnlineUsersAdapter.ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
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
            binding.imageOnlineStatus.setVisibility(View.VISIBLE);
            binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.conversionImage));
            binding.textUsername.setText(chatMessage.conversionName);
        }
    }

    private Bitmap getConversionImage(String encodedImage)
    {
        byte [] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
