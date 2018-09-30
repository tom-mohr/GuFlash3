package com.selbstfindung.guflash.Activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.selbstfindung.guflash.R;

import java.util.ArrayList;

public class CreateEventActivity extends AppCompatActivity {
    
    private static final String TAG = "MONTAG";
    private static final int PLACE_PICKER_REQUEST = 1;
    
    private DatabaseReference mRef;
    private RelativeLayout layoutAddLocation;
    private RelativeLayout layoutSelectedLocation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mRef = FirebaseDatabase.getInstance().getReference();

        init();
    }

    private void init() {

        setTitle("Event erstellen");
        
        
        ((RelativeLayout) findViewById(R.id.create_event_set_time_layout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateEventActivity.this, TimePickingActivity.class));
            }
        });
        
    
        layoutAddLocation = (RelativeLayout) findViewById(R.id.create_event_add_location_layout);
        layoutSelectedLocation = (RelativeLayout) findViewById(R.id.create_event_selected_location_layout);
        
        layoutAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // starte Place Picker!
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(CreateEventActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Toast.makeText(CreateEventActivity.this, "Place Picker konnte nicht gestartet werden. (behebbar)", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(CreateEventActivity.this, "Place Picker konnte nicht gestartet werden.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        

        final EditText eventName = (EditText) findViewById(R.id.create_group_event_name_edit_text);
        final EditText description = (EditText) findViewById(R.id.create_group_description_edit_text);
        Button cancelButton = (Button) findViewById(R.id.create_group_cancel_button);
        Button okButton = (Button) findViewById(R.id.create_group_confirm_button);

        // onclick für "abbrechen" Button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // onclick für "Neue Gruppe erstellen"
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                
                EditText minUsersEditText = (EditText) findViewById(R.id.create_event_min_users);
                EditText maxUsersEditText = (EditText) findViewById(R.id.create_event_max_users);
                
                // Felder auslesen...
                String eventNameString = eventName.getText().toString();
                String descriptionString = description.getText().toString();
                int maxUsers = Integer.parseInt(maxUsersEditText.getText().toString());
                

                if (checkGroupName(eventNameString)) {

                    // neue gruppe in datenbank anlegen
                    DatabaseReference newGroupRef = mRef.child("events").push();
    
                    // userIDs:
                    ArrayList<String> userIDs = new ArrayList<>();

                    // werte ausfüllen
                    newGroupRef.child("name").setValue(eventNameString);
                    newGroupRef.child("description").setValue(descriptionString);
                    newGroupRef.child("users").setValue(userIDs);
                    newGroupRef.child("max_members").setValue(maxUsers);

                    // zurück zur EventActivity
                    finish();

                } else {
                    Snackbar.make(v, "Gib einen Gruppennamen ein!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkGroupName(String name) {
        return !name.equals("");
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                
                Place place = PlacePicker.getPlace(this, data);
                
                ((TextView) findViewById(R.id.create_event_selected_location_name)).setText(place.getName());
                ((TextView) findViewById(R.id.create_event_selected_location_address)).setText(place.getAddress());
                
                layoutAddLocation.setVisibility(View.GONE);
                layoutSelectedLocation.setVisibility(View.VISIBLE);
            }
        }
    }
}
