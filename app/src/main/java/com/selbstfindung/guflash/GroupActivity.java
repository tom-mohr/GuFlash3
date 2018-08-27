package com.selbstfindung.guflash;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {

    private static final String TAG = "MONTAG";

    private ArrayList<String> mGroupNames = new ArrayList<>();
    //private Context mContext;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        initList();
        init();
    }

    private void init()
    {
        ((Button) findViewById(R.id.create_group_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EditText editGruppenname = (EditText) (findViewById(R.id.create_group));
                String Gruppenname = editGruppenname.getText().toString();

                mRef.child("Gruppen").child(Gruppenname).setValue(Gruppenname);
                editGruppenname.setText("");

            }
        });

        ((Button) findViewById(R.id.refresh_button)).setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initList();
            }
        }));

    }

    private void initList()
    {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "ArrayList wird initialisiert");
                mGroupNames.clear();
                for(DataSnapshot ds: dataSnapshot.child("Gruppen").getChildren())
                {
                    Log.d(TAG, "Es wird hinzugefügt" + ds.getValue(String.class));
                    mGroupNames.add(ds.getValue(String.class));
                }
                initRecyclerViewGroup();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  void initRecyclerViewGroup()
    {
        Log.d(TAG, "initialisiere RecyclerViewGroup");

        RecyclerView recyclerViewGroup = findViewById(R.id.recycler_viewGroup);
        RecyclerViewAdapterGroup adapterGroup = new RecyclerViewAdapterGroup(mGroupNames, this);
        recyclerViewGroup.setAdapter(adapterGroup);
        recyclerViewGroup.setLayoutManager(new LinearLayoutManager(this));
    }
}
