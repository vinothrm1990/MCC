package com.app.mcc.member;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mcc.R;
import com.bumptech.glide.Glide;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MemberInterviewAdapter extends RecyclerView.Adapter<MemberInterviewAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<HashMap<String,String>> interviewList;

    public MemberInterviewAdapter(Context mContext, ArrayList<HashMap<String, String>> interviewList) {
        this.mContext = mContext;
        this.interviewList = interviewList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_interview_adpater, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        final HashMap<String, String> map = interviewList.get(position);

        holder.tvUrl.setText(map.get("video"));
        holder.tvTile.setText(map.get("title"));

        holder.tvUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, MemberInterviewPlayerActivity.class);
                intent.putExtra("link", map.get("video"));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return interviewList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvUrl, tvTile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTile = itemView.findViewById(R.id.mem_interview_title);
            tvUrl = itemView.findViewById(R.id.mem_interview_url);

        }
    }
}
