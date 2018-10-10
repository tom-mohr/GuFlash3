package com.selbstfindung.guflash;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

@IgnoreExtraProperties
public class User {

    private static final String TAG = "MONTAG";
    
    private String id;
    private String name;
    private String email;
    private ArrayList<String> eventIDs = new ArrayList<>();
    
    private DatabaseReference userRef;
    
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    
    public abstract interface Callback {
        public void onProfileChanged();
        public void onLoadingFailed();
    }
    
    //liest alle Daten für User aus über die userID
    public User(String userID, @Nullable final Callback callback) {
    
        id = userID;// never changes
        
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(id);
        
        // now, read all data from the userRef
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                String newName = dataSnapshot.child("name").getValue(String.class);
                String newEmail = dataSnapshot.child("email").getValue(String.class);
                
                // in welchen events ist der user
                ArrayList<String> newEventIDs = new ArrayList<>();
                try {
                    Object g = dataSnapshot.child("events").getValue();
                    newEventIDs = (ArrayList<String>) g;
                    
                } catch (ClassCastException e) {
                    Log.w(TAG, "unter 'eventIDs' war keine ArrayList<String>... ", e);
                }
                
                setName(newName);
                setEmail(newEmail);
                setEventIDs(newEventIDs);
                
                if (callback != null)
                    callback.onProfileChanged();
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void make(String id, String name, String email, ArrayList<String> eventIDs) {
        
        // add new child (with id as key) to "users"
        DatabaseReference newUserRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(id);
        
        newUserRef.child("name").setValue(name);
        newUserRef.child("email").setValue(email);
        newUserRef.child("events").setValue(eventIDs);
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
        updateName();
    }
    
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
        updateEmail();
    }

    public ArrayList<String> getEventIDs() {
        return eventIDs;
    }
    public void setEventIDs(ArrayList<String> eventIDs) {
        this.eventIDs = eventIDs;
    }
    public void addEventID(String eventID) {

        if(eventIDs==null) {
            Log.d(TAG, "Creating Eventlist with first element");
            ArrayList<String> arr = new ArrayList<>();
            arr.add(eventID);
            setEventIDs(arr);
            updateEventIDs();
        }
        else if(!eventIDs.contains(eventID))
        {
            Log.d(TAG, "Adding event to list");
            eventIDs.add(eventID);
            updateEventIDs();
        }
    }
    public void removeEventID(String eventID) {
        eventIDs.remove(eventID);
        updateEventIDs();
    }
    
    public void updateName() {
        userRef.child("name").setValue(name);
    }
    public void updateEmail() {
        userRef.child("email").setValue(email);
    }
    public void updateEventIDs() {
        userRef.child("events").setValue(eventIDs);
    }
}