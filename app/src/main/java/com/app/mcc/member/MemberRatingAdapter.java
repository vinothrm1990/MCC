package com.app.mcc.member;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.app.mcc.R;
import com.app.mcc.helper.Constants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

public class MemberRatingAdapter extends RecyclerView.Adapter<MemberRatingAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<HashMap<String,String>> rateList;

    public MemberRatingAdapter(Context mContext, ArrayList<HashMap<String, String>> rateList) {
        this.mContext = mContext;
        this.rateList = rateList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_rate_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        HashMap<String,String> map = rateList.get(position);

        holder.tvTitle.setText(map.get("title"));
        holder.tvTitle.setSelected(true);
        holder.tvDesc.setText(map.get("description"));
        holder.ratingBar.setRating(Float.parseFloat(map.get("star")));
        Glide.with(mContext).load(Constants.RATE_URL + map.get("image")).thumbnail(0.1f).into(holder.ivImage);


    }

    @Override
    public int getItemCount() {
        return rateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        RatingBar ratingBar;
        TextView tvTitle, tvDesc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.mem_rate_iv);
            tvTitle = itemView.findViewById(R.id.mem_rate_title);
            tvDesc = itemView.findViewById(R.id.mem_rate_desc);
            ratingBar = itemView.findViewById(R.id.mem_rate_bar);
        }
    }
}
