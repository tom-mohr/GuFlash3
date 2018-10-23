package com.selbstfindung.guflash;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MemberRecyclerViewAdapter extends RecyclerView.Adapter<MemberRecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "MONTAG";

    private ArrayList<String> memberNames = new ArrayList<>();
    private Context mContext;

    public MemberRecyclerViewAdapter(Context context, ArrayList<String>memberNames){

        mContext = context;
        this.memberNames = memberNames;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_memberlist, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.memberName.setText(memberNames.get(position));

        holder.memberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Hier könnte Ihre Werbung stehen ;)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView memberName;
        RelativeLayout memberLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            memberName = itemView.findViewById(R.id.member_name);
            memberLayout = itemView.findViewById(R.id.member_layout);

        }
    }

}
