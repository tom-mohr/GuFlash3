package com.selbstfindung.guflash.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selbstfindung.guflash.EventInfo;
import com.selbstfindung.guflash.R;
import com.selbstfindung.guflash.EventRecyclerViewAdapter;
import com.selbstfindung.guflash.User;

import java.util.ArrayList;

public class MyEventActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MONTAG";

    private ArrayList<EventInfo> eventInfos;
    private EventRecyclerViewAdapter eventRecyclerViewAdapter;

    String userId;
    String sortType;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myevent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // AUTO-GENERIERT VON ANDROID STUDIO:

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // eigener code:
                Intent intent = new Intent(MyEventActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // eigener Code:


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();


        setTitle("Events");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        sortType = getIntent().getStringExtra(NavigationActivity.EXTRA_MESSAGE_SORT_TYPE);
        //filterTime = getIntent().getStringExtra(NavigationActivity.EXTRA_MESSAGE_FILTER_TIME);
        //filterDistance = getIntent().getStringExtra(NavigationActivity.EXTRA_MESSAGE_FILTER_DISTANCE);

        if (sortType == null) {
            sortType = "Distance";
        }

        Log.d(TAG, "Es soll nach " + sortType + " sortiert werden");

        eventInfos = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.events_recycler_view);
        eventRecyclerViewAdapter = new EventRecyclerViewAdapter(this, eventInfos);
        recyclerView.setAdapter(eventRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // listen for changes to the "events" child
        FirebaseDatabase.getInstance().getReference().child("events").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                // new event added

                EventInfo eventInfo = checkEventInfo(dataSnapshot);


                if(eventInfo.containsUserID(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    if (eventInfo != null) {
                        if (sortType.equals("Time")) {
                            boolean kleinerAlsElement = false;
                            int counter = 0;
                            if (eventInfos.size() == 0) {
                                Log.d(TAG, "" + eventInfos.size());
                                eventInfos.add(eventInfo);
                                eventRecyclerViewAdapter.notifyItemInserted(0);
                            } else {
                                while (!kleinerAlsElement && counter < eventInfos.size()) {
                                    if (eventInfos.get(counter).getMillisTillEvent() >= eventInfo.getMillisTillEvent()) {
                                        eventInfos.add(counter, eventInfo);
                                        eventRecyclerViewAdapter.notifyItemInserted(counter);
                                        Log.d(TAG, "Event zur Liste an der Stelle " + (counter) + " hinzugefügt: " + eventInfo.name);
                                        kleinerAlsElement = true;
                                    } else {
                                        counter++;
                                    }
                                }
                                if (!kleinerAlsElement) {
                                    eventInfos.add(counter - 1, eventInfo);
                                    eventRecyclerViewAdapter.notifyItemInserted(counter - 1);
                                    Log.d(TAG, "Event zur Liste an der Stelle " + (counter - 1) + " hinzugefügt: " + eventInfo.name);
                                }
                            }

                        } else {
                            eventInfos.add(eventInfo);
                            eventRecyclerViewAdapter.notifyItemInserted(eventInfos.size() - 1);
                            Log.d(TAG, "Event zur Liste an der Stelle " + (eventInfos.size() - 1) + " hinzugefügt: " + eventInfo.name);
                        }

                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                EventInfo eventInfo = checkEventInfo(dataSnapshot);

                if(eventInfo.containsUserID(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    if (eventInfo != null) {

                        boolean isInList = false;
                        int index = 0;
                        for (EventInfo otherEventInfo : eventInfos) {
                            if (eventInfo.id.equals(otherEventInfo.id)) {
                                isInList = true;
                                break;
                            }
                            index++;
                        }

                        // wenn event in liste ist, aktualisiere es
                        if (isInList) {

                            // -> ersetze durch neue EventInfo
                            eventInfos.set(index, eventInfo);
                            eventRecyclerViewAdapter.notifyItemChanged(index);

                            Log.d(TAG, "Event aktualisiert: " + eventInfo.name);

                        } else {
                            // wahrscheinlich wurde erst jetzt das "READY"-child hinzugefügt

                            // -> füge eventInfo zu liste hinzu
                            eventInfos.add(eventInfo);
                            eventRecyclerViewAdapter.notifyItemInserted(eventInfos.size() - 1);

                            Log.d(TAG, "Event zur Liste hinzugefügt (warten auf READY): " + eventInfo.name);
                        }

                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private EventInfo checkEventInfo(DataSnapshot ds) {
        // returns EventInfo if everything is alright

        // hat dieses event schon das "READY"-child?
        // -> nur dann darf es verarbeitet werden
        if (ds.child("READY").getValue() != null) {

            EventInfo eventInfo = null;
            try {
                eventInfo = new EventInfo(ds);

            } catch (Exception e) {// NullPointerException oder DatabaseException
                // füge Event mit fehlerhaften Daten nicht hinzu

                Log.w(TAG, "Event-Daten sind fehlerhaft. Event-ID: " + ds.getKey() + "; Fehlermeldung: " + e.getMessage());
            }

            if (eventInfo != null) {

                // das event muss in der Zukunft stattfinden oder
                // vor weniger als 12 stunden gestartet sein
                if (eventInfo.getHoursTillEvent() > -12) {
                    return eventInfo;
                }
            }
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_sort_distance:
                if (item.isChecked()) {
                    // ignore
                } else {
                    // check
                    item.setChecked(true);

                    // apply this filter function

                    FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("sort").setValue("Distance");

                    Intent restartIntent = new Intent(MyEventActivity.this, MyEventActivity.class);
                    restartIntent.putExtra(NavigationActivity.EXTRA_MESSAGE_SORT_TYPE, "Distance");
                    finish();
                    startActivity(restartIntent);

                }
                return true;
            case R.id.filter_sort_time:
                if (item.isChecked()) {
                    // ignore
                } else {
                    // check
                    item.setChecked(true);

                    // apply this filter function

                    FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("sort").setValue("Time");

                    Intent restartIntent = new Intent(MyEventActivity.this, MyEventActivity.class);
                    restartIntent.putExtra(NavigationActivity.EXTRA_MESSAGE_SORT_TYPE, "Time");
                    finish();
                    startActivity(restartIntent);
                }
                return true;
            case R.id.filter_ignore_distance:
                if (item.isChecked()) {
                    // uncheck
                    item.setChecked(false);

                    // remove this filter option
                    // ... noch nicht implementiert
                } else {
                    // check
                    item.setChecked(true);

                    // apply this filter function
                }
                return true;
            case R.id.filter_ignore_time:
                if (item.isChecked()) {
                    // uncheck
                    item.setChecked(false);

                    // remove this filter option
                    // ... noch nicht implementiert
                } else {
                    // check
                    item.setChecked(true);

                    // apply this filter function
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // AUTO-GENERIERTE FUNKTIONEN FÜR NAVIGATION DRAWER:

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_events) {
            startActivity(new Intent(MyEventActivity.this, NavigationActivity.class));

        } else if (id == R.id.nav_favorite_events) {
            startActivity(new Intent(MyEventActivity.this, MyEventActivity.class));
            finish();

        } else if (id == R.id.nav_profile_settings) {
            startActivity(new Intent(MyEventActivity.this, ProfileActivity.class));

        } else if (id == R.id.nav_app_notifications) {
            startActivity(new Intent(MyEventActivity.this, NotificationActivity.class));

        } else if (id == R.id.nav_logout) {

            // ask for confirmation


            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("abmelden").setMessage("Willst du dich wirklich abmelden?");
            b.setIcon(R.drawable.ic_menu_lock);
            b.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    // sign out from firebase
                    FirebaseAuth.getInstance().signOut();

                    ///TODO: listener für erfolgreiches signout
                    ///  ->  https://developers.google.com/android/reference/com/google/firebase/auth/FirebaseAuth.IdTokenListener#onIdTokenChanged(com.google.firebase.auth.FirebaseAuth)

                    finish();

                }
            });
            b.setNegativeButton("abbrechen", null);
            b.show();


        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}