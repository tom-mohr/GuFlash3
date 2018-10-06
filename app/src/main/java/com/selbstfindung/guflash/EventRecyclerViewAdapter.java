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
import com.selbstfindung.guflash.Activities.ChatActivity;
import com.selbstfindung.guflash.Activities.ClosedChatActivity;
import com.selbstfindung.guflash.Activities.JoinPopupActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.MyViewHolder>
{
    private static final String TAG = "MONTAG";

    private ArrayList<EventInfo> eventInfosRaw;
    private ArrayList<EventInfo> eventInfosSorted;
    private Context mContext;
    private String userId;
    
    // sorting
    private int sortType;
    public static final int SORT_TYPE_DISTANCE = 1;
    public static final int SORT_TYPE_TIME = 2;
    public static final int SORT_TYPE_MEMBERS = 3;
    private boolean excludeDistance;
    private int excludeDistanceKilometers = 10;
    private boolean excludeTime;
    private int excludeTimeDays = 14;
    
    private static Comparator<EventInfo> TIME_COMPARATOR = new Comparator<EventInfo>() {
        @Override
        public int compare(EventInfo e1, EventInfo e2) {
            return e1.getEventTime().compareTo(e2.getEventTime());
        }
    };
    
    public EventRecyclerViewAdapter(Context context, ArrayList<EventInfo> eventInfos) {
        mContext = context;
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    
        eventInfosRaw = eventInfos;
        
        excludeDistance = false;
        excludeTime = false;
        setSortType(SORT_TYPE_DISTANCE);
    }
    
    public void setSortType(int newSortType) {
        if (newSortType != sortType) {
            sortType = newSortType;
            makeSortedList();
        }
    }
    
    public void setExcludeDistance(boolean excludeDistance) {
        if (this.excludeDistance != excludeDistance) {
            this.excludeDistance = excludeDistance;
            makeSortedList();
        }
    }
    
    public void setExcludeTime(boolean excludeTime) {
        if (this.excludeTime != excludeTime) {
            this.excludeTime = excludeTime;
            makeSortedList();
        }
    }
    
    private void makeSortedList() {
        eventInfosSorted = new ArrayList<>(eventInfosRaw);
        switch (sortType) {
            case SORT_TYPE_DISTANCE:
                break;
            case SORT_TYPE_TIME:
                Collections.sort(eventInfosSorted, TIME_COMPARATOR);
                break;
        }
        
        notifyDataSetChanged();
    }
    
    public void addEvent(EventInfo eventInfo) {
        eventInfosRaw.add(eventInfo);
        makeSortedList();
        notifyDataSetChanged();
    }
    
    public void eventChanged(EventInfo eventInfo) {
    
        // wo befindet sich das event in der UNSORTIERTEN liste?
        int indexRaw = EventInfo.findIndexWithSameId(eventInfosRaw, eventInfo);
    
        // wenn event in liste ist, aktualisiere es
        if (indexRaw != -1) {
    
            eventInfosRaw.set(indexRaw, eventInfo);
    
            // wo befindet sich das event in der SORTIERTEN liste?
            int indexSorted = EventInfo.findIndexWithSameId(eventInfosSorted, eventInfo);
            
            if (indexSorted != -1) {
                // aktualisiere die Event-Datens
                eventInfosSorted.set(indexSorted, eventInfo);
                // -> berechne die sortierte liste neu, falls sich Daten geändert haben,
                //    sodass sich die Position in der Liste verschiebt
                makeSortedList();
                
                Log.d(TAG, "Event aktualisiert: " + eventInfo.name);
                
            } else {
                // -> das event war wegen den momentanen Filteroptionen nicht in der gefilterten Liste
                // -> berechne die gefilterte Liste neu, für den Fall dass sich die Daten so
                //    geändert haben, dass es doch in die gefilterte Liste aufgenommen wird
                makeSortedList();
            }
            
        } else {
            // event noch nicht in liste
            // (wahrscheinlich wurde erst jetzt das "READY"-child hinzugefügt)
            // -> füge eventInfo zu liste hinzu
            
            eventInfosRaw.add(eventInfo);
            makeSortedList();
    
            Log.d(TAG, "Event zur Liste hinzugefügt (nach warten auf READY): " + eventInfo.name);
        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        
        // zeige die event-daten im holder an
        
        EventInfo eventInfo = eventInfosSorted.get(position);
        
        holder.eventName.setText(eventInfo.name);
        holder.currentMemberCount.setText(String.valueOf(eventInfo.userIds.size()));
        holder.maxMembers.setText(String.valueOf(eventInfo.maxMembers));
        holder.membersDifference.setText(eventInfo.getMembersInfoString());
        holder.deltaTime.setText(eventInfo.getDeltaTimeInfoString());

        // make item clickable
        holder.eventLayout.setOnClickListener(new MyOnClickListener(holder));

    }

    @Override
    public int getItemCount() {
        return eventInfosSorted.size();
    }
    
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_event_item, parent, false);
        return new MyViewHolder(view);
    }
    
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        TextView currentMemberCount;
        TextView maxMembers;
        TextView membersDifference;
        TextView deltaTime;
        RelativeLayout eventLayout;

        MyViewHolder(View itemView) {
            super(itemView);

            eventName = itemView.findViewById(R.id.group_name);
            eventLayout = itemView.findViewById(R.id.group_layout);
            membersDifference = itemView.findViewById(R.id.teilnehmer_differenz);
            currentMemberCount = itemView.findViewById(R.id.current_participants);
            deltaTime = itemView.findViewById(R.id.event_list_delta_time);
            maxMembers = itemView.findViewById(R.id.max_participants);
        }
    }
    
    private class MyOnClickListener implements View.OnClickListener {
        
        RecyclerView.ViewHolder holder;
    
        MyOnClickListener(RecyclerView.ViewHolder holder) {
            this.holder = holder;
        }
    
        @Override
        public void onClick(View v) {
            
            EventInfo eventInfo = eventInfosSorted.get(holder.getAdapterPosition());
            
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
