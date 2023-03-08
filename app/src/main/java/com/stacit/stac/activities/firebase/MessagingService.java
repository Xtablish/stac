package com.stacit.stac.activities.firebase;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NotNull String token){
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);
    }
}
