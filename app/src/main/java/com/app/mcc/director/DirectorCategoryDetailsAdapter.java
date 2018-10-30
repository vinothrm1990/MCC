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

public class DirectorCategoryDetailsAdapter extends RecyclerView.Adapter<DirectorCategoryDetailsAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<HashMap<String,String>> catMemberList;


    public DirectorCategoryDetailsAdapter(Context mContext, ArrayList<HashMap<String, String>> catMemberList) {
        this.mContext = mContext;
        this.catMemberList = catMemberList;
    }

    @NonNull
    @Override
    public DirectorCategoryDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.director_category_details_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DirectorCategoryDetailsAdapter.MyViewHolder holder, int position) {


        final HashMap<String,String> map = catMemberList.get(position);

        holder.tvName.setText(map.get("f_name")+ "" + map.get("l_name"));
        holder.tvtitle.setText(map.get("category"));
        Glide.with(mContext).load(Constants.IMAGE_URL + map.get("profile")).thumbnail(0.1f).into(holder.cvImage);

        holder.ivView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, DirectorCategoryFullDetailsActivity.class);
                intent.putExtra("data", map);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return catMemberList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircularImageView cvImage;
        TextView tvName, tvtitle;
        ImageView ivView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cvImage = itemView.findViewById(R.id.cat_member_iv);
            tvName = itemView.findViewById(R.id.cat_member_name_tv);
            tvtitle = itemView.findViewById(R.id.cat_member_title_tv);
            ivView = itemView.findViewById(R.id.cat_member_view);

        }
    }
}
