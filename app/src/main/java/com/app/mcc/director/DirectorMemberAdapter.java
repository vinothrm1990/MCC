package com.app.mcc.director;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mcc.R;
import com.app.mcc.helper.Constants;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class DirectorMemberAdapter extends RecyclerView.Adapter<DirectorMemberAdapter.MyViewHolder>{

    Context mContext;
    ArrayList<HashMap<String,String>> memberList;

    public DirectorMemberAdapter(Context mContext, ArrayList<HashMap<String, String>> memberList) {
        this.mContext = mContext;
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.director_member_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final HashMap<String,String> map = memberList.get(position);

        holder.tvTitle.setText(map.get("category"));
        holder.tvName.setText(map.get("f_name") + "\t" +map.get("l_name"));
        Glide.with(mContext).load(Constants.IMAGE_URL + map.get("profile")).thumbnail(0.1f).into(holder.cvImage);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivView;
        TextView tvName, tvTitle;
        CircularImageView cvImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivView = itemView.findViewById(R.id.dir_member_view);
            cvImage = itemView.findViewById(R.id.dir_member_iv);
            tvName = itemView.findViewById(R.id.dir_member_name_tv);
            tvTitle = itemView.findViewById(R.id.dir_member_title_tv);
        }
    }
}
