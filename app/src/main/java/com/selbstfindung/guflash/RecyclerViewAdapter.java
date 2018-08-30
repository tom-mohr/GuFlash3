package com.selbstfindung.guflash;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "MONTAG";

    private Context mContext;
    private ArrayList<String> messageUsernames = new ArrayList<>();
    private ArrayList<String> messageTexts = new ArrayList<>();

    public RecyclerViewAdapter(Context context, ArrayList<String> messageUsernames, ArrayList<String> messageTexts) {
        this.mContext = context;
        this.messageUsernames = messageUsernames;
        this.messageTexts = messageTexts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_message, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder wurde aufgerufen.");

        // username und nachricht sichtbar machen
        holder.messageUsername.setText(messageUsernames.get(position));
        holder.messageText.setText(messageTexts.get(position));

        /// for dev:
        // bei klick auf das element, zeige einen toast an:
        holder.messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String infoText = "Nachricht angeklickt: "+String.valueOf(position);

                // SNACKBAR! (richtig nice)
                Snackbar.make(v, infoText, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return messageTexts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView messageUsername;
        TextView messageText;
        RelativeLayout messageLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            messageUsername = itemView.findViewById(R.id.chat_message_username);
            messageText = itemView.findViewById(R.id.chat_message_text);
            messageLayout = itemView.findViewById(R.id.chat_message_layout);
        }
    }
}
