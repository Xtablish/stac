package com.stacit.stac.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.stacit.stac.activities.adapters.UsersAdapter;
import com.stacit.stac.activities.listeners.UserListener;
import com.stacit.stac.activities.models.User;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ActivityUsersBinding;
import com.stacit.stac.databinding.ConversationChatBinding;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListener
{

    //create an instance of the local activity binding class
    private ActivityUsersBinding binding;

    //create an instance of the local preference manager class
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //initiate the preference manager
        preferenceManager = new PreferenceManager(getApplicationContext());
        //call the backBtnListener function to revert to previous view
        backBtnListener();
        //call the getUsers function to show the list of users
        getUsers();
    }

    private void backBtnListener()
    {
        binding.imageBackBtn.setOnClickListener(view -> onBackPressed());
    }
    private void getUsers()
    {
        //this function gets a list of all the users
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task ->
                {
                    loading(false);
                    String currentUserID = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null)
                    {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult())
                        {
                            if (currentUserID.equals(queryDocumentSnapshot.getId()))
                            {
                                continue;
                            }
                            //creates a User class for each user in the array and assigns their info
                            //to what's in the local preference class after sign in
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            users.add(user);
                        }
                        if (users.size() > 0)
                        {
                            //binding the users to the recycle view
                            UsersAdapter usersAdapter = new UsersAdapter(users, this);
                            binding.usersRecycleView.setAdapter(usersAdapter);
                            binding.usersRecycleView.setVisibility(View.VISIBLE);
                        }else
                        {
                            //if there aren't any users, show error message
                            showErrorMessage();
                        }
                    }else
                    {
                        //if task wasn't successful, show error message
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage()
    {
        //sets the error message text to visible and show the error message
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading)
    {
        //conditional statement to change the visibility status of the progress bar
        if (isLoading)
        {
            binding.progressBar.setVisibility(View.VISIBLE);
        }else
        {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), conversationChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}