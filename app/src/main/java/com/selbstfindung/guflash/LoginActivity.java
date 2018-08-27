package com.selbstfindung.guflash;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupFirebase();
        init();
    }


    public void init() {

        // onclick für "zurück" button (zurück zur Registrierung)
        ((Button) findViewById(R.id.login_back_to_signup_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // finish this Activity
                // -> geht zurück zu parent activtiy (in diesem Fall: SignupActivity)
                finish();
            }
        });


        // onclick für "Login" button
        ((Button) findViewById(R.id.login_ok_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editTextEmail = (EditText) findViewById(R.id.login_email);
                EditText editTextPassword = (EditText) findViewById(R.id.login_password);

                // frage die Werte in den Feldern ab
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if(email.equals("") || password.equals("")) {
                    // Nutzer hat "Login" gedrückt, obwohl nicht alle Felder ausgefüllt sind

                    Toast.makeText(LoginActivity.this, "Bitte füll alle Felder aus", Toast.LENGTH_SHORT).show();

                } else {
                    // Nutzer hat alle Felder ausgefüllt UND die beiden Passwörter sind gleich

                    // sage Firebase-Authentication, dass es den Nutzer anmelden soll
                    // außerdem: gib mir feedback, ob das erfolgreich war
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        // Sign in success

                                        Log.d(TAG, "Login erfolgreich.");

                                        Toast.makeText(LoginActivity.this, "Login erfolgreich.", Toast.LENGTH_SHORT).show();

                                    } else {
                                        // If sign in fails, display a message to the user.

                                        Log.w(TAG, "Login fehlgeschlagen.", task.getException());

                                        Toast.makeText(LoginActivity.this, "Login fehlgeschlagen.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });



    }


    private void setupFirebase() {
        Log.d(TAG, "setting up Firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                {
                    //User ist jetzt eingeloggt
                    Log.d(TAG, "User ist jetzt eingeloggt: "+user.getEmail());


                    // weiterleiten zur Chat-Activity
                    startActivity(new Intent(LoginActivity.this, GroupActivity.class));

                    finish();// user soll nicht mehr hierher zurück können
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
