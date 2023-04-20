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
import com.stacit.stac.databinding.RecentItemContainerBinding;

import java.util.List;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>
{
    private final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;

    public RecentConversationsAdapter(List<ChatMessage> chatMessages, ConversionListener conversionListener)
    {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ConversionViewHolder(
                RecentItemContainerBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecentConversationsAdapter.ConversionViewHolder holder, int position)
    {
        holder.SetData(chatMessages.get(position));

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder
    {
        RecentItemContainerBinding binding;

        ConversionViewHolder(RecentItemContainerBinding recentItemContainerBinding)
        {
            super(recentItemContainerBinding.getRoot());
            //binding the view to the recycler
            binding = recentItemContainerBinding;
        }

        void SetData(ChatMessage chatMessage)
        {
            //if the last sender was the user, concatenate the message with you at the start
            if (!chatMessage.lastMessageSender.equals(chatMessage.sender))
            {
                binding.textRecentMessage.setText(chatMessage.message);
            }
            else
            {
                String textHolder = "You: "+chatMessage.message;
                binding.textRecentMessage.setText(textHolder);
            }
            binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.conversionImage));
            binding.textName.setText(chatMessage.conversionName);
            binding.getRoot().setOnClickListener(view -> {
                User user = new User();
                user.id = chatMessage.conversionId;
                user.name = chatMessage.conversionName;
                user.image = chatMessage.conversionImage;
                conversionListener.onConversionClicked(user);
            });

            if (chatMessage.typing != null && chatMessage.typing.equals(true))
            {
                //show the typing status and hide the last message
                binding.typingStatus.setVisibility(View.VISIBLE);
                binding.textRecentMessage.setVisibility(View.INVISIBLE);
            }
            else
            {
                //hide the typingStatus and show the last message
                binding.typingStatus.setVisibility(View.INVISIBLE);
                binding.textRecentMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    private Bitmap getConversionImage(String encodedImage)
    {
        //converts the encoded string image to a Bitmap image to be displayed
        byte [] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
