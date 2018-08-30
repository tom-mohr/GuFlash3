package com.selbstfindung.guflash;

import android.support.annotation.NonNull;
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

    private String username;
    private String email;
    private ArrayList<String> Gruppen;
    private String userID;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    //liest alle Daten für User aus über die userID
    public User(String userID)
    {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        this.userID = userID;

        mRef.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                username = dataSnapshot.child("username").getValue(String.class);
                email = dataSnapshot.child("email").getValue(String.class);

                //später um aktive Gruppen beim User zu speichern

                //for(DataSnapshot ds: dataSnapshot.child("gruppen").getChildren())
                //{
                    //Gruppen.add(ds.getValue(String.class));
                //}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public User(String username, String email, ArrayList<String> Gruppen)
    {
        this.username = username;
        this.email = email;
        this.Gruppen = Gruppen;
    }

    public String getUsername()
    {
        Log.d(TAG, "Username wird abgefragt "+username);
        return username;
    }

    public void setUsername(String username)
    {
        mRef.child("users").child(userID).child("username").setValue(username);
    }
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        mRef.child("users").child(userID).child("email").setValue(email);
    }

    public ArrayList<String> getGruppen()
    {
        return Gruppen;
    }

    public void addGruppe(String Gruppe)
    {
        this.Gruppen.add(Gruppe);
    }

    public void removeGruppe(String Gruppe)
    {
        this.Gruppen.remove(Gruppe);
    }
}