package com.app.mcc.member;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mcc.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

public class MemberLookAdapter extends RecyclerView.Adapter<MemberLookAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<HashMap<String,String>> lookList;

    public MemberLookAdapter(Context mContext, ArrayList<HashMap<String, String>> lookList) {
        this.mContext = mContext;
        this.lookList = lookList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_look_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final HashMap<String, String> map = lookList.get(position);

        holder.tvTitle.setText(map.get("title"));
        Glide.with(mContext).load(map.get("image")).into(holder.ivImage);

    }

    @Override
    public int getItemCount() {
        return lookList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        TextView tvTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.mem_look_title);
            ivImage = itemView.findViewById(R.id.mem_look_image);


        }
    }
}
