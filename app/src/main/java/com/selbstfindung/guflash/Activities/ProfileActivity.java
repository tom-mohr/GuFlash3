package com.selbstfindung.guflash.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.selbstfindung.guflash.R;
import com.selbstfindung.guflash.User;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity
{
    private static final String TAG = "MONTAG";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private DatabaseReference groupRef;

    private String UserID;
    private String Email;
    private String Username;
    private ArrayList<String> Gruppen;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        setupButtons();
        setupFirebase();
        init();
    }


    private void setupButtons()
    {
        //OnClickListener für untere Leiste

        Log.d(TAG, "Setting up Buttons");

        ((Button)findViewById(R.id.profile_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                finish();
            }
        });

        ((Button)findViewById(R.id.home_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, GroupActivity.class));
                finish();
            }
        });
    }

    private void init()
    {
        Log.d(TAG, "Setting up Content");

        user = new User(mUser.getUid());

        ((Button) findViewById(R.id.useless_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserID = mAuth.getCurrentUser().getUid();
                Email = mUser.getEmail();
                Username = Email.substring(0,Email.indexOf('@'));
                //Gruppen.add("");

                mRef.child("users").child(UserID).child("email").setValue(Email);
                mRef.child("users").child(UserID).child("username").setValue(Username);
                mRef.child("users").child(UserID).child("gruppen").child("0").setValue("");

                Toast.makeText(ProfileActivity.this, "Daten zur Datenbank hinzugefügt", Toast.LENGTH_SHORT).show();
            }
        });

        final EditText changeUsername = (EditText) findViewById(R.id.change_username);
        final EditText changePassword = (EditText) findViewById(R.id.change_password);
        final EditText changePasswordConfirm = (EditText) findViewById(R.id.change_password_confirm);

        //changeUsername.setText(user.getUsername());

        ((Button) findViewById(R.id.confirm_changes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String Username = changeUsername.getText().toString();
                final String Password1 = changePassword.getText().toString();
                final String Password2 = changePasswordConfirm.getText().toString();

                changePassword.setText("");
                changePasswordConfirm.setText("");

                //wenn Username geändert wurde
                if(!Username.equals(user.getUsername())&&!Username.equals(""))
                {
                    user.setUsername(changeUsername.getText().toString());
                }
                if(Password1.equals(""))
                {

                }
                else if(!Password1.equals(Password2))
                {
                    Toast.makeText(ProfileActivity.this, "Passwort falsch bestätigt.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mUser.updatePassword(Password1);
                }

            }

        });
        //TODO: aktive Gruppen über Recyclerview ausgeben
        //Layout sollte Gruppe verlassen beinhalten
    }

    private void setupFirebase()
    {
        Log.d(TAG, "Setting up Firebase");

        //brauche Userdaten aus der Datenbank

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {

                mUser = firebaseAuth.getCurrentUser();

                if (mUser == null) {
                    // User ist ausgeloggt, obwohl er noch im Chat ist!
                    // Das ist falsch

                    Log.w(TAG, "User ist in der Chat-Activity, obwohl er nicht angemeldet ist.");

                    // umleiten zur Login-Activity
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));

                    finish();// user soll nicht mehr zurück in den chat kommen, bis er sich wieder angemeldet hat
                }

            }

        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
