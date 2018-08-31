package com.selbstfindung.guflash.Activities;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.selbstfindung.guflash.R;

import java.util.ArrayList;

public class CreateGroupActivity extends AppCompatActivity {

    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mRef = FirebaseDatabase.getInstance().getReference();

        init();
    }

    private void init() {

        setTitle("Event erstellen");

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
                // Felder auslesen...

                String eventNameString = eventName.getText().toString();
                String descriptionString = description.getText().toString();
                // userIDs:
                ArrayList<String> userIDs = new ArrayList<>();

                if (checkGroupName(eventNameString)) {

                    // neue gruppe in datenbank anlegen
                    DatabaseReference newGroupRef = mRef.child("groups").push();

                    // werte ausfüllen
                    newGroupRef.child("name").setValue(eventNameString);
                    newGroupRef.child("description").setValue(descriptionString);
                    newGroupRef.child("users").setValue(userIDs);

                    // zurück zur GroupActivity
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
}
