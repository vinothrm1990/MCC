package com.app.mcc.director;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.app.mcc.member.MemberImageViewActivity;
import com.app.mcc.member.MemberVideoViewActivity;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DirectorCategoryFullDetailsActivity extends AppCompatActivity implements InternetConnectivityListener {

    HashMap<String, String> map;
    CircularImageView ivProfile;
    TextView tvName, tvEmail, tvPhone, tvLanguage, tvCategory, tvAge, tvGender, tvDob, tvQualify, tvAddress, tvCity;
    ImageView ivWishlistFalse, ivWishlistTrue, ivVideo, ivAudio, ivPhoto;
    InternetAvailabilityChecker availabilityChecker;
    Dialog progressDialog;
    RequestQueue queue;
    int flag;
    String memid, dirphone, dirid;
    String ADD_URL = Constants.DIRECTOR_URL + Constants.ADD_REMOVE_WISHLIST;
    String REMOVE_URL = Constants.DIRECTOR_URL + Constants.ADD_REMOVE_WISHLIST;
    String FLAG_URL = Constants.DIRECTOR_URL + Constants.GET_FLAG;
    String COUNT_URL = Constants.DIRECTOR_URL + Constants.ADD_COUNT;
    String photo, audio, video;
    MediaPlayer mediaPlayer;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_category_full_details);

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

        ivProfile = findViewById(R.id.dir_mem_profile);
        ivWishlistTrue = findViewById(R.id.dir_mem_wish_true);
        ivWishlistFalse = findViewById(R.id.dir_mem_wish_false);
        ivVideo = findViewById(R.id.dir_mem_video);
        ivAudio= findViewById(R.id.dir_mem_audio);
        ivPhoto = findViewById(R.id.dir_mem_photo);
        tvName = findViewById(R.id.dir_mem_profile_name);
        tvEmail = findViewById(R.id.dir_mem_profile_email);
        tvPhone = findViewById(R.id.dir_mem_profile_phone);
        tvLanguage = findViewById(R.id.dir_mem_profile_lang);
        tvCategory = findViewById(R.id.dir_mem_profile_cat);
        tvAge= findViewById(R.id.dir_mem_profile_age);
        tvGender = findViewById(R.id.dir_mem_profile_gender);
        tvDob = findViewById(R.id.dir_mem_profile_dob);
        tvAddress = findViewById(R.id.dir_mem_profile_address);
        tvCity = findViewById(R.id.dir_mem_profile_city);
        tvQualify = findViewById(R.id.dir_mem_profile_qualify);


        Glide.with(this).load(Constants.IMAGE_URL + map.get("profile")).thumbnail(0.1f).into(ivProfile);
        tvName.setText(map.get("f_name") + "\t" + (map.get("l_name")));
        tvPhone.setText(map.get("contact"));
        tvEmail.setText(map.get("email"));
        tvLanguage.setText(map.get("language"));
        tvAge.setText(map.get("age"));
        tvGender.setText(map.get("gender"));
        tvDob.setText(map.get("dob"));
        tvAddress.setText(map.get("address"));
        tvCity.setText(map.get("city"));
        tvQualify.setText(map.get("qualify"));
        tvCategory.setText(map.get("category"));
        memid = map.get("id");
        dirphone= Constants.pref.getString("mobileno", "");
        dirid= Constants.pref.getString("id", "");
        photo = map.get("upload_pic");
        audio = map.get("upload_audio");
        video = map.get("video");

        tvAddress.setSelected(true);
        tvCity.setSelected(true);

        sendCount();

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

                if (!video.isEmpty()){
                    Intent intent = new Intent(DirectorCategoryFullDetailsActivity.this, MemberVideoViewActivity.class);
                    intent.putExtra("url", video);
                    startActivity(intent);
                }else {
                    KToast.infoToast(DirectorCategoryFullDetailsActivity.this,
                            "No Video Available",
                            Gravity.BOTTOM,
                            KToast.LENGTH_SHORT);
                }
            }
        });

        ivAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!audio.isEmpty()){
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    audioPlayer();

                }else {
                    KToast.infoToast(DirectorCategoryFullDetailsActivity.this,
                            "No Audio Available",
                            Gravity.BOTTOM,
                            KToast.LENGTH_SHORT);
                }
            }
        });

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!photo.isEmpty()){
                    Intent intent = new Intent(DirectorCategoryFullDetailsActivity.this, MemberImageViewActivity.class);
                    intent.putExtra("url", photo);
                    startActivity(intent);
                }else{
                    KToast.infoToast(DirectorCategoryFullDetailsActivity.this,
                            "No Image Available",
                            Gravity.BOTTOM,
                            KToast.LENGTH_SHORT);
                }
            }
        });

        getFlag();
    }

    private void sendCount() {

        progressDialog = new Dialog(DirectorCategoryFullDetailsActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, COUNT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status")
                                    .equalsIgnoreCase("success")){
                                progressDialog.hide();

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("empty")){
                                progressDialog.hide();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.hide();
                            KToast.errorToast(DirectorCategoryFullDetailsActivity.this,
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
                        KToast.errorToast(DirectorCategoryFullDetailsActivity.this,
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
                params.put("memid", memid);
                params.put("dirid", dirid);
                return params;
            }
        };
        queue.add(request);

    }

    private void audioPlayer() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DirectorCategoryFullDetailsActivity.this);
        LayoutInflater inflater = DirectorCategoryFullDetailsActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.player_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        ImageView ivClose = dialogView.findViewById(R.id.mem_music_close);
        ImageView ivPlay = dialogView.findViewById(R.id.mem_music_play);
        ImageView ivStop = dialogView.findViewById(R.id.mem_music_stop);
        TextView tvSong = dialogView.findViewById(R.id.mem_music_name);
        final ProgressBar bar = dialogView.findViewById(R.id.mem_progress);

        alertDialog = dialogBuilder.create();

        String aFile = Constants.MEM_AUDIO_URL + audio;
        String audioFile = aFile.substring(aFile.lastIndexOf("/")+1);
        tvSong.setText(audioFile);
        tvSong.setSelected(true);

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    String audioFile = Constants.MEM_AUDIO_URL + audio;
                    mediaPlayer.setDataSource(audioFile);
                    mediaPlayer.prepare();

                    KToast.infoToast(DirectorCategoryFullDetailsActivity.this,
                            "Playing",
                            Gravity.BOTTOM,
                            KToast.LENGTH_SHORT);

                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                mediaPlayer.start();
            }
        });

        ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediaPlayer.stop();

                KToast.infoToast(DirectorCategoryFullDetailsActivity.this,
                        "Stoped",
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void getFlag() {

        progressDialog = new Dialog(DirectorCategoryFullDetailsActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, FLAG_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status")
                                    .equalsIgnoreCase("success")){
                                progressDialog.hide();

                                String data = jsonObject.getString("message");
                                JSONArray array = new JSONArray(data);
                                JSONObject object = array.getJSONObject(0);

                                int flagstatus = Integer.parseInt(object.getString("flag"));
                                if (flagstatus == 1){
                                    ivWishlistFalse.setVisibility(View.GONE);
                                    ivWishlistTrue.setVisibility(View.VISIBLE);
                                }

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("empty")){
                                progressDialog.hide();
                                ivWishlistTrue.setVisibility(View.GONE);
                                ivWishlistFalse.setVisibility(View.VISIBLE);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.hide();
                            KToast.errorToast(DirectorCategoryFullDetailsActivity.this,
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
                        KToast.errorToast(DirectorCategoryFullDetailsActivity.this,
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
                params.put("member", memid);
                return params;
            }
        };
        queue.add(request);
    }

    private void removeWishlist() {

        progressDialog = new Dialog(DirectorCategoryFullDetailsActivity.this);
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

                                KToast.successToast(DirectorCategoryFullDetailsActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("already")){
                                KToast.warningToast(DirectorCategoryFullDetailsActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("removed failed")){
                                KToast.errorToast(DirectorCategoryFullDetailsActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else {
                                KToast.errorToast(DirectorCategoryFullDetailsActivity.this,
                                        "Something Went Wrong!",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            KToast.errorToast(DirectorCategoryFullDetailsActivity.this,
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

                        KToast.errorToast(DirectorCategoryFullDetailsActivity.this,
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

        progressDialog = new Dialog(DirectorCategoryFullDetailsActivity.this);
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

                                KToast.successToast(DirectorCategoryFullDetailsActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("already")){
                                KToast.warningToast(DirectorCategoryFullDetailsActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("adding failed")){
                                KToast.errorToast(DirectorCategoryFullDetailsActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else {
                                KToast.errorToast(DirectorCategoryFullDetailsActivity.this,
                                        "Something Went Wrong!",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            KToast.errorToast(DirectorCategoryFullDetailsActivity.this,
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

                        KToast.errorToast(DirectorCategoryFullDetailsActivity.this,
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
    protected void onDestroy() {
        super.onDestroy();
        availabilityChecker.removeInternetConnectivityChangeListener(this);

        if (progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (!isConnected) {
            KToast.warningToast(DirectorCategoryFullDetailsActivity.this,
                    "Check your Internet Connection",
                    Gravity.BOTTOM,
                    KToast.LENGTH_SHORT);
        }
    }
}
