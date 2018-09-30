package com.selbstfindung.guflash.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
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

public class JoinPopupActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";

    private String eventId;
    DatabaseReference eventRef;
    
    // GUI
    TextView eventNameTextView;
    TextView eventDescriptionTextView;

    private User user;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private DatabaseReference groupRef;
    
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

        eventId = getIntent().getStringExtra(ChatActivity.EXTRA_MESSAGE_GRUPPEN_ID);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        groupRef = databaseRef.child("events").child(eventId);

        user = new User(firebaseUser.getUid(), new User.Callback() {
            @Override
            public void onProfileChanged() {}

            @Override
            public void onLoadingFailed() {}
        });
        
        // initialize views
        eventNameTextView = (TextView) findViewById(R.id.join_popup_event_name);
        eventDescriptionTextView = (TextView) findViewById(R.id.join_popup_event_description);
        
        
        findViewById(R.id.join_popup_join_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user.addEventID(eventId);

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

                Intent intent = new Intent(getBaseContext(), ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_MESSAGE_GRUPPEN_ID, eventId);
                startActivity(intent);
            
            }
        });
        findViewById(R.id.join_popup_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        

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
