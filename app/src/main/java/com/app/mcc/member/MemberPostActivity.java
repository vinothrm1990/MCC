package com.app.mcc.member;

import android.app.ActionBar;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.mcc.R;
import com.app.mcc.director.DirectorCategoryFullDetailsActivity;
import com.app.mcc.helper.Constants;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MemberPostActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    RecyclerView recyclerView;
    ArrayList<HashMap<String,String>> postList;
    RequestQueue queue;
    MemberPostAdapter postAdapter;
    RecyclerView.LayoutManager layoutManager;
    Dialog progressDialog;
    String POST_URL = Constants.MEMBER_URL + Constants.GET_POST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_post);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("DIRECTOR'S POST");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        Constants.pref = getApplicationContext().getSharedPreferences("MCC",0);
        Constants.editor = Constants.pref.edit();

        queue = Volley.newRequestQueue(MemberPostActivity.this);

        postList = new ArrayList<>();
        recyclerView = findViewById(R.id.rv_mem_post);
        layoutManager = new LinearLayoutManager(MemberPostActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        jsonData();
    }

    private void jsonData() {

        progressDialog = new Dialog(MemberPostActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, POST_URL,
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
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);

                                    HashMap<String, String> map = new HashMap<>();

                                    String id = object.getString("id");
                                    String title = object.getString("title");
                                    String name = object.getString("name");
                                    String profile = object.getString("profileimage");
                                    String image = object.getString("image");
                                    String mobile = object.getString("mobileno");

                                    map.put("id", id);
                                    map.put("name", name);
                                    map.put("title", title);
                                    map.put("image", image);
                                    map.put("profileimage", profile);
                                    map.put("mobileno", mobile);

                                    postList.add(map);
                                }

                                postAdapter = new MemberPostAdapter(MemberPostActivity.this, postList);
                                recyclerView.setAdapter(postAdapter);

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("failed")){
                                progressDialog.hide();
                                KToast.errorToast(MemberPostActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_LONG);

                            }else {

                                progressDialog.hide();
                                KToast.errorToast(MemberPostActivity.this,
                                        "Something went Wrong!",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_LONG);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.hide();
                            KToast.errorToast(MemberPostActivity.this,
                                    e.getMessage(),
                                    Gravity.BOTTOM,
                                    KToast.LENGTH_LONG);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        KToast.errorToast(MemberPostActivity.this,
                                error.getMessage(),
                                Gravity.BOTTOM,
                                KToast.LENGTH_LONG);
                    }
                });
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
            KToast.warningToast(MemberPostActivity.this,
                    "Check your Internet Connection",
                    Gravity.BOTTOM,
                    KToast.LENGTH_SHORT);
        }
    }
}
