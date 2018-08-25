package com.selbstfindung.guflash;

import android.content.Context;
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
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_signup);
        mContext = SignupActivity.this;
        firebaseMethods = new FirebaseMethods(mContext);

        setupFirebase();
        init();
    }

    private void init()
    {
        ((Button) findViewById(R.id.signup_ok_button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                EditText usernameEditText = (EditText) findViewById(R.id.signup_username);
                EditText passwordEditText = (EditText) findViewById(R.id.signup_password);

                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if(!(username.equals("")||password.equals("")))
                    mAuth.createUserWithEmailAndPassword(username, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task< AuthResult > task)
                                {
                                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                    if (!task.isSuccessful()) {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignupActivity.this, "Registrierung fehlgeschlagen.",
                                                Toast.LENGTH_SHORT).show();

                                    } else if(task.isSuccessful()){
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                    }
                                }});





            }
        });
    }
    private void setupFirebase()
    {
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //if(user !=null)
                //{
                    //User ist eingeloggt
                    databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange (DataSnapshot dataSnapshot)
                    {
                        //User existiert bereits -> wird nicht weitergeleitet
                        if (FirebaseMethods.UsernameExists(username, dataSnapshot))
                        {

                        }
                        //User gibt es nicht -> Eintrag wird in Datenbank erstellt und wird weitergeleitet
                        else
                        {
                            //databaseRef.child("users").child(username).setValue(newUser);
                            firebaseMethods.addUserToDatabase(username, password);

                            Intent intent = new Intent(SignupActivity.this, BoringActivity.class);
                            intent.putExtra(BoringActivity.EXTRA_MESSAGE_USERNAME_TAG, username);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled (DatabaseError databaseError)
                    {
                        
                    }
                    });
                //}
                //else
                //{
                    //User ist ausgeloggt
                //}
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
        if (mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
