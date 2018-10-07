package com.selbstfindung.guflash.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.selbstfindung.guflash.R;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
        buttonSetTime = (Button) findViewById(R.id.create_event_set_time_button);
        textViewDate = (TextView) findViewById(R.id.create_event_date_text_view);
        textViewTime = (TextView) findViewById(R.id.create_event_time_text_view);
        editTextMinUsers = (EditText) findViewById(R.id.create_event_min_users);
        editTextMaxUsers = (EditText) findViewById(R.id.create_event_max_users);
        
        // Get Current Date and Time
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        
        showDateAndTime();
        
        buttonSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                
                                // remember this
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;
    
                                // show to user
                                showDateAndTime();
    
                                // now launch Time Picker Dialog
                                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    
                                                // remember this
                                                mHour = hourOfDay;
                                                mMinute = minute;
                    
                                                // show to user
                                                showDateAndTime();
                    
                                            }
                                        }, mHour, mMinute, true);
                                timePickerDialog.show();
                    
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
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
                
                if (checkEventName(eventNameString) &&// mit dieser hierarchie wird der user über falsche daten informiert:
                        checkEventDescription(eventDescriptionString) &&
                        checkLocation(lat, lng, locationName, locationAddress) &&
                        checkTime(mYear, mMonth, mDay, mHour, mMinute) &&
                        checkMinUsers(minUsersString) &&
                        checkMaxUsers(maxUsersString, minUsersString)
                        ) {

                    // neue gruppe in datenbank anlegen
                    DatabaseReference newEventRef = mRef.child("events").push();
    
                    // userIDs:
                    ArrayList<String> userIDs = new ArrayList<>();

                    userIDs.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    // werte ausfüllen
                    
                    newEventRef.child("name").setValue(eventNameString);
                    newEventRef.child("description").setValue(eventDescriptionString);
                    newEventRef.child("users").setValue(userIDs);
                    newEventRef.child("min_members").setValue(Integer.parseInt(minUsersString));
                    newEventRef.child("max_members").setValue(Integer.parseInt(maxUsersString));
                    
                    DatabaseReference placeRef = newEventRef.child("place");
                    placeRef.child("latitude").setValue(lat);
                    placeRef.child("longitude").setValue(lng);
                    placeRef.child("name").setValue(locationName);
                    placeRef.child("address").setValue(locationAddress);
                    
                    DatabaseReference timeRef = newEventRef.child("time");
                    timeRef.child("year").setValue(mYear);
                    timeRef.child("month").setValue(mMonth);
                    timeRef.child("day").setValue(mDay);
                    timeRef.child("hour").setValue(mHour);
                    timeRef.child("minute").setValue(mMinute);
                    
                    // als letztes: READY-child adden!
                    // -> erst wenn dieses child da ist, darf es verarbeitet werden
                    newEventRef.child("READY").setValue("READY");
                    

                    // zurück zur EventActivity
                    finish();

                }
            }
        });
    }
    
    private void showDateAndTime() {
        
        Calendar calendar = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute);
        String dateString = DateUtils.formatDateTime(getApplicationContext(), calendar.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE);
        String timeString = DateUtils.formatDateTime(getApplicationContext(), calendar.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME);
    
        textViewDate.setText(dateString);
        textViewTime.setText(timeString);
        
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
                int n = Integer.parseInt(s);
                
                if (n<3){
                    Snackbar.make(parentLayout, "Es müssen mindestens 3 Teilnehmer sein!", Snackbar.LENGTH_SHORT).show();
                    return false;
                }
                
            } catch (NumberFormatException e) {
                Snackbar.make(parentLayout, "Gib eine gültige Mindestanzahl von Teilnehmern ein!", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }
    
    private boolean checkMaxUsers(String s, String minUsersString) {
        if (s.equals("")) {
            Snackbar.make(parentLayout, "Gib eine Höchstzahl von Teilnehmern ein!", Snackbar.LENGTH_SHORT).show();
            return false;
        } else {
            try {
                int nMax = Integer.parseInt(s);
                int nMin = Integer.parseInt(minUsersString);
                
                if (nMax<nMin) {
                    Snackbar.make(parentLayout, "Gib eine gültige Höchstzahl von Teilnehmern ein!", Snackbar.LENGTH_SHORT).show();
                    return false;
                }
                
            } catch (NumberFormatException e) {
                Snackbar.make(parentLayout, "Gib eine gültige Höchstzahl von Teilnehmern ein!", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }
    
    private boolean checkLocation(double lat, double lng, String locationName, String locationAddress) {
        if (locationName == null || locationAddress == null) {// just basic check if placepicker returned anything
            Snackbar.make(parentLayout, "Wähle einen Ort!", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        // assuming that lat & lng have been set by the place picker (since even the strings have been set)
        return true;
    }

    private boolean checkTime(int year, int month, int day, int hour, int minute) {
        
        Calendar now = Calendar.getInstance();
        Calendar eventTime = new GregorianCalendar();
        eventTime.set(year, month, day, hour, minute);
        
        if (now.before(eventTime))
            return true;
        
        Snackbar.make(parentLayout, "Die gewählte Zeit liegt in der Vergangenheit!", Snackbar.LENGTH_SHORT).show();
        return false;
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        //save easy ones
        outState.putString("savedeventNameString", editTextEventName.getText().toString());
        outState.putString("savedeventDescriptionString", editTextEventDescription.getText().toString());
        outState.putString("savedminUsersString", editTextMinUsers.getText().toString());
        outState.putString("savedmaxUsersString", editTextMaxUsers.getText().toString());

        //save time
        outState.putInt("savedYear", mYear);
        outState.putInt("savedMonth", mMonth);
        outState.putInt("savedDay", mDay);
        outState.putInt("savedHour", mHour);
        outState.putInt("savedMinute", mMinute);

        //save place
        outState.putDouble("savedlat", lat);
        outState.putDouble("savedlng", lng);
        outState.putString("savedlocationName", locationName);
        outState.putString("savedlocationAddress", locationAddress);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        editTextEventName.setText(savedInstanceState.getString("savedeventNameString"));
        editTextEventDescription.setText(savedInstanceState.getString("savedeventDescriptionString"));
        editTextMinUsers.setText(savedInstanceState.getString("savedminUsersString"));
        editTextMaxUsers.setText(savedInstanceState.getString("savedmaxUsersString"));

        mYear = savedInstanceState.getInt("savedYear");
        mMonth = savedInstanceState.getInt("savedMonth");
        mDay = savedInstanceState.getInt("savedDay");
        mHour = savedInstanceState.getInt("savedHour");
        mMinute = savedInstanceState.getInt("savedMinute");

        showDateAndTime();

        lat = savedInstanceState.getDouble("savedlat");
        lng = savedInstanceState.getDouble("savedlng");
        locationName = savedInstanceState.getString("savedlocationName");
        locationAddress = savedInstanceState.getString("savedlocationAddress");

        if(locationName!=null) {

            textViewSelectedLocationName.setText(locationName);
            textViewSelectedLocationAddress.setText(locationAddress);
            layoutSelectedLocation.setVisibility(View.VISIBLE);
        }

    }
}
