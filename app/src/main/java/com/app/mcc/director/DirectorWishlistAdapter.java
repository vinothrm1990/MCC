package com.app.mcc.director;

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
import com.app.mcc.helper.Constants;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import java.util.ArrayList;
import java.util.HashMap;

public class DirectorWishlistAdapter extends RecyclerView.Adapter<DirectorWishlistAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<HashMap<String,String>> wishList;

    public DirectorWishlistAdapter(Context mContext, ArrayList<HashMap<String, String>> wishList) {
        this.mContext = mContext;
        this.wishList = wishList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.director_wishlist_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final HashMap<String,String> map = wishList.get(position);

        holder.tvTitle.setText(map.get("category"));
        holder.tvName.setText(map.get("f_name") + "\t" + (map.get("l_name")));
        Glide.with(mContext).load(Constants.IMAGE_URL + map.get("profile")).thumbnail(0.1f).into(holder.ivImage);

        holder.ivView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, DirectorWishlistDetailsActivity.class);
                intent.putExtra("data", map);
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return wishList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivView;
        CircularImageView ivImage;
        TextView tvName, tvTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivView = itemView.findViewById(R.id.dir_wishlist_view);
            ivImage = itemView.findViewById(R.id.dir_wishlist_iv);
            tvName = itemView.findViewById(R.id.dir_wishlist_name);
            tvTitle = itemView.findViewById(R.id.dir_wishlist_title);

        }
    }
}
