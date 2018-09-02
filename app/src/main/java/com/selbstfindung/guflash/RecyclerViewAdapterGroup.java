package com.selbstfindung.guflash;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selbstfindung.guflash.Activities.ChatActivity;

import java.util.ArrayList;

public class RecyclerViewAdapterGroup extends RecyclerView.Adapter<RecyclerViewAdapterGroup.ViewHolder>
{
    private static final String TAG = "MONTAG";

    private ArrayList<String> mGroupIDs = new ArrayList<>();
    private Context mContext;

    String GroupName;
    String GroupDescription;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    public RecyclerViewAdapterGroup(ArrayList<String> groupIDs, Context context)
    {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        mGroupIDs = groupIDs;
        mContext = context;
    }



    @NonNull
    @Override
    public RecyclerViewAdapterGroup.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapterGroup.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder(Group-RecycleView): called");



        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                GroupName = dataSnapshot.child("groups").child(mGroupIDs.get(position)).child("name").getValue(String.class);
                GroupDescription = dataSnapshot.child("groups").child(mGroupIDs.get(position)).child("description").getValue(String.class);

                if(GroupName.length()<42)
                {
                    holder.group_name.setText(GroupName);
                }
                else
                {
                    holder.group_name.setText(GroupName.substring(0,38)+"...");
                }
                if(!GroupDescription.equals("")) {

                        holder.group_description.setText(GroupDescription);
                }
                else
                {
                    holder.group_description.setText("keine Beschreibung verfügbar");
                }

                Log.d(TAG, "Gruppe hinzugefügt "+GroupName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.group_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "Gruppe " +mGroupIDs.get(position)+ " wurde aufgerufen");

                //weiterleiten an den Chat
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_MESSAGE_GRUPPEN_ID, mGroupIDs.get(position));
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mGroupIDs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView group_name;
        TextView group_description;
        TextView group_participants;
        RelativeLayout group_layout;

        public ViewHolder(View itemView)
        {
            super(itemView);

            group_name = itemView.findViewById(R.id.group_name);
            group_layout = itemView.findViewById(R.id.group_layout);
            group_description = itemView.findViewById(R.id.group_description);
            group_participants = itemView.findViewById(R.id.group_participants);
        }
    }
}
