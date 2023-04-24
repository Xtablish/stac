package com.stacit.stac.activities.Control;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.stacit.stac.activities.adapters.ChatAdapter;
import com.stacit.stac.activities.models.ChatMessage;
import com.stacit.stac.activities.models.User;
import com.stacit.stac.activities.utilities.Constants;
import com.stacit.stac.activities.utilities.PreferenceManager;
import com.stacit.stac.databinding.ConversationChatBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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
    private String receiverID;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private String lastSender;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private Boolean isReceiverTyping = false;
    private Boolean isReceiverAvailable = false;
    private String conversationId = null;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_SELECT_GALLERY_CODE = 1000;
    private static final int IMAGE_TAKE_CAMERA_CODE = 1001;
    String[] cameraPermission;
    String[] storagePermission;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ConversationChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //camera permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //camera intent onClick Listeners
        binding.layoutCamera.setOnClickListener(view -> {
            //camera option clicked
            if (!checkedCameraPermission())
            {
                //camera permission not allowed, request it
                requestCameraPermission();
            }
            else
            {
                //permission allowed, take picture
                useCamera();
            }
        });

        //gallery intent onClick Listener
        binding.layoutGallery.setOnClickListener(view -> {
            //gallery option clicked
            if (!checkedStoragePermission())
            {
                //storage permission not allowed, request it
                requestStoragePermission();
            }
            else
            {
                //permission allowed, select picture
                useGallery();
            }
        });

        //call to private functions
        setListener();
        loadReceiverDetails();
        init();
        listenMessages();
        listenTypingOfReceiver();
        updateConversations();
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
        if (!binding.inputMessage.getText().toString().isEmpty())
        {
            //update last sender for the recent messages view
            lastSender = preferenceManager.getString(Constants.KEY_USER_ID);
            HashMap<String, Object> message = new HashMap<>();
            message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            message.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
            message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString().trim());
            message.put(Constants.KEY_LAST_MESSAGE_SENDER, preferenceManager.getString(Constants.KEY_USER_ID));
            message.put(Constants.KEY_TIMESTAMP, new Date());
            database.collection(Constants.KEY_COLLECTION_CHAT).add(message);

            //if this is a new conversation
            if (conversationId != null)
            {
                updateConversation(binding.inputMessage.getText().toString());
            }else
            {
                //create a new conversation
                HashMap<String, Object> conversion = new HashMap<>();
                conversion.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                conversion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
                conversion.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
                conversion.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
                conversion.put(Constants.KEY_RECEIVER_NAME, receiverUser.name);
                conversion.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.image);
                conversion.put(Constants.KEY_RECEIVER_TYPING, false);
                conversion.put(Constants.KEY_RECEIVER_ONLINE, false);
                conversion.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString().trim());
                conversion.put(Constants.KEY_LAST_MESSAGE_SENDER, preferenceManager.getString(Constants.KEY_USER_ID));
                conversion.put(Constants.KEY_TIMESTAMP, new Date());
                addConversion(conversion);
            }
            //clear the input field after the message was sent
            binding.inputMessage.setText(null);
        }
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
                //if the receiver is online update the view
                binding.imageOnlineStatus.setVisibility(View.VISIBLE);

            }
            else
            {
                //if the receiver isn't online update the view
                binding.imageOnlineStatus.setVisibility(View.INVISIBLE);

            }
        });
    }

    //similar to the listener fo receiver availability, this function listens for whether the receiver is typing
    private void listenTypingOfReceiver()
    {
        if (receiverUser.id != null)
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
                    if (value.get(Constants.KEY_TYPING) != null && value.get(Constants.KEY_TYPING).equals(true))
                    {
                        binding.typingStatus.setVisibility(View.VISIBLE);
                        isReceiverTyping = true;
                    }
                    else
                    {
                        binding.typingStatus.setVisibility(View.INVISIBLE);
                        isReceiverTyping = false;
                    }
                }
            });
        }
    }

    //this function uses the eventListener to query data associate with the current sender and receiver
    private void listenMessages()
    {
        //listen for messages from both the receiver and the sender
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
                    chatMessage.time = getReadableTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.date = getReadableDate(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
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
        binding.readReceiverImage.setImageBitmap(getBitmapFromEncodedString(receiverUser.image));
        receiverID = receiverUser.id;
    }

    //converts the date and time into a readable format
    private String getReadableTime(Date time)
    {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(time);
    }
    private String getReadableDate(Date date)
    {
        return new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date);
    }

    //binding listeners for onClick events from the view
    private void setListener() {
        binding.imageBackBtn.setOnClickListener(view -> onBackPressed());
        binding.layoutSendReady.setOnClickListener(v -> sendMessage());

        //check to editText for focus
        binding.inputMessage.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus)
            {
                //hide other messaging tools if using is typing
                binding.layoutMic.setVisibility(View.GONE);
                binding.layoutCamera.setVisibility(View.GONE);
                binding.layoutGallery.setVisibility(View.GONE);

                //changes the send button state to ready
                binding.layoutSendReady.setVisibility(View.VISIBLE);
                binding.layoutSend.setVisibility(View.INVISIBLE);
            }
            else
            {
                //changes the send button state
                binding.layoutSend.setVisibility(View.VISIBLE);
                binding.layoutSendReady.setVisibility(View.INVISIBLE);

                //show other messaging tools if user isn't typing
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
                if (charSequence.toString().trim().length() == 0)
                {
                    updateTypingStatus(false);
                }
                else
                {
                    updateTypingStatus(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable)
            {
            }
        });

    }

    //user is typing function
    private void updateTypingStatus(boolean status)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.KEY_TYPING, status);

        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .set(data, SetOptions.merge());
    }

    //this function updates the database for the recent conversations feed
    private void updateTypingStatusRecent(boolean status)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.KEY_RECEIVER_TYPING, status);

        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .document(conversationId)
                .set(data, SetOptions.merge());
    }

    //this function updates the database for the recent conversations feed
    private void updateReceiverAvailability(boolean isReceiverAvailable)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.KEY_RECEIVER_ONLINE, isReceiverAvailable);

        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .document(conversationId)
                .set(data, SetOptions.merge());
    }

    //this function updates the conversations collection if the current user is the receiver
    private void updateConversations()
    {
        if (Objects.equals(receiverID, preferenceManager.getString(Constants.KEY_USER_ID)))
        {
            updateReceiverAvailability(isReceiverAvailable);
            updateTypingStatusRecent(isReceiverTyping);
        }
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
                Constants.KEY_LAST_MESSAGE_SENDER, lastSender,
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

    //this function updates the view to show the soft keyboard when the editText is focused
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

    //this function  is used to launch the camera activity, so the user can take a picture for the text recognition
    private void useCamera()
    {
        //function to take image using the camera, the image is also save to storage
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "scan_doc_image"); //set the tile of the image
        values.put(MediaStore.Images.Media.DESCRIPTION, "Text recognition image"); //set the image description
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_TAKE_CAMERA_CODE);
    }

    //this function is used to request storage permission from the user
    private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    //this function is used to check if the app already has permission to use the storage
    private boolean checkedStoragePermission()
    {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    //this function is used to request camera permission from the user
    private void requestCameraPermission()
    {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    //this function is used to check if the app already has permission to use the camera
    private boolean checkedCameraPermission()
    {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean resultExtended = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && resultExtended;
    }

    //handle permission result by overriding the onRequestPermissionResult function
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        useCamera();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        useGallery();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    //this function is called to start the gallery activity, so the user can select a photo
    private void useGallery()
    {
        //function to select image from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        //set intent type to image
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_SELECT_GALLERY_CODE);
    }

    //handle image result by overriding the onActivityResult function
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == IMAGE_SELECT_GALLERY_CODE)
            {
                //get image from gallery and crop it
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON) //enable image guidelines
                        .start(this);
            }
            else if (requestCode == IMAGE_TAKE_CAMERA_CODE)
            {
                //take image using camera and crop it
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON) //enable image guidelines
                        .start(this);
            }
        }
        //get cropped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                Uri resultUri = result.getUri(); //get image uri
                //set image to image view
                binding.imageIVPreview.setImageURI(resultUri);

                //get drawable bitmap for text recognition
                BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.imageIVPreview.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational())
                {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();

                    //get text from textBlock into stringBuilder
                    for (int i =0; i<items.size(); i++)
                    {
                        TextBlock myItem = items.valueAt(i);
                        stringBuilder.append(myItem.getValue());
                        stringBuilder.append("\n");
                    }
                    //set text to edit text view
                    binding.inputMessage.setText(stringBuilder.toString());
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                //if there's any error, use the toast method to show it
                Exception error = result.getError();
                Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}