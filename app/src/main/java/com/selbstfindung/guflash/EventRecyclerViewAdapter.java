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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selbstfindung.guflash.Activities.ChatActivity;
import com.selbstfindung.guflash.Activities.ClosedChatActivity;
import com.selbstfindung.guflash.Activities.JoinPopupActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.MyViewHolder>
{
    private static final String TAG = "MONTAG";

    private ArrayList<EventInfo> eventInfos;
    private Context mContext;
    
    Map<String,ArrayList<String>> memberIds;

    boolean userVorhanden;
    
    private DatabaseReference eventsRef;
    private String userId;
    private String sortType;

    public EventRecyclerViewAdapter(ArrayList<EventInfo> eventInfos, Context context) {
        this.eventInfos = eventInfos;
        mContext = context;
        this.sortType = sortType;


        
        eventsRef = FirebaseDatabase.getInstance().getReference().child("events");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        memberIds = new HashMap<>();
    }
    
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        
        // zeige die event-daten im holder an
        
        EventInfo eventInfo = eventInfos.get(position);
        
        holder.eventName.setText(eventInfo.name);
        holder.currentMemberCount.setText(String.valueOf(eventInfo.userIds.size()));
        holder.maxMembers.setText(String.valueOf(eventInfo.maxMembers));

        //Unterschiedlicher Output je nach Teilnehmerzahl
        int diff = eventInfo.maxMembers - eventInfo.userIds.size();
        int diff2 = eventInfo.minMembers - eventInfo.userIds.size();
        String nMembersInfoString;
        if(diff2>0)
        {
            nMembersInfoString = "Noch " + diff2 + " Teilnehmer benötigt bis zur Eröffnung des Chats";
        }
        else {
            if(diff == 1)
            {
                nMembersInfoString = "Nur noch " + diff + " Platz frei";
            }
            else if (diff > 3)
                nMembersInfoString = "Noch " + diff + " Plätze frei";
            else if(diff > 0)
            {
                nMembersInfoString = "Nur noch " + diff + " Plätze frei";
            }
            else
                nMembersInfoString = "alle " + eventInfo.maxMembers + " Plätze belegt";
        }
            holder.membersDifference.setText(nMembersInfoString);

            // make item clickable
            holder.eventLayout.setOnClickListener(new MyOnClickListener(holder));

    }

    @Override
    public int getItemCount() {
        return eventInfos.size();
    }
    
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_event_item, parent, false);
        return new MyViewHolder(view);
    }
    
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        TextView currentMemberCount;
        TextView maxMembers;
        TextView membersDifference;
        RelativeLayout eventLayout;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            eventName = itemView.findViewById(R.id.group_name);
            eventLayout = itemView.findViewById(R.id.group_layout);
            membersDifference = itemView.findViewById(R.id.teilnehmer_differenz);
            currentMemberCount = itemView.findViewById(R.id.current_participants);
            maxMembers = itemView.findViewById(R.id.max_participants);
        }
    }
    
    private class MyOnClickListener implements View.OnClickListener {
        
        RecyclerView.ViewHolder holder;
    
        public MyOnClickListener(RecyclerView.ViewHolder holder) {
            this.holder = holder;
        }
    
        @Override
        public void onClick(View v) {
            
            EventInfo eventInfo = eventInfos.get(holder.getAdapterPosition());
            
            if (eventInfo.userIds.contains(userId)) {
                // user ist mitglied -> öffne chat
                
                if (eventInfo.userIds.size() < eventInfo.minMembers) {
                    // chat ist noch gesperrt -> öffne ClosedChatActivity
    
                    Intent intent = new Intent(mContext, ClosedChatActivity.class);
                    intent.putExtra(ClosedChatActivity.EXTRA_MESSAGE_GRUPPEN_ID, eventInfo.id);
                    intent.putExtra(ClosedChatActivity.EXTRA_MESSAGE_REASON, "Teilnehmer" + (eventInfo.minMembers-eventInfo.userIds.size()));
                    mContext.startActivity(intent);
                    
                } else {
                    // chat ist offen -> öffne ChatActivity
    
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra(ChatActivity.EXTRA_MESSAGE_GRUPPEN_ID, eventInfo.id);
                    mContext.startActivity(intent);
                }
                
            } else {
                // user ist nicht mitglied -> öffne popup
    
                Intent intent = new Intent(mContext, JoinPopupActivity.class);
                intent.putExtra(ChatActivity.EXTRA_MESSAGE_GRUPPEN_ID, eventInfo.id);
                intent.putExtra(JoinPopupActivity.EXTRA_MESSAGE_MIN_TEILNEHMER, String.valueOf(eventInfo.minMembers));
                mContext.startActivity(intent);
            }
        }
    }
}
