package com.selbstfindung.guflash;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter {

    private static final String TAG = "MONTAG";
    
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    
    private DateFormat timeFormatter;

    private Context mContext;
    private List<Message> messageList;
    String ownUserID;
    Map<String, String> userNames;

    public ChatRecyclerViewAdapter(Context context, List<Message> messageList, String ownUserID) {
        this.mContext = context;
        this.messageList = messageList;
        this.ownUserID = ownUserID;
        
        timeFormatter = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
        
        userNames = new HashMap<>();
    }
    
    @Nullable
    private String getUsernameFromID(String id) {
        if (userNames.containsKey(id)) {
            return userNames.get(id);
        }
        return null;
    }
    
    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSenderUserID().equals(ownUserID)) {
            return VIEW_TYPE_MESSAGE_SENT;
        }
        return VIEW_TYPE_MESSAGE_RECEIVED;
    }
    
    // Inflates the appropriate layout according to the ViewType.
    @Override
    ////@NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_sent, parent, false);
            return new SentMessageHolder(view);
            
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_received, parent, false);
            return new ReceivedMessageHolder(view);
            
        }
        return null;
    }
    
    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        
        switch (holder.getItemViewType()) {
            
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
                
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
    
    
    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        RelativeLayout messageLayout;// for onclick listener
        
        SentMessageHolder(View itemView) {
            super(itemView);
            
            messageText = (TextView) itemView.findViewById(R.id.chat_message_text);
            timeText = (TextView) itemView.findViewById(R.id.chat_message_timestamp);
            messageLayout = (RelativeLayout) itemView.findViewById(R.id.layout_chat_message_sent);
        }
        
        void bind(Message message) {
            // Timestamp lesbar machen (long -> String)
            String timeString = timeFormatter.format(new Date(message.getTime()));
            
            messageText.setText(message.getText());
            timeText.setText(timeString);
        }
    }
    
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        RelativeLayout messageLayout;// for onclick listener
        
        ReceivedMessageHolder(View itemView) {
            super(itemView);
            
            messageText = (TextView) itemView.findViewById(R.id.chat_message_text);
            timeText = (TextView) itemView.findViewById(R.id.chat_message_timestamp);
            nameText = (TextView) itemView.findViewById(R.id.chat_message_username);
            messageLayout = (RelativeLayout) itemView.findViewById(R.id.layout_chat_message_received);
        }
        
        void bind(Message message) {
            // Timestamp lesbar machen (long -> String)
            String timeString = timeFormatter.format(new Date(message.getTime()));
    
            messageText.setText(message.getText());
            timeText.setText(timeString);
            nameText.setText(message.getSenderUserID());
        }
    }
}
