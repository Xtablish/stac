package com.stacit.stac.activities.models;

import java.util.Date;

public class ChatMessage
{
    public String senderID, receiverID, message, date, time, lastMessageSender, sender;
    public Boolean typing, isMessageRead, online;
    public Date dateObject;
    public String conversionId, conversionName, conversionImage;
}
