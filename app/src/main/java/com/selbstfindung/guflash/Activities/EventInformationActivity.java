package com.selbstfindung.guflash.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selbstfindung.guflash.EventInfo;
import com.selbstfindung.guflash.MemberRecyclerViewAdapter;
import com.selbstfindung.guflash.R;
import com.selbstfindung.guflash.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class EventInformationActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";

    // GUI
    TextView eventNameTextView,
            eventDateTextView,
            eventTimeTextView,
            eventDescriptionTextView,
            eventMembersAmountTextView,
            eventMaxMembersAmountTextView,
            eventPlaceNameTextView,
            eventAddressTextView;

    // Firebase
    private String eventId;
    DatabaseReference eventRef;
    private User user;
    ArrayList<String> userIDs;

    // info über das event
    private long currentEventMemberCount;
    private long currentMaxEventMemberCount;
    private long currentMinEventMemberCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_information);

        Log.d(TAG, "Popup aufgerufen");


        // initialize views
        eventNameTextView = (TextView) findViewById(R.id.event_information_event_name);
        eventDescriptionTextView = (TextView) findViewById(R.id.event_information_event_description);
        eventDateTextView = (TextView) findViewById(R.id.event_information_event_date);
        eventTimeTextView = (TextView) findViewById(R.id.event_information_event_time);
        eventPlaceNameTextView = (TextView) findViewById(R.id.event_information_place_name);
        eventAddressTextView = (TextView) findViewById(R.id.event_information_event_place_address);
        eventMembersAmountTextView = (TextView) findViewById(R.id.event_information_members_amount);
        eventMaxMembersAmountTextView = (TextView) findViewById(R.id.event_information_max_members_amount);

        // von welcher gruppe soll das popup informationen anzeigen?
        eventId = getIntent().getStringExtra(ChatActivity.EXTRA_MESSAGE_GRUPPEN_ID);

        // Firebase stuff
        eventRef = FirebaseDatabase.getInstance().getReference().child("events").child(eventId);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        user = new User(userId, null);

        // listen for changes
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {

                // fetch data

                EventInfo eventInfo = new EventInfo(ds);

                userIDs = eventInfo.getUserIds();

                initRecyclerView();

                String eventName = (String) ds.child("name").getValue();
                String eventDescription = (String) ds.child("description").getValue();

                String eventPlaceName = ds.child("place").child("name").getValue(String.class);
                String eventPlaceAddress = ds.child("place").child("address").getValue(String.class);

                currentEventMemberCount = ds.child("users").getChildrenCount();
                currentMaxEventMemberCount = (long) ds.child("max_members").getValue();
                currentMinEventMemberCount = (long) ds.child("min_members").getValue();

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.member_recycler_view);
        MemberRecyclerViewAdapter adapter = new MemberRecyclerViewAdapter(this, userIDs);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
