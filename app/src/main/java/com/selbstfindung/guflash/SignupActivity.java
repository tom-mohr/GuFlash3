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

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setupFirebase();
        init();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "Firebase auth. is running");
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void init() {


        // onclick für Button "Du hast schon einen Account? Melde dich hier wieder an."
        ((Button) findViewById(R.id.signup_goto_login_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // weiterleiten zu Login-Activity
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });


        // onclick für Button "Registrieren"
        ((Button) findViewById(R.id.signup_ok_button)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText editTextEmail = (EditText) findViewById(R.id.signup_email);
                EditText editTextPassword = (EditText) findViewById(R.id.signup_password);
                EditText editTextPasswordConfirm = (EditText) findViewById(R.id.signup_password_confirm);

                // frage die Werte in den Feldern ab
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String passwordConfirm = editTextPasswordConfirm.getText().toString();

                if(email.equals("") || password.equals("") || passwordConfirm.equals("")) {
                    // Nutzer hat "Registrieren" gedrückt, obwohl nicht alle Felder ausgefüllt sind

                    Toast.makeText(SignupActivity.this, "Bitte füll alle Felder aus", Toast.LENGTH_SHORT).show();

                } else if (passwordConfirm.equals(password)) {
                    // Nutzer hat alle Felder ausgefüllt UND die beiden Passwörter sind gleich

                    // signalisiere dem Nutzer, dass jetzt versucht wird, ihn zu registrieren
                    Toast.makeText(SignupActivity.this, "Registrierung...", Toast.LENGTH_SHORT);


                    // sage Firebase-Authentication, dass es einen neuen Nutzer erstellen soll
                    // außerdem: gib mir feedback, ob das erfolgreich war
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task< AuthResult > task)
                                {

                                    if (task.isSuccessful()) {
                                        // Sign in success

                                        Log.d(TAG, "Neuer User wurde erstellt.");

                                        Toast.makeText(SignupActivity.this, "Registrierung erfolgreich.", Toast.LENGTH_SHORT).show();


                                    } else {
                                        // If sign in fails, display a message to the user.

                                        // TODO: Verschiedene Errors unterscheiden (zu kurzes Passwort? email bereits registriert?)

                                        Log.w(TAG, "Registrierung fehlgeschlagen.", task.getException());

                                        Toast.makeText(SignupActivity.this, "Registrierung fehlgeschlagen.", Toast.LENGTH_SHORT).show();
                                    }
                                }});

                } else {

                    // alle Felder sind ausgefüllt, aber das Bestätigungs-Passwort ist falsch.

                    Toast.makeText(SignupActivity.this, "Passwort falsch bestätigt.", Toast.LENGTH_SHORT).show();

                    // lösche den Inhalt des Passwort-bestätigen-Feldes
                    editTextPasswordConfirm.setText("");
                }



            }
        });
    }

    private void setupFirebase()
    {
        Log.d(TAG, "setting up Firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user !=null) {
                    //User ist jetzt eingeloggt (-> Registrierung war also erfolgreich)

                    Log.d(TAG, "User ist jetzt eingeloggt.");

                    // weiterleiten zur Chat-Activity
                    startActivity(new Intent(SignupActivity.this, GroupActivity.class));

                    finish();// user soll nicht mehr hierher zurück können
                }
            }
        };


    }

    @Override
    public void onStop() {
        Log.d(TAG, "Firebase auth. stopped");
        super.onStop();
        if (mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
