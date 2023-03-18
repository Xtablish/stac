package com.stacit.stac.activities.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stacit.stac.activities.listeners.UserListener;
import com.stacit.stac.activities.models.User;
import com.stacit.stac.databinding.UserItemContainerBinding;

import java.util.List;

//this class connects the User model class to the view
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>
{
    //generated constructor for the UsersAdapter list
    public UsersAdapter(List<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    //a call to the User list constructor
    private final List<User> users;

    //an instantiation of the UserListener class
    private final UserListener userListener;

    //creates a the view holder on the parent layout/view
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        UserItemContainerBinding itemContainerBinding = UserItemContainerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerBinding);
    }

    //puts the user data to a set position in the parent view
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position)
    {
        holder.setUserData(users.get(position));
    }

    //returns number of users
    @Override
    public int getItemCount()
    {
        return users.size();
    }

    //this sub class is an extension of the built in RecycleView class
    class UserViewHolder extends RecyclerView.ViewHolder
    {
        //creates an instance of the local UserItemContainer layout binding class
        UserItemContainerBinding binding;

        //this method binds the class to the view when called
        UserViewHolder(UserItemContainerBinding userItemContainerBinding)
        {
            super(userItemContainerBinding.getRoot());
            binding = userItemContainerBinding;
        }

        //this method is used to assign the User model class to their layout IDs
        void setUserData(User user)
        {
            binding.textName.setText(user.name);
            binding.textEmail.setText(user.email);
            binding.imageProfile.setImageBitmap(getUserImage(user.image));
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
        }
    }

    //this function uses Base64 algorithm to decode the image string from the database
    private Bitmap getUserImage(String encodedImage)
    {
        byte [] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
