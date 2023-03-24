package com.stacit.stac.activities.StacAI;

import android.annotation.SuppressLint;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.stacit.stac.activities.BaseActivity;
import com.stacit.stac.activities.models.ChatMessage;
import com.stacit.stac.activities.models.User;
import com.stacit.stac.activities.utilities.STAC;
import com.stacit.stac.activities.utilities.StacManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class StacMessages extends BaseActivity
{
    //local variables and instances
    private FirebaseFirestore database;
    private Boolean isReceiverAvailable = false;
    private Boolean isStacSignedIn = false;
    private String conversationId = null;
    private List<ChatMessage> chatMessages;
    private StacManager preferenceManager;
    private User receiverUser;


    //initializes key components for other functions
    public void init(StacManager stacManager)
    {
        preferenceManager = stacManager;
        chatMessages = new ArrayList<>();
        database = FirebaseFirestore.getInstance();
    }


    //checks the input validation and then checks the database for the signIn details entered
    public void signIn()
    {
        database.collection(STAC.KEY_COLLECTION_USERS)
                .whereEqualTo(STAC.KEY_EMAIL, "stac.ai@stacit.com")
                .whereEqualTo(STAC.KEY_PASSWORD, "Punisher_Pete2003")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0)
                    {
                        //getting data from the database and putting them in the STAC class using the preferenceManager class
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(STAC.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(STAC.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(STAC.KEY_NAME, documentSnapshot.getString(STAC.KEY_NAME));
                        preferenceManager.putString(STAC.KEY_IMAGE, documentSnapshot.getString(STAC.KEY_IMAGE));
                        preferenceManager.putString(STAC.KEY_FACE_ID, documentSnapshot.getString(STAC.KEY_FACE_ID));
                        preferenceManager.putString(STAC.KEY_LANGUAGE, documentSnapshot.getString(STAC.KEY_LANGUAGE));
                        preferenceManager.putString(STAC.KEY_LISTEN, documentSnapshot.getString(STAC.KEY_LISTEN));
                        preferenceManager.putString(STAC.KEY_VOICE, documentSnapshot.getString(STAC.KEY_VOICE));
                        preferenceManager.putString(STAC.KEY_AI, documentSnapshot.getString(STAC.KEY_AI));
                        preferenceManager.putString(STAC.KEY_COUNTRY, documentSnapshot.getString(STAC.KEY_COUNTRY));

                        isStacSignedIn = true;
                    } else
                    {
                       isStacSignedIn = false;
                    }
                });
    }

    //this function allows STAC to send messages
    public void sendMessage(String messageSend)
    {
        HashMap<String, Object> message = new HashMap<>();
        message.put(STAC.KEY_SENDER_ID, preferenceManager.getString(STAC.KEY_USER_ID));
        message.put(STAC.KEY_RECEIVER_ID, receiverUser.id);
        message.put(STAC.KEY_MESSAGE, messageSend);
        message.put(STAC.KEY_TIMESTAMP, new Date());
        database.collection(STAC.KEY_COLLECTION_CHAT).add(message);

        if (conversationId != null)
        {
            updateConversation(messageSend);
        }else
        {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(STAC.KEY_SENDER_ID, preferenceManager.getString(STAC.KEY_USER_ID));
            conversion.put(STAC.KEY_SENDER_NAME, preferenceManager.getString(STAC.KEY_NAME));
            conversion.put(STAC.KEY_SENDER_IMAGE, preferenceManager.getString(STAC.KEY_IMAGE));
            conversion.put(STAC.KEY_RECEIVER_ID, receiverUser.id);
            conversion.put(STAC.KEY_RECEIVER_NAME, receiverUser.name);
            conversion.put(STAC.KEY_RECEIVER_IMAGE, receiverUser.image);
            conversion.put(STAC.KEY_LAST_MESSAGE, messageSend);
            conversion.put(STAC.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }
    }

    //checks if the receiver is online or not
    @SuppressLint("SetTextI18n")
    private void listenAvailabilityOfReceiver()
    {
        database.collection(STAC.KEY_COLLECTION_USERS).document(
                receiverUser.id
        ).addSnapshotListener(StacMessages.this, (value, error) -> {
            if (error != null)
            {
                return;
            }
            if (value != null)
            {
                if (value.getLong(STAC.KEY_AVAILABILITY) != null)
                {
                    int availability = Objects.requireNonNull(
                            value.getLong(STAC.KEY_AVAILABILITY)
                    ).intValue();
                    isReceiverAvailable = availability == 1;
                }
            }
        });
    }


    //this function uses the eventListener to query data associate with the current sender and receiver
    public void listenMessages()
    {
        database.collection(STAC.KEY_COLLECTION_CHAT)
                .whereEqualTo(STAC.KEY_SENDER_ID, preferenceManager.getString(STAC.KEY_USER_ID))
                .whereEqualTo(STAC.KEY_RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);

        database.collection(STAC.KEY_COLLECTION_CHAT)
                .whereEqualTo(STAC.KEY_SENDER_ID, receiverUser.id)
                .whereEqualTo(STAC.KEY_RECEIVER_ID, preferenceManager.getString(STAC.KEY_USER_ID))
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
            for (DocumentChange documentChange : value.getDocumentChanges())
            {
                if (documentChange.getType() == DocumentChange.Type.ADDED)
                {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderID = documentChange.getDocument().getString(STAC.KEY_SENDER_ID);
                    chatMessage.receiverID = documentChange.getDocument().getString(STAC.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(STAC.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(STAC.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(STAC.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
        }
        if (conversationId == null)
        {
            checkForConversion();
        }
    };

    //converts the date and time into a readable format
    private String getReadableDateTime(Date date)
    {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }


    //adds the conversion of the conversation data into the database conversations collection
    private void addConversion(HashMap<String, Object> conversion)
    {
        database.collection(STAC.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversationId = documentReference.getId());
    }

    //updates the conversation to the last sent or received message
    private void updateConversation(String message)
    {
        DocumentReference documentReference =
                database.collection(STAC.KEY_COLLECTION_CONVERSATIONS).document(conversationId);
        documentReference.update(
                STAC.KEY_LAST_MESSAGE, message,
                STAC.KEY_TIMESTAMP, new Date()
        );
    }

    //checks the database to see if any new message is for the sender or receiver
    private void checkForConversion()
    {
        if (chatMessages.size() != 0)
        {
            checkForConversionRemotely(
                    preferenceManager.getString(STAC.KEY_USER_ID),
                    receiverUser.id
            );
            checkForConversionRemotely(
                    receiverUser.id,
                    preferenceManager.getString(STAC.KEY_USER_ID)
            );
        }
    }

    //checks the database for new conversations
    private void checkForConversionRemotely(String senderId, String receiverId)
    {
        database.collection(STAC.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(STAC.KEY_SENDER_ID, senderId)
                .whereEqualTo(STAC.KEY_RECEIVER_ID, receiverId)
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
}
