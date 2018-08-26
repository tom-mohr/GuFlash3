package com.selbstfindung.guflash;

import android.content.Context;
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
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";

    private Context mContext;
    private String username, password;

    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods firebaseMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = LoginActivity.this;
        firebaseMethods = new FirebaseMethods(mContext);

        setupFirebase();
        init();
    }


    public void init() {
        ((Button) findViewById(R.id.login_ok_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText usernameEditText = (EditText) findViewById(R.id.login_username);
                EditText passwordEditText = (EditText) findViewById(R.id.login_password);

                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if (!(username.equals("") || password.equals(""))) {
                    mAuth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                    if (!task.isSuccessful()) {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Einloggen fehlgeschlagen.",
                                                Toast.LENGTH_SHORT).show();

                                    } else if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "Login:success");
                                        Toast.makeText(LoginActivity.this, "Einloggen erfolgreich",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                }

                // Wenn User eingeloggt ist soll er zur Boring Activity weitergeleitet werden

                if (mAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(LoginActivity.this, BoringActivity.class);
                    intent.putExtra(BoringActivity.EXTRA_MESSAGE_USERNAME_TAG, username);
                    startActivity(intent);
                    finish();
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
                    //User ist eingeloggt
                    Log.d(TAG, "onAuthStateChanged:signed_in:");
                }
                else
                {
                    //User ist ausgeloogt
                    Log.d(TAG, "onAuthStateChanged:signed_out");
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
