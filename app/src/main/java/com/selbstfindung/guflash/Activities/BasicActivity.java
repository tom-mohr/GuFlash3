package com.selbstfindung.guflash.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.selbstfindung.guflash.R;
import com.selbstfindung.guflash.RecyclerViewAdapterGroup;

import java.util.ArrayList;

public class BasicActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";

    private ArrayList<String> groupIDs = new ArrayList<>();
    private ArrayList<String> groupNames = new ArrayList<>();
    private RecyclerViewAdapterGroup recyclerViewAdapterGroup;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        init();
    }

    private void init() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BasicActivity.this, CreateGroupActivity.class));
            }
        });


        setTitle("Gruppen");

        // listen for changes to the "groups" child
        mRef.child("groups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                // new group added

                String groupID = dataSnapshot.getKey();

                String groupName = dataSnapshot.child("name").getValue(String.class);

                groupIDs.add(groupID);
                groupNames.add(groupName);


                if (recyclerViewAdapterGroup != null) {
                    int position = groupIDs.size() - 1;// last index
                    recyclerViewAdapterGroup.notifyItemInserted(position);
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
        recyclerViewAdapterGroup = new RecyclerViewAdapterGroup(groupIDs, groupNames, this);
        recyclerView.setAdapter(recyclerViewAdapterGroup);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
