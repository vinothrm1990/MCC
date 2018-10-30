package com.app.mcc.member;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
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
        Glide.with(mContext).load(Constants.PROFILE_URL + map.get("profileimage")).thumbnail(0.1f).into(holder.cvProfile);
        Glide.with(mContext).load(Constants.POST_IMAGE_URL + map.get("image")).thumbnail(0.1f).into(holder.ivImage);

        DOWNLOAD_URL = Constants.POST_IMAGE_URL + map.get("image");
        holder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveImage();
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
        ImageView ivImage;
        FloatingActionButton fab;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.mem_post_title_name);
            tvName = itemView.findViewById(R.id.mem_post_dir_name);
            ivImage = itemView.findViewById(R.id.mem_post_image);
            cvProfile = itemView.findViewById(R.id.mem_post_dir_image);
            fab = itemView.findViewById(R.id.mem_post_download);
        }
    }

    private void saveImage() {

        queue = Volley.newRequestQueue(mContext);

        progressDialog = new Dialog(mContext);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        ImageRequest imageRequest  = new ImageRequest(DOWNLOAD_URL,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {

                        saveImageToInternalStorage(response);
                        progressDialog.hide();
                        KToast.errorToast((Activity) mContext,
                                String.valueOf(response),
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);

                    }
                },
                0, // Image width
                0, // Image height
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.hide();
                        KToast.errorToast((Activity) mContext,
                                error.getMessage(),
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
                    }
                });
        queue.add(imageRequest);
    }

    private Uri saveImageToInternalStorage(Bitmap bitmap) {

        // Initialize ContextWrapper
        ContextWrapper wrapper = new ContextWrapper(mContext);

        // Initializing a new file
        // The bellow line return a directory in internal storage
        File file = wrapper.getDir("MCC",0);

        // Create a file to save the image
        file = new File(file, "MCC"+ getTime()+".jpg");

        try{
            // Initialize a new OutputStream
            OutputStream stream = null;

            // If the output file exists, it can be replaced or appended to it
            stream = new FileOutputStream(file);

            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

            // Flushes the stream
            stream.flush();

            // Closes the stream
            stream.close();

        }catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }

        // Parse the gallery image url to uri
        Uri savedImageURI = Uri.parse(file.getAbsolutePath());

        // Return the saved image Uri
        return savedImageURI;

    }

    public Date getTime(){

        long millis=System.currentTimeMillis();
        Date date = new Date(millis);
        return date;
    }
}
