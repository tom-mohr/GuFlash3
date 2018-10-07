package com.selbstfindung.guflash.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.selbstfindung.guflash.EventInfo;
import com.selbstfindung.guflash.R;
import com.selbstfindung.guflash.EventRecyclerViewAdapter;

import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MONTAG";

    private ArrayList<EventInfo> eventInfos;
    private EventRecyclerViewAdapter eventRecyclerViewAdapter;

    String userId;
    
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // AUTO-GENERIERT VON ANDROID STUDIO:


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // eigener code:
                Intent intent = new Intent(NavigationActivity.this, CreateEventActivity.class);
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
    
        setTitle(R.string.title_eventlist_all);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        eventInfos = new ArrayList<>();
    
        final RecyclerView recyclerView = findViewById(R.id.events_recycler_view);
        eventRecyclerViewAdapter = new EventRecyclerViewAdapter(this, eventInfos, new FilterInformationManager());
        recyclerView.setAdapter(eventRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // set divider for recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // listen for changes to the "events" child
        FirebaseDatabase.getInstance().getReference().child("events").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                // new event added
                
                EventInfo eventInfo = checkEventInfo(dataSnapshot);
                    
                if (eventInfo != null) {
                    
                    eventRecyclerViewAdapter.addEvent(eventInfo);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                
                EventInfo eventInfo = checkEventInfo(dataSnapshot);
                
                if (eventInfo != null) {
                    
                    eventRecyclerViewAdapter.eventChanged(eventInfo);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        
        // get my location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);// request "block" level accuracy
        mLocationRequest.setInterval(1000*60);// get location once every 60 seconds
        mLocationRequest.setFastestInterval(1000*20);// if available, get location up to once every 20 seconds
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.w(TAG, "LocationCallback: Location is null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    
                    eventRecyclerViewAdapter.myLocationChanged(location);
                }
            };
        };
        getLocationOnce();
        startLocationUpdates();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }
    
    public class FilterInformationManager {
        private LinearLayout layout;
        private TextSwitcher line1;
        private TextSwitcher line2;
        
        private int sortType = EventRecyclerViewAdapter.NO_SORT_TYPE;
    
        FilterInformationManager() {
            
            layout = findViewById(R.id.content_navigation_filter_information_layout);
            line1 = findViewById(R.id.content_navigation_filter_information_line1);
            line2 = findViewById(R.id.content_navigation_filter_information_line2);
    
            // set in- and out-Animation for TextSwitchers
            Animation inAnim1 = AnimationUtils.loadAnimation(NavigationActivity.this, android.R.anim.slide_in_left);
            Animation outAnim1 = AnimationUtils.loadAnimation(NavigationActivity.this, android.R.anim.slide_out_right);
            Animation inAnim2 = AnimationUtils.loadAnimation(NavigationActivity.this, android.R.anim.fade_in);
            Animation outAnim2 = AnimationUtils.loadAnimation(NavigationActivity.this, android.R.anim.fade_out);
            line1.setInAnimation(inAnim1);
            line1.setOutAnimation(outAnim1);
            line2.setInAnimation(inAnim2);
            line2.setOutAnimation(outAnim2);
            
            // set ViewFactory for TextSwitchers
            ViewSwitcher.ViewFactory vf = new ViewSwitcher.ViewFactory() {
                @Override
                public View makeView() {
                    TextView t = new TextView(NavigationActivity.this);
                    TextViewCompat.setTextAppearance(t, R.style.TextAppearance_AppCompat_Widget_ActionBar_Subtitle_Inverse);// support lower APIs
                    t.setPadding(10,10,10,10);
                    return t;
                }
            };
            line1.setFactory(vf);
            line2.setFactory(vf);
        }
        
        public void setSortType(int newSortType) {
            if (newSortType != sortType) {
                sortType = newSortType;
                
                switch (sortType) {
                    case EventRecyclerViewAdapter.SORT_TYPE_DISTANCE:
                        line1.setVisibility(View.VISIBLE);
                        line1.setText("sortiert nach Entfernung");
                        break;
                        
                    case EventRecyclerViewAdapter.SORT_TYPE_TIME:
                        line1.setVisibility(View.VISIBLE);
                        line1.setText("sortiert nach Zeit");
                        break;
                        
                    case EventRecyclerViewAdapter.SORT_TYPE_ALPHABETICALLY:
                        line1.setVisibility(View.VISIBLE);
                        line1.setText("sortiert alphabetisch");
                        break;
                        
                    case EventRecyclerViewAdapter.NO_SORT_TYPE:
                        line1.setText("");
                        line1.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }
    
    private void getLocationOnce() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                    
                        Log.i(TAG, "got Location: Lat " + location.getLatitude() + " | Long " + location.getLongitude());
                    
                        eventRecyclerViewAdapter.myLocationChanged(location);
                    
                    } else {
                        Log.w(TAG, "Location is null");
                    }
                }
            });
        } else {
            Log.w(TAG, "Permission ACCESS_COARSE_LOCATION not granted");
        }
    }
    
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null);
        }
    }
    
    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
    
    private EventInfo checkEventInfo(DataSnapshot ds) {
        // returns EventInfo if everything is alright
        // returns null if something was wrong
    
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

                    eventRecyclerViewAdapter.setSortType(EventRecyclerViewAdapter.SORT_TYPE_DISTANCE);
                }
                return true;
                
            case R.id.filter_sort_time:
                if (item.isChecked()) {
                    // ignore
                } else {
                    // check
                    item.setChecked(true);
            
                    // apply this filter function
    
                    eventRecyclerViewAdapter.setSortType(EventRecyclerViewAdapter.SORT_TYPE_TIME);
                }
                return true;
            
            case R.id.filter_sort_alphabetically:
                if (item.isChecked()) {
                    // ignore
                } else {
                    // check
                    item.setChecked(true);
                    
                    // apply this filter function
                    
                    eventRecyclerViewAdapter.setSortType(EventRecyclerViewAdapter.SORT_TYPE_ALPHABETICALLY);
                }
                return true;
                
            case R.id.filter_ignore_distance:
                if (item.isChecked()) {
                    // uncheck
                    item.setChecked(false);
                    
                    // remove this filter option
                    eventRecyclerViewAdapter.setExcludeDistance(false);
                    
                } else {
                    // check
                    item.setChecked(true);
    
                    // apply this filter function
                    eventRecyclerViewAdapter.setExcludeDistance(true);
                }
                return true;
                
            case R.id.filter_ignore_time:
                if (item.isChecked()) {
                    // uncheck
                    item.setChecked(false);
            
                    // remove this filter option
                    eventRecyclerViewAdapter.setExcludeTime(false);
                    
                } else {
                    // check
                    item.setChecked(true);
    
                    // apply this filter function
                    eventRecyclerViewAdapter.setExcludeTime(true);
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
            eventRecyclerViewAdapter.setExcludeUser(false);
            setTitle(R.string.title_eventlist_all);

        } else if (id == R.id.nav_favorite_events) {
            eventRecyclerViewAdapter.setExcludeUser(true);
            setTitle(R.string.title_eventlist_my);

        } else if (id == R.id.nav_profile_settings) {
            startActivity(new Intent(NavigationActivity.this, ProfileActivity.class));

        } else if (id == R.id.nav_app_notifications) {
            startActivity(new Intent(NavigationActivity.this, NotificationActivity.class));

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
