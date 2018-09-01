package com.selbstfindung.guflash.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.selbstfindung.guflash.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Mainactivity onCreate()");

        setupFirebase();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void setupFirebase() {

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //User ist bereits eingeloggt

                    Log.d(TAG, "User ist bereits an diesem Gerät eingeloggt.");

                    // direkt weiterleiten zu Chat-Activity
                    startActivity(new Intent(MainActivity.this, NavigationActivity.class));
                    finish();// user soll nicht mehr hierher zurück können

                    Log.d(TAG, "finish main activity");

                } else {
                    //User ist ausgeloggt

                    Log.d(TAG, "User ist ausgeloggt. Leite weiter zu Signup-Activity.");

                    // weiterleiten zu Signup-Activity
                    startActivity(new Intent(MainActivity.this, SignupActivity.class));
                    finish();// user soll nicht mehr hierher zurück können

                    Log.d(TAG, "finish main activity");
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
