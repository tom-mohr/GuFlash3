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

import com.selbstfindung.guflash.Activities.ChatActivity;

import java.util.ArrayList;

public class RecyclerViewAdapterGroup extends RecyclerView.Adapter<RecyclerViewAdapterGroup.ViewHolder>
{
    private static final String TAG = "MONTAG";

    private ArrayList<String> mGroupIDs = new ArrayList<>();
    private ArrayList<String> mGroupNames = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapterGroup(ArrayList<String> groupIDs, ArrayList<String> groupNames, Context context)
    {
        mGroupIDs = groupIDs;
        mGroupNames = groupNames;
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
    public void onBindViewHolder(@NonNull RecyclerViewAdapterGroup.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder(Group-RecycleView): called");

        holder.group_name.setText(mGroupNames.get(position));

        holder.group_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "Gruppe " +mGroupNames.get(position)+ " wurde aufgerufen");

                //weiterleiten an den Chat
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_MESSAGE_GRUPPEN_ID, mGroupIDs.get(position));
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mGroupNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView group_name;
        RelativeLayout group_layout;

        public ViewHolder(View itemView)
        {
            super(itemView);

            group_name = itemView.findViewById(R.id.group_name);
            group_layout = itemView.findViewById(R.id.group_layout);
        }
    }
}
