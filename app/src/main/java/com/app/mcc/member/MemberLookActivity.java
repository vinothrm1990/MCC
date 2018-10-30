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
import com.app.mcc.helper.Constants;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MemberLookActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    RecyclerView recyclerView;
    ArrayList<HashMap<String,String>> lookList;
    RequestQueue queue;
    MemberLookAdapter lookAdapter;
    RecyclerView.LayoutManager layoutManager;
    Dialog progressDialog;
    String LOOK_URL = Constants.MEMBER_URL + Constants.GET_LOOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_look);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("FIRST LOOK");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        Constants.pref = getApplicationContext().getSharedPreferences("MCC",0);
        Constants.editor = Constants.pref.edit();

        queue = Volley.newRequestQueue(MemberLookActivity.this);

        lookList = new ArrayList<>();
        recyclerView = findViewById(R.id.rv_mem_look);
        layoutManager = new LinearLayoutManager(MemberLookActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        jsonData();
    }

    private void jsonData() {

        progressDialog = new Dialog(MemberLookActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, LOOK_URL,
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
                                    String image = object.getString("image");

                                    map.put("id", id);
                                    map.put("title", title);
                                    map.put("image", image);

                                    lookList.add(map);
                                }

                                lookAdapter = new MemberLookAdapter(MemberLookActivity.this, lookList);
                                recyclerView.setAdapter(lookAdapter);

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("failed")){
                                progressDialog.hide();
                                KToast.errorToast(MemberLookActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else {

                                progressDialog.hide();
                                KToast.errorToast(MemberLookActivity.this,
                                        "Something went Wrong!",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.hide();
                            KToast.errorToast(MemberLookActivity.this,
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
                        KToast.errorToast(MemberLookActivity.this,
                                error.getMessage(),
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
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
            KToast.warningToast(MemberLookActivity.this,
                    "Check your Internet Connection",
                    Gravity.BOTTOM,
                    KToast.LENGTH_SHORT);
        }
    }
}
