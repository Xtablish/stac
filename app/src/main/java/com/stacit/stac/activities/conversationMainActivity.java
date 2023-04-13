package com.stacit.stac.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.stacit.stac.activities.adapters.OnlineUsersAdapter;
import com.stacit.stac.activities.adapters.RecentConversationsAdapter;
import com.stacit.stac.activities.listeners.ConversionListener;
import com.stacit.stac.activities.models.ChatMessage;
import com.stacit.stac.activities.models.User;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ConversationMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class conversationMainActivity extends BaseActivity implements ConversionListener
{


    private ConversationMainBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    private Boolean isReceiverAvailable;
    private String receiverUserID;
    private RecentConversationsAdapter conversationsAdapter;
    private OnlineUsersAdapter statusAdapter;
    private FirebaseFirestore database;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
         binding = ConversationMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setting the recycleView spacing
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.usersActiveRecycleView.setLayoutManager(linearLayoutManager);
        //call the initialization function
        init();
        //creating an instance of the preferenceManager class
        preferenceManager = new PreferenceManager(getApplicationContext());
        //call the getUserInfo function
        getUserInfo();
        //call to the getUserToken function to update the KEY_FCM_TOKEN variable
        getUserToken();
        //call to the signOutListener to sign the current user out
        signOutListener();
        //call to the listenerConversations function to update the view
        listenerConversations();
    }

    private void init()
    {
        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations, this);
        statusAdapter = new OnlineUsersAdapter(conversations);
        binding.conversationRecycleView.setAdapter(conversationsAdapter);
        binding.usersActiveRecycleView.setAdapter(statusAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private List<ChatMessage> getActiveUsers()
    {
        List<ChatMessage> chatMessageList = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, true)
                .whereEqualTo(Constants.KEY_SENDER_ID, true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult())
                        {
                            receiverUserID = documentSnapshot.getString(Constants.KEY_RECEIVER_ID);
                            listenAvailabilityOfReceiver();
                            if(isReceiverAvailable)
                            {
                                chatMessage.conversionName = documentSnapshot.getString(Constants.KEY_RECEIVER_NAME);
                                chatMessage.conversionImage = documentSnapshot.getString(Constants.KEY_RECEIVER_IMAGE);
                                chatMessageList.add(chatMessage);
                            }
                        }
                    }
                    else
                    {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                });
        return chatMessageList;
    }


    private void signOutListener()
    {
        //listens for an onClick event, if there is, a call will be made to the userSignOut function
        binding.imageOpenCamera.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), OCRActivity.class)));
        binding.imageAddNewAccounts.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), UsersActivity.class)));
    }

    private void getUserInfo()
    {
        //binding the username from the local Constants class to the textView
        binding.textName.setText("Chats");
        //binding the user profileImage to the imageView
        byte [] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void listenerConversations()
    {
        //this function listens for new messages and then updates the database
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) ->
    {
        //this function updates the chatMessage properties for each message
        if (error != null)
        {
            return;
        }
        if (value != null)
        {
            for (DocumentChange documentChange : value.getDocumentChanges())
            {
                if (documentChange.getType() == DocumentChange.Type.ADDED)
                {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderID = senderId;
                    chatMessage.receiverID = receiverId;

                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId))
                    {
                        chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    }
                    else
                    {
                        chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);

                    }
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversations.add(chatMessage);
                }
                else if (documentChange.getType() == DocumentChange.Type.MODIFIED)
                {
                    for (int i = 0; i < conversations.size(); i++)
                    {
                        String senderID = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverID = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);

                        if (conversations.get(i).senderID.equals(senderID) && conversations.get(i).receiverID.equals(receiverID))
                        {
                            conversations.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversations.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            conversations.sort((obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            conversationsAdapter.notifyDataSetChanged();
            binding.conversationRecycleView.smoothScrollToPosition(0);
            binding.conversationRecycleView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
            //active status
            statusAdapter.notifyDataSetChanged();
            binding.usersActiveRecycleView.smoothScrollToPosition(0);
            binding.usersActiveRecycleView.setVisibility(View.VISIBLE);
            binding.progressBarActiveUsers.setVisibility(View.GONE);
        }
    };

    private void updateUserToken(String userToken)
    {
        //creating an instance of the fireBase fireStore and getting the userID
        FirebaseFirestore fire_store = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                fire_store.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        //updating that userID
        documentReference.update(Constants.KEY_FCM_TOKEN, userToken)
                .addOnFailureListener(e -> showToast("Token update failed"));

    }

    private  void getUserToken()
    {
        //creates a fireBase messaging instance and then use that token to update the KEY_FCM_TOKEN VALUE
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateUserToken);
    }

    private void showToast(String message)
    {
        //displays a toast message to the view
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void listenAvailabilityOfReceiver()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(receiverUserID)
                .addSnapshotListener(conversationMainActivity.this, (value, error) -> {
            if (error != null)
            {
                return;
            }
            if (value != null)
            {
                if (value.getLong(Constants.KEY_AVAILABILITY) != null)
                {
                    int availability = Objects.requireNonNull(
                            value.getLong(Constants.KEY_AVAILABILITY)
                    ).intValue();
                    isReceiverAvailable = availability == 1;
                }
                else
                {
                    isReceiverAvailable = false;
                }
            }
        });
    }

    @Override
    public void onConversionClicked(User user)
    {
        Intent intent = new Intent(getApplicationContext(), conversationChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }

}