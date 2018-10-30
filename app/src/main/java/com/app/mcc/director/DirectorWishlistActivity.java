package com.app.mcc.director;

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
import java.util.Map;

public class DirectorWishlistActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    RecyclerView recyclerView;
    ArrayList<HashMap<String,String>> wishList;
    RecyclerView.LayoutManager layoutManager;
    RequestQueue queue;
    Dialog progressDialog;
    DirectorWishlistAdapter wishlistAdapter;
    String WISHLIST_URL = Constants.DIRECTOR_URL + Constants.GET_WISHLIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_wishlist);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("MY WISHLIST");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        Constants.pref = getApplicationContext().getSharedPreferences("MCC",0);
        Constants.editor = Constants.pref.edit();

        queue = Volley.newRequestQueue(this);

        wishList = new ArrayList<>();
        recyclerView = findViewById(R.id.rv_wishlist);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        jsonData();

    }

    private void jsonData() {

        progressDialog = new Dialog(DirectorWishlistActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, WISHLIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equalsIgnoreCase("success")){

                                String data = jsonObject.getString("message");
                                JSONArray array = new JSONArray(data);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);

                                    HashMap<String, String> map = new HashMap<>();

                                    String id = object.getString("w_id");
                                    String mid = object.getString("mem_id");
                                    String count = object.getString("count");
                                    String vid = object.getString("view_id");
                                    String flag = object.getString("flag");
                                    String fname = object.getString("f_name");
                                    String lname = object.getString("l_name");
                                    String title = object.getString("category");
                                    String image = object.getString("profile");

                                    map.put("w_id", id);
                                    map.put("mem_id", mid);
                                    map.put("count", count);
                                    map.put("view_id", vid);
                                    map.put("flag", flag);
                                    map.put("f_name", fname);
                                    map.put("l_name", lname);
                                    map.put("category", title);
                                    map.put("profile", image);

                                    wishList.add(map);
                                }

                                wishlistAdapter = new DirectorWishlistAdapter(DirectorWishlistActivity.this, wishList);
                                recyclerView.setAdapter(wishlistAdapter);

                            }else if (jsonObject.getString("status").equalsIgnoreCase("empty")){
                                KToast.warningToast(DirectorWishlistActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else if (jsonObject.getString("status").equalsIgnoreCase("failed")){
                                KToast.errorToast(DirectorWishlistActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else {

                                KToast.errorToast(DirectorWishlistActivity.this,
                                        "Something went Wrong!",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            KToast.errorToast(DirectorWishlistActivity.this,
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

                        KToast.errorToast(DirectorWishlistActivity.this,
                                error.getMessage(),
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                String mobile = Constants.pref.getString("mobileno", "");
                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobileno", mobile);
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
            KToast.warningToast(DirectorWishlistActivity.this,
                    "Check your Internet Connection",
                    Gravity.BOTTOM,
                    KToast.LENGTH_SHORT);
        }
    }
}
