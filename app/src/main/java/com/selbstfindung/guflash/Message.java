package com.selbstfindung.guflash;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Message {

    public String senderUsername;
    public String text;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(String senderUsername, String text) {
        this.senderUsername = senderUsername;
        this.text = text;
    }
}
