package com.selbstfindung.guflash;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberRecyclerViewAdapter extends RecyclerView.Adapter<MemberRecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "MONTAG";

    private ArrayList<String> userIds = new ArrayList<>();
    private Context mContext;
    Map<String, String> userNames;

    DatabaseReference allUsersRef;

    public MemberRecyclerViewAdapter(Context context, ArrayList<String>userIds){

        mContext = context;
        this.userIds = userIds;

        allUsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        userNames = new HashMap<>();
    }

    private interface GetUsernameFromIdCallback {
        void foundUsername(String username);
    }

    @Nullable
    private void getUsernameFromID(String id, final GetUsernameFromIdCallback callback) {
        if (userNames.containsKey(id)) {
            String name = userNames.get(id);
            callback.foundUsername(name);
        } else {
            final String messageUserId = id;
            allUsersRef.child(id).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = (String) dataSnapshot.getValue();
                    userNames.put(messageUserId, name);
                    callback.foundUsername(name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
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
        //holder.memberName.setText(userIds.get(position));

        holder.bind(userIds, position);

        holder.memberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Hier könnte Ihre Werbung stehen ;)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userIds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView memberName;
        RelativeLayout memberLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            memberName = itemView.findViewById(R.id.member_name);
            memberLayout = itemView.findViewById(R.id.member_layout);

        }

        void bind(ArrayList<String> userIds, int position)
        {
            GetUsernameFromIdCallback callback = new GetUsernameFromIdCallback() {
                @Override
                public void foundUsername(String username) {
                    memberName.setText(username);
                }
            };

            getUsernameFromID(userIds.get(position), callback);
        }
    }

}
