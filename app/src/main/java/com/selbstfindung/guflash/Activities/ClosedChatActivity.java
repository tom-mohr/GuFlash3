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

    public static final String EXTRA_MESSAGE_EVENT_ID = "EVENT_ID";
    public static final String EXTRA_MESSAGE_REASON = "REASON";

    private String eventID;
    private String reason;

    private EditText chatTextInput;
    private TextView closingInfo;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseRef;
    private DatabaseReference eventRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_closed);


        Intent intent = getIntent();
        eventID = intent.getStringExtra(EXTRA_MESSAGE_EVENT_ID);
        reason = intent.getStringExtra(EXTRA_MESSAGE_REASON);

        init();
    }


    private void init()
    {

        chatTextInput = (EditText) findViewById(R.id.edittext_chatbox);
        closingInfo = (TextView) findViewById(R.id.closing_information);

        databaseRef = FirebaseDatabase.getInstance().getReference();

        // create reference for this group
        eventRef = databaseRef.child("events").child(eventID);


        chatTextInput.setFocusable(false);
        /*
        final int fehlendeTeilnehmer = Integer.parseInt(reason.substring(10));
        if(fehlendeTeilnehmer>1) {
            closingInfo.setText("Die Gruppe hat noch nicht die Mindestteilnehmerzahl erreicht, es fehlen noch "+fehlendeTeilnehmer+" Teilnehmer");
        }else{
            closingInfo.setText("Die Gruppe hat noch nicht die Mindestteilnehmerzahl erreicht, es fehlt noch "+fehlendeTeilnehmer+" Teilnehmer");
        }
           */

        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String newGroupName = dataSnapshot.child("name").getValue(String.class);

                setTitle(newGroupName);// set group name as title in action bar

                Log.d(TAG, "group name changed to "+newGroupName);

                int minTeilnehmer = ((Long) dataSnapshot.child("min_members").getValue()).intValue();
                int currentTeilnehmer = (int) dataSnapshot.child("users").getChildrenCount();

                int fehlendeTeilnehmer = minTeilnehmer-currentTeilnehmer;

                if(fehlendeTeilnehmer>1) {
                    closingInfo.setText("Die Gruppe hat noch nicht die Mindestteilnehmerzahl erreicht, es fehlen noch "+fehlendeTeilnehmer+" Teilnehmer");
                }else if(fehlendeTeilnehmer==1){
                    closingInfo.setText("Die Gruppe hat noch nicht die Mindestteilnehmerzahl erreicht, es fehlt noch "+fehlendeTeilnehmer+" Teilnehmer");
                }else{
                    Intent intent = new Intent(ClosedChatActivity.this, ChatActivity.class);
                    intent.putExtra(ChatActivity.EXTRA_MESSAGE_GRUPPEN_ID, eventID);
                    startActivity(intent);
                    finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
