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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class BoringActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";

    private EditText usernameTextInput;
    private TextView chat;
    private EditText chatTextInput;

    private String username = "anonym";

    /// for dev:
    private int counter;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boring);

        // get intent message (username)
        Intent intent = getIntent();
        username = intent.getStringExtra(UsernameActivity.EXTRA_MESSAGE_TAG);

        // get views

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

                // update UI
                chat.setText(chat.getText().toString() + msg.senderUsername + ": " + msg.text + "\n");
                Log.d(TAG, chat.getText().toString());

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

        // den buttons funktionen hinzufügen

        Button sendButton =  (Button) findViewById(R.id.button_chatbox_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // text string vom input feld kriegen
                String text = chatTextInput.getText().toString();

                if (! text.equals("")) {

                    // input feld leermachen
                    chatTextInput.setText("");

                    // send message
                    writeNewMessage(username, text);
                }
            }
        });

        Button deleteButton =  (Button) findViewById(R.id.button_delete_chat);
        deleteButton.setOnClickListener(new View.OnClickListener() {
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

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        databaseRef.child("users").child(userId).setValue(user);
    }
}
