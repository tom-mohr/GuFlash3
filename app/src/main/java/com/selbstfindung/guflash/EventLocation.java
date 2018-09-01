package com.selbstfindung.guflash;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;

public class EventLocation {
    private String name;
    private String Location;
    
    private DatabaseReference eventRef;// the event that this Location belongs to
    private DatabaseReference myRef;
    
    public EventLocation(DatabaseReference eventRef) {
        this.eventRef = eventRef;
        myRef = this.eventRef.child("location");
    }
}
