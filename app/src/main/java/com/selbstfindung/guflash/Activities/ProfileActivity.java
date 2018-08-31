package com.selbstfindung.guflash.Activities;


import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    
    private User user;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        
        init();
    }

    private void init() {

        setTitle("Profil");
    
        Log.d(TAG, "init: wuaaaaat?");
        
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        if (userID != null) {
            
            user = new User(userID, new User.Callback() {
                @Override
                public void onProfileChanged() {
                    ((TextView) findViewById(R.id.profile_username)).setText(user.getName());
                    ((TextView) findViewById(R.id.profile_email)).setText(user.getEmail());
    
    
                    //TODO: aktive Gruppen über Recyclerview ausgeben
                    //Bessere Idee: Im Navigation Drawer kann man ja in "meine Events" wechseln
                }
    
                @Override
                public void onLoadingFailed() {
                    Log.d(TAG, "Profil konnte nicht geladen werden");
                }
            });
            
        } else {
            
            Log.w(TAG, "Firebase-Auth. hat keine CurrentUser-Uid geliefert");
        }
    }
}
