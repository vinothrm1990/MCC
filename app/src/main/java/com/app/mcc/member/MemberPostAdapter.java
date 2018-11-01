package com.app.mcc.member;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.app.mcc.R;
import com.app.mcc.helper.Constants;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MemberPostAdapter extends RecyclerView.Adapter<MemberPostAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<HashMap<String,String>> postList;
    RequestQueue queue;
    Dialog progressDialog;
    String DOWNLOAD_URL;

    public MemberPostAdapter(Context mContext, ArrayList<HashMap<String, String>> postList) {
        this.mContext = mContext;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_post_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final HashMap<String,String> map = postList.get(position);

        holder.tvTitle.setText(map.get("title"));
        holder.tvName.setText(map.get("name"));
        Glide.with(mContext).load(Constants.DIR_PROFILE_URL + map.get("profileimage")).thumbnail(0.1f).into(holder.cvProfile);
        Glide.with(mContext).load(Constants.POST_IMAGE_URL + map.get("image")).thumbnail(0.1f).into(holder.ivImage);

        DOWNLOAD_URL = Constants.POST_IMAGE_URL + map.get("image");
        holder.ivView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, MemberPostViewActivity.class);
                intent.putExtra("image", map.get("image"));
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvName;
        CircularImageView cvProfile;
        ImageView ivImage, ivView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.mem_post_title_name);
            tvName = itemView.findViewById(R.id.mem_post_dir_name);
            ivImage = itemView.findViewById(R.id.mem_post_image);
            cvProfile = itemView.findViewById(R.id.mem_post_dir_image);
            ivView = itemView.findViewById(R.id.mem_post_download);
        }
    }

}
