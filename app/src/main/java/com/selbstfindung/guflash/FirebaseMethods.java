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
        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists.");

        User user = new User();
        long maximum = dataSnapshot.getChildrenCount();
        Log.d(TAG, "schleife läuft " + maximum + " mal");
        //Schleife die durch alle Children der Node "Users" durchgeht
        for(DataSnapshot ds: dataSnapshot.child("users").getChildren())
        {
            user.setName(ds.getValue(User.class).getName());
            //wenn der Username Vorhanden ist wird true ausgegeben
            if(user.getName().equals(username))
            {
                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + user.getName());
                return true;
            }
        }
        Log.d(TAG, "checkIfUsernameExists: DIDN'T FIND A MATCH for : " + user.getName());
        return false;
    }
}
