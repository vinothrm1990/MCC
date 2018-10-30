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
import java.util.ArrayList;
import java.util.HashMap;


public class MemberTrailerAdapter extends RecyclerView.Adapter<MemberTrailerAdapter.MyViewHolder>  {

    Context mContext;
    ArrayList<HashMap<String,String>> trailerList;

    public MemberTrailerAdapter(Context mContext, ArrayList<HashMap<String, String>> trailerList) {
        this.mContext = mContext;
        this.trailerList = trailerList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_trailer_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final HashMap<String, String> map = trailerList.get(position);

        holder.tvTitle.setText(map.get("title"));
        holder.tvTitle.setSelected(true);
        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, MemberTrailerPlayerActivity.class);
                intent.putExtra("link", map.get("url"));
                mContext.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView ivPlay;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.mem_trailer_title);
            ivPlay = itemView.findViewById(R.id.mem_trailer_play);
        }
    }

}
