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
import com.selbstfindung.guflash.Activities.JoinPopupActivity;
import com.selbstfindung.guflash.Activities.NavigationActivity;

import java.util.ArrayList;

public class RecyclerViewAdapterEvent extends RecyclerView.Adapter<RecyclerViewAdapterEvent.ViewHolder>
{
    private static final String TAG = "MONTAG";

    private ArrayList<String> mEventIDs = new ArrayList<>();
    private Context mContext;

    private String eventName;
    private int nMembers;
    private int maxMembers;
    private ArrayList<String> memberIds = new ArrayList<>();

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

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                DataSnapshot eventRef = dataSnapshot.child("events").child(mEventIDs.get(position));

                eventName = eventRef.child("name").getValue(String.class);
                for (DataSnapshot child: eventRef.child("users").getChildren()) {
                    memberIds.add(child.getKey());
                }
                nMembers = ((Long) eventRef.child("users").getChildrenCount()).intValue();
                maxMembers = ((Long) eventRef.child("max_members").getValue()).intValue();
                int diff = maxMembers - nMembers;

                if(eventName.length()<42) {
                    holder.event_name.setText(eventName);
                } else {
                    holder.event_name.setText(eventName.substring(0,38)+"...");
                }
                
                String nMembersInfoString;
                if (diff > 0)
                    nMembersInfoString = "Noch "+diff+" von "+maxMembers+" Plätzen frei";
                else
                    nMembersInfoString = "alle "+maxMembers+" Plätze belegt";
                
                holder.teilnehmer_differenz.setText(nMembersInfoString);
                holder.current_participants.setText(String.valueOf(nMembers));
                holder.max_participants.setText(String.valueOf(maxMembers));

                Log.d(TAG, "Gruppe hinzugefügt "+ eventName +" "+ nMembers);
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
                
                // bin ich bereits member in dieser gruppe?
                if (isUserMember()) {
                    //weiterleiten an den Chat
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra(ChatActivity.EXTRA_MESSAGE_GRUPPEN_ID, mEventIDs.get(position));
                    mContext.startActivity(intent);
                } else {
                    // join-dialog öffnen
                    Intent intent = new Intent(mContext, JoinPopupActivity.class);
                    intent.putExtra(ChatActivity.EXTRA_MESSAGE_GRUPPEN_ID, mEventIDs.get(position));
                    mContext.startActivity(intent);
                }
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
    
    private boolean isUserMember() {
        for (String id: memberIds)
            if (id.equals(user.getId()))
                return true;
        return false;
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
