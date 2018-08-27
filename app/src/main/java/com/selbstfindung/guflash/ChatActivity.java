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

import java.util.ArrayList;


public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";

    public static final String EXTRA_MESSAGE_GRUPPEN_NAME = "GRUPPEN_NAME";

    private EditText chatTextInput;

    private ArrayList<String> messageUsernames = new ArrayList<>();
    private ArrayList<String> messageTexts = new ArrayList<>();

    RecyclerViewAdapter recyclerViewAdapter;// need instance for newly added messages

    /// for dev
    private int counter = 0;
    private String Gruppenname;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.d(TAG, "ChatActivity onCreate()");

        Intent intent = getIntent();
        Gruppenname = intent.getStringExtra(EXTRA_MESSAGE_GRUPPEN_NAME);

        setupFirebase();
        init();
    }

    private void init() {

        // get views (youtube money)
        chatTextInput = (EditText) findViewById(R.id.edittext_chatbox);


        databaseRef = FirebaseDatabase.getInstance().getReference();

        // listen to database changes (new messages)
        databaseRef.child("Gruppen").child(Gruppenname).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message msg = dataSnapshot.getValue(Message.class);

                // absender und inhalt den arrays hinzufügen
                messageUsernames.add(msg.senderUsername);
                messageTexts.add(msg.text);

                Log.d(TAG, "Message added: "+msg.senderUsername+": "+msg.text);


                int id = Integer.valueOf(dataSnapshot.getKey());
                if (id >= counter) {
                    counter = id+1;
                }


                // notify recyclerViewAdapter that new message was added
                // but only, if it was already initialized
                // see: https://stackoverflow.com/questions/27845069/add-a-new-item-to-recyclerview-programmatically
                if (recyclerViewAdapter != null) {

                    int position = messageTexts.size() - 1;// last index in list
                    recyclerViewAdapter.notifyItemInserted(position);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        initRecyclerView();


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

    }


    private void initRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.chat_recycler_view);

        recyclerViewAdapter = new RecyclerViewAdapter(this, messageUsernames, messageTexts);

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void writeNewMessage(String senderUsername, String text) {
        Message msg = new Message(senderUsername, text);

        String messageId = String.valueOf(counter);

        databaseRef.child("Gruppen").child(Gruppenname).child(messageId).setValue(msg);

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
                    startActivity(new Intent(ChatActivity.this, LoginActivity.class));

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
