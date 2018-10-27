package com.app.mcc.director;

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
import com.app.mcc.helper.Constants;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.HashMap;

public class DirectorHomeAdapter extends RecyclerView.Adapter<DirectorHomeAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<HashMap<String,String>> homeList;

    public DirectorHomeAdapter(Context mContext, ArrayList<HashMap<String, String>> homeList) {
        this.mContext = mContext;
        this.homeList = homeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.director_home_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final HashMap<String,String> map = homeList.get(position);

        holder.tvTitle.setText(map.get("category"));
        holder.tvName.setText(map.get("f_name") + "\t" + (map.get("l_name")));
        Glide.with(mContext).load(Constants.IMAGE_URL + map.get("profile")).thumbnail(0.1f).into(holder.ivImage);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, DirectorHomeActivity.class);
                intent.putExtra("data", map);
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return homeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvName;
        ImageView ivImage;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.home_title_tv);
            tvName = itemView.findViewById(R.id.home_name_tv);
            ivImage = itemView.findViewById(R.id.home_profile_iv);
            cardView = itemView.findViewById(R.id.cv_home_guest);
        }
    }
}
