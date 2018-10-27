package com.app.mcc.director;

import android.app.ActionBar;
import android.app.Dialog;
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
import com.app.mcc.activity.HomeActivity;
import com.app.mcc.helper.Constants;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DirectorHomeActivity extends AppCompatActivity implements InternetConnectivityListener {

    HashMap<String, String> map;
    CircularImageView ivProfile;
    TextView tvName, tvTitle;
    ImageView ivWishlistFalse, ivWishlistTrue, ivVideo, ivAudio, ivPhoto;
    InternetAvailabilityChecker availabilityChecker;
    Dialog progressDialog;
    RequestQueue queue;
    int flag;
    String memid, dirid;
    String ADD_URL = Constants.DIRECTOR_URL + Constants.ADD_REMOVE_WISHLIST;
    String REMOVE_URL = Constants.DIRECTOR_URL + Constants.ADD_REMOVE_WISHLIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_home);

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

        ivProfile = findViewById(R.id.home_profile_iv);
        ivWishlistTrue = findViewById(R.id.dir_home_wish_true);
        ivWishlistFalse = findViewById(R.id.dir_home_wish_false);
        ivVideo = findViewById(R.id.dir_home_video);
        ivAudio= findViewById(R.id.dir_home_audio);
        ivPhoto = findViewById(R.id.dir_home_photo);
        tvName = findViewById(R.id.dir_home_name);
        tvTitle = findViewById(R.id.dir_home_title);

        Glide.with(this).load(Constants.IMAGE_URL + map.get("profile")).thumbnail(0.1f).into(ivProfile);
        tvName.setText(map.get("f_name") + "\t" + (map.get("l_name")));
        tvTitle.setText(map.get("category"));
        memid = map.get("id");
        dirid = Constants.pref.getString("mobileno", "");

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

                flag = 0;
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

        progressDialog = new Dialog(this);
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

                                    KToast.successToast(DirectorHomeActivity.this,
                                            jsonObject.getString("message"),
                                            Gravity.BOTTOM,
                                            KToast.LENGTH_SHORT);

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("already")){
                                    KToast.warningToast(DirectorHomeActivity.this,
                                            jsonObject.getString("message"),
                                            Gravity.BOTTOM,
                                            KToast.LENGTH_SHORT);

                                }else {
                                KToast.errorToast(DirectorHomeActivity.this,
                                        "Something Went Wrong!",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            KToast.errorToast(DirectorHomeActivity.this,
                                    e.getMessage(),
                                    Gravity.BOTTOM,
                                    KToast.LENGTH_SHORT);
                        }

                        progressDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        KToast.errorToast(DirectorHomeActivity.this,
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
                params.put("mobileno", dirid);
                params.put("member", memid);
                params.put("flag", String.valueOf(flag));
                return params;
            }
        };
        queue.add(request);
    }

    private void addWishlist() {

        progressDialog = new Dialog(DirectorHomeActivity.this);
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

                                KToast.successToast(DirectorHomeActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("already")){
                                KToast.warningToast(DirectorHomeActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else {
                                KToast.errorToast(DirectorHomeActivity.this,
                                        "Something Went Wrong!",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            KToast.errorToast(DirectorHomeActivity.this,
                                    e.getMessage(),
                                    Gravity.BOTTOM,
                                    KToast.LENGTH_SHORT);
                        }

                        progressDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        KToast.errorToast(DirectorHomeActivity.this,
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
                params.put("mobileno", dirid);
                params.put("member", memid);
                params.put("flag", String.valueOf(flag));
                return params;
            }
        };
        queue.add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        availabilityChecker.removeInternetConnectivityChangeListener(this);
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (!isConnected) {
            KToast.warningToast(DirectorHomeActivity.this,
                    "Check your Internet Connection",
                    Gravity.BOTTOM,
                    KToast.LENGTH_SHORT);
        }
    }
}
