package com.selbstfindung.guflash;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class BoringActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";

    public static final String EXTRA_MESSAGE_USERNAME_TAG = "USERNAME_MESSAGE";

    private TextView chat;
    private EditText chatTextInput;

    /// für dev.:
    private int counter;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boring);

        setupFirebase();
        init();
    }

    private void init() {
        // get views (youtube money)
        chat = (TextView) findViewById(R.id.chat_message_list);
        chatTextInput = (EditText) findViewById(R.id.edittext_chatbox);


        chat.setText("");
        counter = 0;


        databaseRef = FirebaseDatabase.getInstance().getReference();

        // listen to database changes (new messages)
        databaseRef.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message msg = dataSnapshot.getValue(Message.class);

                Log.d(TAG, "Message added: "+msg.senderUsername+": "+msg.text);

                // add message (update UI)
                chat.setText(chat.getText().toString() + msg.senderUsername + ": " + msg.text + "\n");


                int id = Integer.valueOf(dataSnapshot.getKey());
                if (id >= counter) {
                    counter = id+1;
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                chat.setText("");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });


        // onclick für "senden" button
        ((Button) findViewById(R.id.chat_send_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // text string vom input feld kriegen
                String text = chatTextInput.getText().toString();

                if (! text.equals("")) {

                    // input feld leermachen
                    chatTextInput.setText("");

                    // send message
                    writeNewMessage(user.getEmail(), text);
                }
            }
        });

        /// für dev:
        // onclick für "Chat löschen" button
        ((Button) findViewById(R.id.button_delete_chat)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseRef.child("messages").removeValue(null);
            }
        });

    }

    private void writeNewMessage(String senderUsername, String text) {
        Message msg = new Message(senderUsername, text);

        String messageId = String.valueOf(counter);

        databaseRef.child("messages").child(messageId).setValue(msg);

        counter++;
    }


    private void setupFirebase() {

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {

                user = firebaseAuth.getCurrentUser();

                if (user == null) {
                    // User ist ausgeloggt, obwohl er noch im Chat ist!
                    // Das ist falsch

                    Log.w(TAG, "User ist in der Chat-Activity, obwohl er nicht angemeldet ist.");

                    // umleiten zur Login-Activity
                    startActivity(new Intent(BoringActivity.this, LoginActivity.class));

                    finish();// user soll nicht mehr zurück in den chat kommen, bis er sich wieder angemeldet hat
                }

            }

        };
    }

    @Override
    public void onBackPressed() {
        // zurück-knopf am handy beendet die ganze app
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
