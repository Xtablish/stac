package com.stacit.stac.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.stacit.stac.R;
import com.stacit.stac.activities.adapters.ChatAdapter;
import com.stacit.stac.activities.models.ChatMessage;
import com.stacit.stac.activities.models.User;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ConversationChatBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class conversationChatActivity extends BaseActivity
{
    //private members initialization
    private ConversationChatBinding binding;
    private User receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private Boolean isReceiverAvailable = false;
    private String conversationId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ConversationChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //call to private functions
        setListener();
        loadReceiverDetails();
        init();
        listenMessages();
    }
    //initializes key components for other functions
    private void init()
    {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitmapFromEncodedString(receiverUser.image),
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        binding.chatRecycle.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }
    //takes the text from the EditText input and pushes it to the database
    private void sendMessage()
    {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);

        if (conversationId != null)
        {
            updateConversation(binding.inputMessage.getText().toString());
        }else
        {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
            conversion.put(Constants.KEY_RECEIVER_NAME, receiverUser.name);
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.image);
            conversion.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }

        binding.inputMessage.setText(null);
    }
    //checks if the receiver is online or not
    @SuppressLint("SetTextI18n")
    private void listenAvailabilityOfReceiver()
    {
        database.collection(Constants.KEY_COLLECTION_USERS).document(
                receiverUser.id
        ).addSnapshotListener(conversationChatActivity.this, (value, error) -> {
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
            }
            if (isReceiverAvailable)
            {
                binding.imageOnlineStatus.setVisibility(View.VISIBLE);
            }
            else
            {
                binding.imageOnlineStatus.setVisibility(View.INVISIBLE);
            }
        });
    }

    //this function uses the eventListener to query data associate with the current sender and receiver
    private void listenMessages()
    {
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);

        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }
    //this is an eventListener for the database query changes
    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) ->
    {
        if (error != null)
        {
            return;
        }
        if (value != null)
        {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges())
            {
                if (documentChange.getType() == DocumentChange.Type.ADDED)
                {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderID = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverID = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            chatMessages.sort(Comparator.comparing(obj -> obj.dateObject));
            if (count == 0)
            {
                chatAdapter.notifyDataSetChanged();
            }else
            {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecycle.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecycle.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);

        if (conversationId == null)
        {
            checkForConversion();
        }
    };
    //converts the encoded string data into a bitmap image using Base64 algorithm
    private Bitmap getBitmapFromEncodedString(String encodedImage)
    {
        byte [] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    //updates the view with the receiver information
    private void loadReceiverDetails()
    {
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textUsername.setText(receiverUser.name);
        binding.imageProfile.setImageBitmap(getBitmapFromEncodedString(receiverUser.image));
    }
    //converts the date and time into a readable format
    private String getReadableDateTime(Date date)
    {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
    //binding listeners for onClick events from the view
    private void setListener() {
        binding.imageBackBtn.setOnClickListener(view -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> sendMessage());
        binding.cameraRecognitionBtn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), OCRActivity.class)));
        binding.imageProfile.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), settingsProfileActivity.class)));

        //change the board radius of the send button
        if (binding.inputMessage.getText().toString() != null)
        {
            binding.layoutSend.setBackgroundResource(R.drawable.send_button_ready_state);
        }
        else
        {
            binding.layoutSend.setBackgroundResource(R.drawable.background_image);
        }

        //check to editText for focus
        binding.inputMessage.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus)
            {
                binding.layoutMic.setVisibility(View.GONE);
                binding.layoutCamera.setVisibility(View.GONE);
                binding.layoutGallery.setVisibility(View.GONE);
            }
            else
            {
                binding.layoutGallery.setVisibility(View.VISIBLE);
                binding.layoutCamera.setVisibility(View.VISIBLE);
                binding.layoutMic.setVisibility(View.VISIBLE);
            }
        });

        //implementation for isUserTyping feature
        binding.inputMessage.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            }
            @Override
            public void afterTextChanged(Editable editable)
            {
            }
        });

    }
    //adds the conversion of the conversation data into the database conversations collection
    private void addConversion(HashMap<String, Object> conversion)
    {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversationId = documentReference.getId());
    }

    //updates the conversation to the last sent or received message
    private void updateConversation(String message)
    {
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversationId);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE, message,
                Constants.KEY_TIMESTAMP, new Date()
        );
    }
    //checks the database to see if any new message is for the sender or receiver
    private void checkForConversion()
    {
        if (chatMessages.size() != 0)
        {
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    receiverUser.id
            );
            checkForConversionRemotely(
                    receiverUser.id,
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }
    //checks the database for new conversations
    private void checkForConversionRemotely(String senderId, String receiverId)
    {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversationOnCompleteListener);
    }
    //OnComplete Listener to check if the query from the database isn't empty
    private final OnCompleteListener<QuerySnapshot> conversationOnCompleteListener = task ->
    {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0)
        {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversationId = documentSnapshot.getId();

        }
    };
    //overriding and  updating the onResume function from the BaseActivity
    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            View view = getCurrentFocus();
            if (view instanceof EditText)
            {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY()))
                {
                    view.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}