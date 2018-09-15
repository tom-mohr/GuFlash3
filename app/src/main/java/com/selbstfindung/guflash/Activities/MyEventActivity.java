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
import com.selbstfindung.guflash.R;
import com.selbstfindung.guflash.RecyclerViewAdapterEvent;
import com.selbstfindung.guflash.User;

import java.util.ArrayList;

public class MyEventActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MONTAG";

    private ArrayList<String> eventIDs = new ArrayList<>();
    private RecyclerViewAdapterEvent recyclerViewAdapterEvent;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myevent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // AUTO-GENERIERT VON ANDROID STUDIO:

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

        init();
    }

    private void init() {

        setTitle("My Events");

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // listen for changes to the "groups" child
        mRef.child("users").child(userID).child("events").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                // new group added

                String eventID = dataSnapshot.getValue(String.class);

                eventIDs.add(eventID);


                if (recyclerViewAdapterEvent != null) {
                    int position = eventIDs.size() - 1;// last index
                    recyclerViewAdapterEvent.notifyItemInserted(position);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });


        initRecyclerView();
    }



    private  void initRecyclerView() {
        Log.d(TAG, "initialisiere RecyclerView für Gruppen");

        RecyclerView recyclerView = findViewById(R.id.groups_recycler_view);
        recyclerViewAdapterEvent = new RecyclerViewAdapterEvent(eventIDs, this);
        recyclerView.setAdapter(recyclerViewAdapterEvent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_groups) {
            startActivity(new Intent(MyEventActivity.this, NavigationActivity.class));

        } else if (id == R.id.nav_favorite_groups) {
            startActivity(new Intent(MyEventActivity.this, MyEventActivity.class));

        } else if (id == R.id.nav_profile_settings) {
            startActivity(new Intent(MyEventActivity.this, ProfileActivity.class));

        } else if (id == R.id.nav_app_settings) {
            startActivity(new Intent(MyEventActivity.this, ConfigurationActivity.class));

        } else if (id == R.id.nav_logout) {

            // ask for confirmation


            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Abmelden").setMessage("Willst du dich wirklich abmelden?");
            b.setIcon(R.drawable.ic_menu_lock);
            b.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    // sign out from firebase
                    mAuth.signOut();

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
