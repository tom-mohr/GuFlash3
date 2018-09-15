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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selbstfindung.guflash.ChatRecyclerViewAdapter;
import com.selbstfindung.guflash.Message;
import com.selbstfindung.guflash.R;
import com.selbstfindung.guflash.User;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";

    public static final String EXTRA_MESSAGE_GRUPPEN_ID = "GRUPPEN_ID";

    private String groupID;

    private EditText chatTextInput;

    private List<Message> messageList;
    ChatRecyclerViewAdapter recyclerViewAdapter;// need instance for newly added messages

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseRef;
    private DatabaseReference groupRef;

    private com.selbstfindung.guflash.User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.d(TAG, "ChatActivity onCreate()");


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {// user ist angemeldet

            ActionBar actionBar = getActionBar();
            if (actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(true);

            Intent intent = getIntent();
            groupID = intent.getStringExtra(EXTRA_MESSAGE_GRUPPEN_ID);

            init();

        } else {
            // user ist aus irgendeinem grund nicht angemeldet,
            // aber trotzdem in der chat activity gelandet.
            // das sollte eigentlich nicht vorkommen.
            finish();// erstmal zurück in die navigation activity
        }
    }

    private void init() {
        
        messageList = new ArrayList<>();

        chatTextInput = (EditText) findViewById(R.id.edittext_chatbox);

        databaseRef = FirebaseDatabase.getInstance().getReference();

        // create reference for this group
        groupRef = databaseRef.child("groups").child(groupID);

        user = new User(firebaseUser.getUid(), new User.Callback() {
            @Override
            public void onProfileChanged() {

            }

            @Override
            public void onLoadingFailed() {

            }
        });

        //Fügt User zum Event hinzu wenn er nicht eingetragen ist
        groupRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()!=0)
                {
                    boolean userVorhanden = false;

                    for(DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        if(ds.getValue().toString().equals(user.getId()))
                        {
                            userVorhanden = true;
                        }
                    }

                    if(!userVorhanden)
                    {
                        groupRef.child("users").child(user.getId()).setValue(user.getId());
                    }
                }
                else
                {
                    groupRef.child("users").child(user.getId()).setValue(user.getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // listen to database changes (new messages)
        groupRef.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message msg = dataSnapshot.getValue(Message.class);
                
                messageList.add(msg);
                
                Log.d(TAG, "Message added: "+msg.getSenderUserID()+": "+msg.getText());

                // notify recyclerViewAdapter that new message was added
                // but only, if it was already initialized
                // see: https://stackoverflow.com/questions/27845069/add-a-new-item-to-recyclerview-programmatically
                if (recyclerViewAdapter != null) {

                    int position = messageList.size() - 1;// last index in list
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
        ((Button) findViewById(R.id.chat_send_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // text string vom input feld kriegen
                String text = chatTextInput.getText().toString();

                if (! text.equals("")) {

                    // input feld leermachen
                    chatTextInput.setText("");

                    // send message
                    writeNewMessage(user, text);
                }
            }
        });

    }


    private void initRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.chat_recycler_view);

        recyclerViewAdapter = new ChatRecyclerViewAdapter(this, messageList, user.getId());

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void writeNewMessage(User user, String text) {
        Log.d(TAG, "writing new Message");
    
        String senderUserID = user.getId();
        long time = new GregorianCalendar().getTime().getTime();
        
        DatabaseReference newMessageRef = groupRef.child("messages").push();
        newMessageRef.setValue(new Message(senderUserID, text, time));
    }


    private void initAuthListener() {

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {

                firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser ==null) {
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