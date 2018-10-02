package com.selbstfindung.guflash.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.selbstfindung.guflash.R;
import com.selbstfindung.guflash.RecyclerViewAdapterEvent;
import com.selbstfindung.guflash.User;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class JoinPopupActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";

    public static final String EXTRA_MESSAGE_MIN_TEILNEHMER = "MIN_TEILNEHMER";
    
    // GUI
    TextView eventNameTextView,
            eventDateTextView,
            eventTimeTextView,
            eventDescriptionTextView,
            eventMembersAmountTextView,
            eventMaxMembersAmountTextView,
            eventPlaceNameTextView,
            eventAddressTextView;
    Button joinButton;
    
    // Firebase
    private String eventId;
    DatabaseReference eventRef;
    private User user;
    
    // info über das event
    private int minTeilnehmer;
    private long currentEventMemberCount;
    private long currentMaxEventMemberCount;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_popup);

        Log.d(TAG, "Popup aufgerufen");
    
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        int width = (int) (0.8 * dm.widthPixels);
        int height = (int) (0.8 * dm.heightPixels);
        
        getWindow().setLayout(width, height);
        
        // initialize views
        eventNameTextView = (TextView) findViewById(R.id.join_popup_event_name);
        eventDescriptionTextView = (TextView) findViewById(R.id.join_popup_event_description);
        eventDateTextView = (TextView) findViewById(R.id.join_popup_event_date);
        eventTimeTextView = (TextView) findViewById(R.id.join_popup_event_time);
        eventPlaceNameTextView = (TextView) findViewById(R.id.join_popup_event_place_name);
        eventAddressTextView = (TextView) findViewById(R.id.join_popup_event_place_address);
        eventMembersAmountTextView = (TextView) findViewById(R.id.join_popup_members_amount);
        eventMaxMembersAmountTextView = (TextView) findViewById(R.id.join_popup_max_members_amount);
        joinButton = (Button) findViewById(R.id.join_popup_join_button);
        
        // von welcher gruppe soll das popup informationen anzeigen?
        eventId = getIntent().getStringExtra(ChatActivity.EXTRA_MESSAGE_GRUPPEN_ID);
        minTeilnehmer = Integer.parseInt(getIntent().getStringExtra(EXTRA_MESSAGE_MIN_TEILNEHMER));
        
        // Firebase stuff
        eventRef = FirebaseDatabase.getInstance().getReference().child("events").child(eventId);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        user = new User(userId, null);
        
        // listen for changes
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                
                // fetch data
                
                String eventName = (String) ds.child("name").getValue();
                String eventDescription = (String) ds.child("description").getValue();
                
                String eventPlaceName = ds.child("place").child("name").getValue(String.class);
                String eventPlaceAddress = ds.child("place").child("address").getValue(String.class);
                
                currentEventMemberCount = ds.child("users").getChildrenCount();
                currentMaxEventMemberCount = (long) ds.child("max_members").getValue();
    
                // time formatting
                DataSnapshot timeRef = ds.child("time");
                int year = ((Long) timeRef.child("year").getValue()).intValue();
                int month = ((Long) timeRef.child("month").getValue()).intValue();
                int day = ((Long) timeRef.child("day").getValue()).intValue();
                int hour = ((Long) timeRef.child("hour").getValue()).intValue();
                int minute = ((Long) timeRef.child("minute").getValue()).intValue();
                Calendar calendar = new GregorianCalendar(year, month, day, hour, minute);
                String dateString = DateUtils.formatDateTime(getApplicationContext(), calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE);
                String timeString = DateUtils.formatDateTime(getApplicationContext(), calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);



                // make it visible
                eventNameTextView.setText(eventName);
                eventDescriptionTextView.setText(eventDescription);
                eventMembersAmountTextView.setText(String.valueOf(currentEventMemberCount));
                eventMaxMembersAmountTextView.setText(String.valueOf(currentMaxEventMemberCount));
                eventDateTextView.setText(dateString);
                eventTimeTextView.setText(timeString);
                eventPlaceNameTextView.setText(eventPlaceName);
                eventAddressTextView.setText(eventPlaceAddress);
                
                // wenn die maximale user anzahl erreicht wurde, deaktiviere den join-button
                if (currentEventMemberCount >= currentMaxEventMemberCount)
                    joinButton.setVisibility(View.INVISIBLE);
                else
                    joinButton.setVisibility(View.VISIBLE);// falls user wieder leaven, reaktiviere
                
            }
    
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        
        // onclick listeners
        
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                // user clicked "JOIN"
                
                // ist ein platz frei?
                if (currentEventMemberCount < currentMaxEventMemberCount) {// diese daten werden ständing aktualisiert
                    
                    // add user to the "users" list of this event in the database
                    eventRef.child("users").child(user.getId()).setValue(user.getId());
    
                    user.addEventID(eventId);// add event to "my events"
    
                    // open the chat (of that event) and finish this popup-activity
                    if(minTeilnehmer>currentEventMemberCount+1)
                    {
                        Intent intent = new Intent(JoinPopupActivity.this, ClosedChatActivity.class);
                        intent.putExtra(ChatActivity.EXTRA_MESSAGE_GRUPPEN_ID, eventId);
                        intent.putExtra(ClosedChatActivity.EXTRA_MESSAGE_REASON, "Teilnehmer"+(minTeilnehmer-currentEventMemberCount-1));
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(JoinPopupActivity.this, ChatActivity.class);
                        intent.putExtra(ChatActivity.EXTRA_MESSAGE_GRUPPEN_ID, eventId);
                        startActivity(intent);
                    }
                    finish();// beende das popup (damit man es nicht wieder sieht wenn man den chat verlässt)
                }
            
            }
        });
        findViewById(R.id.join_popup_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
