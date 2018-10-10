package com.selbstfindung.guflash;

import android.text.format.DateFormat;
import android.util.JsonWriter;
import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.GregorianCalendar;

@IgnoreExtraProperties
public class Message {

    public String senderUserID;
    public String text;
    public long time;
    
    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(String senderUserID, String text, long time) {
        this.senderUserID = senderUserID;
        this.text = text;
        this.time = time;
    }
    
    @Exclude
    public String getSenderUserID() {
        return senderUserID;
    }
    @Exclude
    public String getText() {
        return text;
    }
    @Exclude
    public long getTime() {
        return time;
    }
}
