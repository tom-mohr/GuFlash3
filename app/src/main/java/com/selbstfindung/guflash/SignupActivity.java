package com.selbstfindung.guflash;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";
    private DatabaseReference databaseRef;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        databaseRef = FirebaseDatabase.getInstance().getReference();


        ((Button) findViewById(R.id.signup_ok_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText usernameEditText = (EditText) findViewById(R.id.signup_username);
                EditText passwordEditText = (EditText) findViewById(R.id.signup_password);

                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                // neuen nutzer registieren:

                User newUser = new User(username, password);

                mAuth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();


                                    Intent intent = new Intent(SignupActivity.this, BoringActivity.class);
                                    intent.putExtra(BoringActivity.EXTRA_MESSAGE_USERNAME_TAG, username);
                                    startActivity(intent);


                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignupActivity.this, "Registrierung fehlgeschlagen.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });

                //databaseRef.child("users").child(username).setValue(newUser);


            }
        });
    }
}
