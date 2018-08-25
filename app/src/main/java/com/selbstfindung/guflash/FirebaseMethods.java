package com.selbstfindung.guflash;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Ref;

public class FirebaseMethods
{
    private static final String TAG = "MONTAG";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private String userID;

    private Context mContext;

    public FirebaseMethods(Context context)
    {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mContext = context;


        if(mAuth.getCurrentUser() != null)
        {
            userID = mAuth.getCurrentUser().getUid();
        }
    }
    //Überprüfen ob Username bereits verwendet wird

    public static boolean UsernameExists(String username, DataSnapshot dataSnapshot)
    {
        User user = new User();

        //Schleife die durch alle Children durchgeht
        for(DataSnapshot ds: dataSnapshot.getChildren())
        {
            user.setUsername(ds.getValue(User.class).getUsername());
            //wenn der Username Vorhanden ist wird true ausgegeben
            if(user.getUsername().equals(username))
            {
                return true;
            }
        }
        return false;
    }
    //fügt User zur Datenbank hinzu
    public void addUserToDatabase(String username, String password)
    {
        User user = new User(username, password);

        mRef.child("users").child(username).setValue(user);
    }
}
