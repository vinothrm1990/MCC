package com.app.mcc.guest;

import android.content.Context;
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

public class GuestHomeAdapter extends RecyclerView.Adapter<GuestHomeAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<HashMap<String,String>> homeList;

    public GuestHomeAdapter(Context mContext, ArrayList<HashMap<String, String>> homeList) {
        this.mContext = mContext;
        this.homeList = homeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.guest_home_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        HashMap<String,String> map = homeList.get(position);

        holder.tvTitle.setText(map.get("title"));
        Glide.with(mContext).load(map.get("image")).thumbnail(0.1f).into(holder.ivImage);

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

            tvTitle = itemView.findViewById(R.id.home_tv);
            ivImage = itemView.findViewById(R.id.home_iv);
            cardView = itemView.findViewById(R.id.cv_home);
        }
    }
}
