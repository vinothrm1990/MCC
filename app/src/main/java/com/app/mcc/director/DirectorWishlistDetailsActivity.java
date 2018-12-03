package com.app.mcc.director;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.mcc.R;
import com.app.mcc.helper.Constants;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DirectorWishlistDetailsActivity extends AppCompatActivity implements InternetConnectivityListener {

    HashMap<String, String> map;
    CircularImageView ivProfile;
    TextView tvName, tvTitle;
    ImageView ivWishlistFalse, ivWishlistTrue, ivVideo, ivAudio, ivPhoto;
    InternetAvailabilityChecker availabilityChecker;
    Dialog progressDialog;
    RequestQueue queue;
    int flag;
    String memid, dirphone, dirid;
    String ADD_URL = Constants.DIRECTOR_URL + Constants.ADD_REMOVE_WISHLIST;
    String REMOVE_URL = Constants.DIRECTOR_URL + Constants.ADD_REMOVE_WISHLIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_wishlist_details);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("MEMBER DETAILS");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        Constants.pref = getApplicationContext().getSharedPreferences("MCC",0);
        Constants.editor = Constants.pref.edit();

        map = (HashMap<String, String>) getIntent().getExtras().get("data");
        queue = Volley.newRequestQueue(this);

        ivProfile = findViewById(R.id.dir_wish_profile_iv);
        ivWishlistTrue = findViewById(R.id.dir_wish_true);
        ivWishlistFalse = findViewById(R.id.dir_wish_false);
        ivVideo = findViewById(R.id.dir_wish_video);
        ivAudio= findViewById(R.id.dir_wish_audio);
        ivPhoto = findViewById(R.id.dir_wish_photo);
        tvName = findViewById(R.id.dir_wish_name);
        tvTitle = findViewById(R.id.dir_wish_title);

        Glide.with(this).load(Constants.IMAGE_URL + map.get("profile")).thumbnail(0.1f).into(ivProfile);
        tvName.setText(map.get("f_name") + "\t" + (map.get("l_name")));
        tvTitle.setText(map.get("category"));
        memid = map.get("mem_id");
        dirphone= Constants.pref.getString("mobileno", "");
        dirid= Constants.pref.getString("id", "");

        int flagstatus = Integer.parseInt(map.get("flag"));
        if (flagstatus == 1){
            ivWishlistFalse.setVisibility(View.GONE);
            ivWishlistTrue.setVisibility(View.VISIBLE);
        }else{
            ivWishlistTrue.setVisibility(View.GONE);
            ivWishlistFalse.setVisibility(View.VISIBLE);
        }

        ivWishlistFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flag = 1;
                ivWishlistFalse.setVisibility(View.GONE);
                ivWishlistTrue.setVisibility(View.VISIBLE);
                addWishlist();
            }
        });

        ivWishlistTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flag = 2;
                ivWishlistTrue.setVisibility(View.GONE);
                ivWishlistFalse.setVisibility(View.VISIBLE);
                removeWishlist();
            }
        });

        ivVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        ivAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }

    private void removeWishlist() {

        progressDialog = new Dialog(DirectorWishlistDetailsActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, REMOVE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status")
                                    .equalsIgnoreCase("removed")){
                                progressDialog.hide();
                                KToast.successToast(DirectorWishlistDetailsActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("already")){
                                progressDialog.hide();
                                KToast.warningToast(DirectorWishlistDetailsActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("removed failed")){
                                progressDialog.hide();
                                KToast.errorToast(DirectorWishlistDetailsActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else {
                                KToast.errorToast(DirectorWishlistDetailsActivity.this,
                                        "Something Went Wrong!",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.hide();
                            KToast.errorToast(DirectorWishlistDetailsActivity.this,
                                    e.getMessage(),
                                    Gravity.BOTTOM,
                                    KToast.LENGTH_SHORT);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        KToast.errorToast(DirectorWishlistDetailsActivity.this,
                                error.getMessage(),
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String>  params = new HashMap<String, String>();
                params.put("id", dirid);
                params.put("mobileno", dirphone);
                params.put("member", memid);
                params.put("flag", String.valueOf(flag));
                return params;
            }
        };
        queue.add(request);
    }

    private void addWishlist() {

        progressDialog = new Dialog(DirectorWishlistDetailsActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, ADD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status")
                                    .equalsIgnoreCase("added")){
                                progressDialog.hide();
                                KToast.successToast(DirectorWishlistDetailsActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("already")){
                                progressDialog.hide();
                                KToast.warningToast(DirectorWishlistDetailsActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("adding failed")){
                                progressDialog.hide();
                                KToast.errorToast(DirectorWishlistDetailsActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else {
                                progressDialog.hide();
                                KToast.errorToast(DirectorWishlistDetailsActivity.this,
                                        "Something Went Wrong!",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.hide();
                            KToast.errorToast(DirectorWishlistDetailsActivity.this,
                                    e.getMessage(),
                                    Gravity.BOTTOM,
                                    KToast.LENGTH_SHORT);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        KToast.errorToast(DirectorWishlistDetailsActivity.this,
                                error.getMessage(),
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id", dirid);
                params.put("mobileno", dirphone);
                params.put("member", memid);
                params.put("flag", String.valueOf(flag));
                return params;
            }
        };
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, DirectorWishlistActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        availabilityChecker.removeInternetConnectivityChangeListener(this);
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (!isConnected) {
            KToast.warningToast(DirectorWishlistDetailsActivity.this,
                    "Check your Internet Connection",
                    Gravity.BOTTOM,
                    KToast.LENGTH_SHORT);
        }
    }
}
