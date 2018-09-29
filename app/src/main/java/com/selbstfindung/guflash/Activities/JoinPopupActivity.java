package com.selbstfindung.guflash.Activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.selbstfindung.guflash.R;
import com.selbstfindung.guflash.RecyclerViewAdapterEvent;

public class JoinPopupActivity extends AppCompatActivity {
    
    private String eventId;
    DatabaseReference eventRef;
    
    // GUI
    TextView eventNameTextView;
    TextView eventDescriptionTextView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_popup);
    
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        int width = (int) (0.8 * dm.widthPixels);
        int height = (int) (0.8 * dm.heightPixels);
        
        getWindow().setLayout(width, height);
        
        // initialize views
        eventNameTextView = (TextView) findViewById(R.id.join_popup_event_name);
        eventDescriptionTextView = (TextView) findViewById(R.id.join_popup_event_description);
        
        
        findViewById(R.id.join_popup_join_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            
            }
        });
        findViewById(R.id.join_popup_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        eventId = getIntent().getStringExtra(ChatActivity.EXTRA_MESSAGE_GRUPPEN_ID);
        eventRef = FirebaseDatabase.getInstance().getReference().child("events").child(eventId);
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String eventName = (String) dataSnapshot.child("name").getValue();
                String eventDescription = (String) dataSnapshot.child("description").getValue();
                
                eventNameTextView.setText(eventName);
                eventDescriptionTextView.setText(eventDescription);
            }
    
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
        
            }
        });
    }
}
