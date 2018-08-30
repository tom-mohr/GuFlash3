package com.selbstfindung.guflash.Activities;

import android.app.ActionBar;
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
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selbstfindung.guflash.Message;
import com.selbstfindung.guflash.R;
import com.selbstfindung.guflash.RecyclerViewAdapter;
import com.selbstfindung.guflash.User;

import java.util.ArrayList;


public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";

    public static final String EXTRA_MESSAGE_GRUPPEN_ID = "GRUPPEN_ID";

    private String groupID;

    private EditText chatTextInput;

    private ArrayList<String> messageUsernames = new ArrayList<>();
    private ArrayList<String> messageTexts = new ArrayList<>();
    RecyclerViewAdapter recyclerViewAdapter;// need instance for newly added messages

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private DatabaseReference databaseRef;
    private DatabaseReference groupRef;

    private com.selbstfindung.guflash.User User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.d(TAG, "ChatActivity onCreate()");

        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        groupID = intent.getStringExtra(EXTRA_MESSAGE_GRUPPEN_ID);

        setupFirebase();
        init();
    }

    private void init() {

        chatTextInput = (EditText) findViewById(R.id.edittext_chatbox);

        databaseRef = FirebaseDatabase.getInstance().getReference();

        // create reference for this group
        groupRef = databaseRef.child("groups").child(groupID);

        User = new User(mUser.getUid());
        Log.d(TAG, "AAAAAAAAAAAAAAAAAAAAAAAA "+User.getUsername());

        // listen to database changes (new messages)
        groupRef.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message msg = dataSnapshot.getValue(Message.class);

                // absender und inhalt den arrays hinzufügen
                messageUsernames.add(msg.senderUsername);
                messageTexts.add(msg.text);

                Log.d(TAG, "Message added: "+msg.senderUsername+": "+msg.text);

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

        // listen to database changes (groupname)
        groupRef.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String newGroupName = dataSnapshot.getValue(String.class);

                setTitle(newGroupName);// set group name as title in action bar

                Log.d(TAG, "group name changed to "+newGroupName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });


        initRecyclerView();


        // onclick für "senden" button
        ((ImageButton) findViewById(R.id.chat_send_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Log.d(TAG, "senden gedrückt "+mUser.getUid());
                Log.d(TAG, "senden gedrückt "+User.getUsername());

                // text string vom input feld kriegen
                String text = chatTextInput.getText().toString();

                if (! text.equals("")) {

                    // input feld leermachen
                    chatTextInput.setText("");

                    // send message
                    writeNewMessage(User.getUsername(), text);
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
        Log.d(TAG, "writing new Message");
        DatabaseReference newMessageRef = groupRef.child("messages").push();
        newMessageRef.setValue(new Message(senderUsername, text));
    }


    private void setupFirebase() {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {

                mUser = firebaseAuth.getCurrentUser();

                if (mUser == null) {
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
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
