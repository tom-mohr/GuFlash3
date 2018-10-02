package com.selbstfindung.guflash.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selbstfindung.guflash.R;

public class ClosedChatActivity extends AppCompatActivity
{

    private static final String TAG = "MONTAG";

    public static final String EXTRA_MESSAGE_GRUPPEN_ID = "GRUPPEN_ID";
    public static final String EXTRA_MESSAGE_REASON = "REASON";

    private String groupID;
    private String reason;

    private EditText chatTextInput;
    private TextView closingInfo;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseRef;
    private DatabaseReference groupRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_closed);


        Intent intent = getIntent();
        groupID = intent.getStringExtra(EXTRA_MESSAGE_GRUPPEN_ID);
        reason = intent.getStringExtra(EXTRA_MESSAGE_REASON);

        init();
    }


    private void init()
    {

        chatTextInput = (EditText) findViewById(R.id.edittext_chatbox);
        closingInfo = (TextView) findViewById(R.id.closing_information);

        databaseRef = FirebaseDatabase.getInstance().getReference();

        // create reference for this group
        groupRef = databaseRef.child("events").child(groupID);


        chatTextInput.setFocusable(false);
        int fehlendeTeilnehmer = Integer.parseInt(reason.substring(10));
        if(fehlendeTeilnehmer>1) {
            closingInfo.setText("Die Gruppe hat noch nicht die Mindestteilnehmerzahl erreicht, es fehlen noch "+fehlendeTeilnehmer+" Teilnehmer");
        }else{
            closingInfo.setText("Die Gruppe hat noch nicht die Mindestteilnehmerzahl erreicht, es fehlt noch "+fehlendeTeilnehmer+" Teilnehmer");
        }


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
    }
}
