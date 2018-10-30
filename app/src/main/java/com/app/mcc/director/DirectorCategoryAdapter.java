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

public class DirectorCategoryAdapter extends RecyclerView.Adapter<DirectorCategoryAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<HashMap<String,String>> categoryList;

    public DirectorCategoryAdapter(Context mContext, ArrayList<HashMap<String, String>> categoryList) {
        this.mContext = mContext;
        this.categoryList = categoryList;
    }


    @NonNull
    @Override
    public DirectorCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.director_category_adpater, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DirectorCategoryAdapter.MyViewHolder holder, int position) {

        final HashMap<String,String> map = categoryList.get(position);

        holder.tvTitle.setText(map.get("title"));
        Glide.with(mContext).load(map.get("image")).thumbnail(0.1f).into(holder.ivImage);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, DirectorCategoryDetailsActivity.class);
                intent.putExtra("data", map);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        TextView tvTitle;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.dir_category_iv);
            tvTitle= itemView.findViewById(R.id.dir_category_tv);
            cardView = itemView.findViewById(R.id.cv_dir_category);
        }
    }
}
