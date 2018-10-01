package com.selbstfindung.guflash.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.selbstfindung.guflash.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {
    
    private static final String TAG = "MONTAG";
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int TIME_PICKER_REQUEST = 2;
    
    private DatabaseReference mRef;
    
    // views
    private EditText editTextEventName;
    private EditText editTextEventDescription;
    private Button buttonStartPlacePicker;
    private LinearLayout layoutSelectedLocation;
    private TextView textViewSelectedLocationName;
    private TextView textViewSelectedLocationAddress;
    private TextView textViewDate;
    private Button buttonSetDate;
    private TextView textViewTime;
    private Button buttonSetTime;
    private EditText editTextMinUsers;
    private EditText editTextMaxUsers;
    
    // for snackbar:
    private View parentLayout;
    
    // daten über das zu erstellende event werden in diesen feldern gesammelt:
    double lat;
    double lng;
    String locationName;
    String locationAddress;
    private int mYear, mMonth, mDay, mHour, mMinute;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        
        parentLayout = findViewById(android.R.id.content);

        mRef = FirebaseDatabase.getInstance().getReference();

        init();
    }

    private void init() {

        setTitle("Event erstellen");
        
        // get views
        editTextEventName = (EditText) findViewById(R.id.create_group_event_name_edit_text);
        editTextEventDescription = (EditText) findViewById(R.id.create_group_description_edit_text);
        buttonStartPlacePicker = (Button) findViewById(R.id.create_event_place_picker_button);
        layoutSelectedLocation = (LinearLayout) findViewById(R.id.create_event_selected_location_layout);
        textViewSelectedLocationName = (TextView) findViewById(R.id.create_event_selected_location_name);
        textViewSelectedLocationAddress = (TextView) findViewById(R.id.create_event_selected_location_address);
        buttonSetDate = (Button) findViewById(R.id.create_event_set_date_button);
        textViewDate = (TextView) findViewById(R.id.create_event_date_text_view);
        buttonSetTime = (Button) findViewById(R.id.create_event_set_time_button);
        textViewTime = (TextView) findViewById(R.id.create_event_time_text_view);
        editTextMinUsers = (EditText) findViewById(R.id.create_event_min_users);
        editTextMaxUsers = (EditText) findViewById(R.id.create_event_max_users);
        
        buttonSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
    
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                
                                // remember this
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;
                                
                                // show to user
                                if(dayOfMonth<10&&monthOfYear<10){textViewDate.setText("0"+dayOfMonth + ".0" + (monthOfYear + 1) + "." + year);}
                                else if(dayOfMonth<10){textViewDate.setText("0"+dayOfMonth + "." + (monthOfYear + 1) + "." + year);}
                                else if(monthOfYear<10){textViewDate.setText(dayOfMonth + ".0" + (monthOfYear + 1) + "." + year);}
                                else{textViewDate.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year);}
                    
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        
        buttonSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
    
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                
                                // remember this
                                mHour = hourOfDay;
                                mMinute = minute;
                                
                                // show to user
                                if(minute<10&&hourOfDay<10) {textViewTime.setText("0"+hourOfDay + ":0" + minute);}
                                else if(minute<10) {textViewTime.setText(hourOfDay + ":0" + minute);}
                                else if(hourOfDay<10) {textViewTime.setText("0"+hourOfDay + ":" + minute);}
                                else {textViewTime.setText(hourOfDay + ":" + minute);}
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            
            }
        });
        
        buttonStartPlacePicker.setOnClickListener(new View.OnClickListener() {
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
            public void onClick(View v) {
                
                // Felder auslesen
                String eventNameString = editTextEventName.getText().toString();
                String eventDescriptionString = editTextEventDescription.getText().toString();
                String minUsersString = editTextMinUsers.getText().toString();
                String maxUsersString = editTextMaxUsers.getText().toString();
                
                if (
                        checkEventName(eventNameString) &&
                        checkEventDescription(eventDescriptionString) &&
                        checkMinUsers(minUsersString) &&
                        checkMaxUsers(maxUsersString)
                        ) {

                    // neue gruppe in datenbank anlegen
                    DatabaseReference newGroupRef = mRef.child("events").push();
    
                    // userIDs:
                    ArrayList<String> userIDs = new ArrayList<>();

                    // werte ausfüllen
                    
                    newGroupRef.child("name").setValue(eventNameString);
                    newGroupRef.child("description").setValue(eventDescriptionString);
                    newGroupRef.child("users").setValue(userIDs);
                    newGroupRef.child("min_members").setValue(Integer.parseInt(minUsersString));
                    newGroupRef.child("max_members").setValue(Integer.parseInt(maxUsersString));
                    
                    DatabaseReference placeRef = newGroupRef.child("place");
                    placeRef.child("latitude").setValue(lat);
                    placeRef.child("longitude").setValue(lng);
                    placeRef.child("name").setValue(locationName);
                    placeRef.child("address").setValue(locationAddress);
                    
                    DatabaseReference timeRef = newGroupRef.child("time");
                    timeRef.child("year").setValue(mYear);
                    timeRef.child("month").setValue(mMonth);
                    timeRef.child("day").setValue(mDay);
                    timeRef.child("hour").setValue(mHour);
                    timeRef.child("minute").setValue(mMinute);

                    // zurück zur EventActivity
                    finish();

                }
            }
        });
    }
    
    private boolean checkEventName(String s) {
        if (s.equals("")) {
            Snackbar.make(parentLayout, "Gib einen Eventnamen ein!", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    
    private boolean checkEventDescription(String s) {
        if (s.equals("")) {
            Snackbar.make(parentLayout, "Gib eine Beschreibung ein!", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    
    private boolean checkMinUsers(String s) {
        if (s.equals("")) {
            Snackbar.make(parentLayout, "Gib eine Mindestanzahl von Teilnehmern ein!", Snackbar.LENGTH_SHORT).show();
            return false;
        } else {
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
                Snackbar.make(parentLayout, "Gib eine richtige Mindestanzahl von Teilnehmern ein!", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }
    
    private boolean checkMaxUsers(String s) {
        if (s.equals("")) {
            Snackbar.make(parentLayout, "Gib eine Höchstzahl von Teilnehmern ein!", Snackbar.LENGTH_SHORT).show();
            return false;
        } else {
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
                Snackbar.make(parentLayout, "Gib eine richtige Höchstzahl von Teilnehmern ein!", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Erhalte Result "+requestCode+" "+resultCode);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                
                Place place = PlacePicker.getPlace(this, data);
                CharSequence name = place.getName();
                CharSequence addr = place.getAddress();

                // remember this
                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;
                if (name != null) locationName = name.toString();
                if (addr != null) locationAddress = addr.toString();
                
                // show to user
                if (name!= null)
                    textViewSelectedLocationName.setText(name);
                if (name != null)
                    textViewSelectedLocationAddress.setText(place.getAddress());
                layoutSelectedLocation.setVisibility(View.VISIBLE);// make layout visible
                
            }
        }
    }
}
