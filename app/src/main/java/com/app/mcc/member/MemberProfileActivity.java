package com.app.mcc.member;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.app.mcc.activity.HomeActivity;
import com.app.mcc.director.DirectorProfileActivity;
import com.app.mcc.helper.Constants;
import com.app.mcc.helper.FilePath;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MemberProfileActivity extends AppCompatActivity implements InternetConnectivityListener {

    ImageView ivEdit, ivPhoto, ivAudio, ivVideo;
    CircularImageView ivProfile;
    AppCompatEditText etName, etEmail, etFb;
    TextView tvPhone, tvCategory;
    InternetAvailabilityChecker availabilityChecker;
    RequestQueue queue;
    Dialog progressDialog;
    String photo, audio, video;
    AlertDialog alertDialog;
    MediaPlayer mediaPlayer;
    String PROFILE_URL = Constants.MEMBER_URL + Constants.GET_PROFILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profile);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        Constants.pref = getApplicationContext().getSharedPreferences("MCC",0);
        Constants.editor = Constants.pref.edit();

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("MY PROFILE");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        ivEdit = findViewById(R.id.mem_profile_edit);
        ivProfile = findViewById(R.id.mem_profile_image);
        etName = findViewById(R.id.mem_profile_name);
        etEmail = findViewById(R.id.mem_profile_email);
        etFb = findViewById(R.id.mem_profile_fb);
        tvPhone = findViewById(R.id.mem_profile_phone);
        tvCategory = findViewById(R.id.mem_profile_cat);
        ivPhoto = findViewById(R.id.mem_profile_view_photo);
        ivAudio = findViewById(R.id.mem_profile_view_audio);
        ivVideo = findViewById(R.id.mem_profile_view_video);

        queue = Volley.newRequestQueue(this);

        getProfile();

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!photo.isEmpty()){
                    Intent intent = new Intent(MemberProfileActivity.this, MemberImageViewActivity.class);
                    intent.putExtra("url", photo);
                    startActivity(intent);
                }else {
                    KToast.infoToast(MemberProfileActivity.this,
                            "No Image Available",
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
                    KToast.infoToast(MemberProfileActivity.this,
                            "No Audio Available",
                            Gravity.BOTTOM,
                            KToast.LENGTH_SHORT);
                }
            }
        });

        ivVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!video.isEmpty()){
                    Intent intent = new Intent(MemberProfileActivity.this, MemberVideoViewActivity.class);
                    intent.putExtra("url", video);
                    startActivity(intent);
                }else {
                    KToast.infoToast(MemberProfileActivity.this,
                            "No Video Available",
                            Gravity.BOTTOM,
                            KToast.LENGTH_SHORT);
                }
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MemberProfileActivity.this, MemberProfileEditActivity.class));
            }
        });

    }

    private void audioPlayer() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MemberProfileActivity.this);
        LayoutInflater inflater = MemberProfileActivity.this.getLayoutInflater();
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

                    KToast.infoToast(MemberProfileActivity.this,
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

                KToast.infoToast(MemberProfileActivity.this,
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


    private void getProfile() {

        progressDialog = new Dialog(MemberProfileActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, PROFILE_URL,
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

                                String fname = object.getString("f_name");
                                String lname = object.getString("l_name");
                                String fid = object.getString("fb_id");
                                String email = object.getString("email");
                                String mobile = object.getString("contact");
                                String category = object.getString("category");
                                String profile = object.getString("profile");
                                photo = object.getString("upload_pic");
                                audio = object.getString("upload_audio");
                                video = object.getString("video");

                                etName.setText(fname + "" + lname);
                                etEmail.setText(email);
                                tvPhone.setText(mobile);
                                tvCategory.setText(category);
                                etFb.setText(fid);

                                Constants.editor.putString("profile", profile);
                                Constants.editor.apply();
                                Constants.editor.commit();

                                String profileImage = Constants.MEM_PROFILE_URL + profile;
                                if (!profile.isEmpty()){
                                    Glide.with(MemberProfileActivity.this).load(profileImage).thumbnail(0.1f).into(ivProfile);
                                }else {
                                    Glide.with(MemberProfileActivity.this).load(R.drawable.logo).thumbnail(0.1f).into(ivProfile);
                                }

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("failed")){
                                progressDialog.hide();
                                KToast.warningToast(MemberProfileActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("empty")){
                                progressDialog.hide();
                                KToast.warningToast(MemberProfileActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }else {
                                progressDialog.hide();
                                KToast.errorToast(MemberProfileActivity.this,
                                        "Something Went Wrong!",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.hide();
                            KToast.errorToast(MemberProfileActivity.this,
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
                        KToast.errorToast(MemberProfileActivity.this,
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
                params.put("mobileno", Constants.pref.getString("phone", ""));
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
            KToast.warningToast(MemberProfileActivity.this,
                    "Check your Internet Connection",
                    Gravity.BOTTOM,
                    KToast.LENGTH_SHORT);
        }
    }
}
