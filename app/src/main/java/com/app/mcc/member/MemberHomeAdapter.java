package com.app.mcc.member;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
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

import spencerstudios.com.bungeelib.Bungee;


public class MemberHomeAdapter extends RecyclerView.Adapter<MemberHomeAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<HashMap<String,String>> homeList;

    public MemberHomeAdapter(Context mContext, ArrayList<HashMap<String, String>> homeList) {
        this.mContext = mContext;
        this.homeList = homeList;
    }

    @NonNull
    @Override
    public MemberHomeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_home_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberHomeAdapter.MyViewHolder holder, final int position) {


        HashMap<String,String> map = homeList.get(position);

        holder.tvTitle.setText(map.get("title"));
        Glide.with(mContext).load(map.get("image")).thumbnail(0.1f).into(holder.ivImage);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (position == 0){
                    mContext.startActivity(new Intent(mContext, MemberPostActivity.class));
                    Bungee.fade(mContext);
                }else if (position == 1){
                    mContext.startActivity(new Intent(mContext, MemberTrailerActivity.class));
                    Bungee.fade(mContext);
                }else if (position == 2){
                    mContext.startActivity(new Intent(mContext, MemberRatingActivity.class));
                    Bungee.fade(mContext);
                }else if (position == 3){
                    mContext.startActivity(new Intent(mContext, MemberLookActivity.class));
                    Bungee.fade(mContext);
                }else if (position == 4){
                    mContext.startActivity(new Intent(mContext, MemberInterviewActivity.class));
                    Bungee.fade(mContext);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return homeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView ivImage;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.mem_home_tv);
            ivImage = itemView.findViewById(R.id.mem_home_iv);
            cardView = itemView.findViewById(R.id.cv_mem_home);
        }
    }
}
