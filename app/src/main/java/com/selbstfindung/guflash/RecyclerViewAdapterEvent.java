package com.selbstfindung.guflash;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selbstfindung.guflash.Activities.ChatActivity;
import com.selbstfindung.guflash.Activities.ProfileActivity;

import java.util.ArrayList;

public class RecyclerViewAdapterEvent extends RecyclerView.Adapter<RecyclerViewAdapterEvent.ViewHolder>
{
    private static final String TAG = "MONTAG";

    private ArrayList<String> mEventIDs = new ArrayList<>();
    private Context mContext;

    String EventName;
    String Teilnehmerzahl;
    String MaxTeilnehmerzahl;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private User user;

    public RecyclerViewAdapterEvent(ArrayList<String> eventIDs, Context context)
    {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        mEventIDs = eventIDs;
        mContext = context;
    }



    @NonNull
    @Override
    public RecyclerViewAdapterEvent.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_event_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapterEvent.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder(Group-RecycleView): called");

        user = new User(firebaseUser.getUid(), new User.Callback() {
            @Override
            public void onProfileChanged() {}

            @Override
            public void onLoadingFailed() {}
        });

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                EventName = dataSnapshot.child("groups").child(mEventIDs.get(position)).child("name").getValue(String.class);
                Teilnehmerzahl = "" + dataSnapshot.child("groups").child(mEventIDs.get(position)).child("users").getChildrenCount();
                MaxTeilnehmerzahl = "" + dataSnapshot.child("groups").child(mEventIDs.get(position)).child("maxTeilnehmer").getValue();
                int Differenz = (int)((long)dataSnapshot.child("groups").child(mEventIDs.get(position)).child("maxTeilnehmer").getValue()-dataSnapshot.child("groups").child(mEventIDs.get(position)).child("users").getChildrenCount());

                if(EventName.length()<42)
                {
                    holder.event_name.setText(EventName);
                }
                else
                {
                    holder.event_name.setText(EventName.substring(0,38)+"...");
                }

                holder.teilnehmer_differenz.setText("Noch "+ Differenz +" Plätze übrig");

                holder.current_participants.setText(Teilnehmerzahl);
                holder.max_participants.setText(MaxTeilnehmerzahl);

                Log.d(TAG, "Gruppe hinzugefügt "+EventName +" "+ Teilnehmerzahl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.event_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "Gruppe " +mEventIDs.get(position)+ " wurde aufgerufen");

                //weiterleiten an den Chat
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_MESSAGE_GRUPPEN_ID, mEventIDs.get(position));
                mContext.startActivity(intent);
            }
        });

        holder.join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user.addEventID(mEventIDs.get(position));

                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_MESSAGE_GRUPPEN_ID, mEventIDs.get(position));
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mEventIDs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView event_name;
        TextView teilnehmer_differenz;
        TextView current_participants;
        TextView max_participants;
        Button join_button;
        RelativeLayout event_layout;

        public ViewHolder(View itemView)
        {
            super(itemView);

            event_name = itemView.findViewById(R.id.group_name);
            event_layout = itemView.findViewById(R.id.group_layout);
            teilnehmer_differenz = itemView.findViewById(R.id.teilnehmer_differenz);
            current_participants = itemView.findViewById(R.id.current_participants);
            max_participants = itemView.findViewById(R.id.max_participants);
            join_button = itemView.findViewById(R.id.join_button);
        }
    }
}
