package com.selbstfindung.guflash;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class GroupActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";

    private ArrayList<String> groupIDs = new ArrayList<>();
    private ArrayList<String> groupNames = new ArrayList<>();
    private RecyclerViewAdapterGroup recyclerViewAdapterGroup;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        init();
    }

    private void init() {

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

        // onclick für "Neue Gruppe erstellen"
        ((ImageButton) findViewById(R.id.create_group_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EditText editGroupName = ((EditText) findViewById(R.id.create_group));

                String groupName = editGroupName.getText().toString();

                DatabaseReference newGroupRef = mRef.child("groups").push();
                newGroupRef.child("name").setValue(groupName);

                editGroupName.setText("");

            }
        });
    }

    private  void initRecyclerView() {
        Log.d(TAG, "initialisiere RecyclerView für Gruppen");

        RecyclerView recyclerView = findViewById(R.id.groups_recycler_view);
        recyclerViewAdapterGroup = new RecyclerViewAdapterGroup(groupIDs, groupNames, this);
        recyclerView.setAdapter(recyclerViewAdapterGroup);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
