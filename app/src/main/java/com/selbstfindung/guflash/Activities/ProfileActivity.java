package com.selbstfindung.guflash.Activities;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
        
        
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        if (userID != null) {
            
            user = new User(userID, new User.Callback() {
                @Override
                public void onProfileChanged() {
                    ((TextView) findViewById(R.id.profile_username)).setText(user.getName());
                    ((TextView) findViewById(R.id.profile_email)).setText(user.getEmail());

                }
    
                @Override
                public void onLoadingFailed() {
                    Log.d(TAG, "Profil konnte nicht geladen werden");
                }
            });
            
        } else {
            
            Log.w(TAG, "Firebase-Auth. hat keine CurrentUser-Uid geliefert");
        }
        
        
        ((ImageButton) findViewById(R.id.profile_username_edit_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                // Dialog öffnen
                
    
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Namen bearbeiten");
    
                final EditText input = new EditText(ProfileActivity.this);
                // Specify the type of input expected
                input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                input.setText(user.getName());// already put the existing username in there
                builder.setView(input);
                
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newUsername = input.getText().toString();
                        
                        user.setName(newUsername);
                    }
                });
                builder.setNegativeButton("abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                
                builder.show();
            }
        });
        
        ((Button) findViewById(R.id.profile_change_password_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    
                // passwort-eingabefeld freigeben und button verschwinden lassen
                
                ((LinearLayout) findViewById(R.id.profile_change_password_layout)).setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.profile_change_password_button)).setVisibility(View.GONE);
            }
        });
    
        ((Button) findViewById(R.id.profile_change_password_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    
                ((LinearLayout) findViewById(R.id.profile_change_password_layout)).setVisibility(View.GONE);
                ((Button) findViewById(R.id.profile_change_password_button)).setVisibility(View.VISIBLE);
            }
        });
    
        ((Button) findViewById(R.id.profile_change_password_confirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    
                FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
    
                if (authUser != null) {
    
                    // Passwort ändern
                    String password = ((TextView) findViewById(R.id.profile_change_password_edittext)).getText().toString();
                    authUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Passwort geändert.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Passwort konnte nicht geändert werden.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    
                } else {
                    Toast.makeText(ProfileActivity.this, "Fehler: Du bist nicht angemeldet.", Toast.LENGTH_SHORT).show();
                }
                
                
                ((LinearLayout) findViewById(R.id.profile_change_password_layout)).setVisibility(View.GONE);
                ((Button) findViewById(R.id.profile_change_password_button)).setVisibility(View.VISIBLE);
            }
        });
    }
}
