package com.app.mcc.director;

import android.app.ActionBar;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
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

public class DirectorCategoryDetailsActivity extends AppCompatActivity implements InternetConnectivityListener {

    RecyclerView recyclerView;
    ArrayList<HashMap<String,String>> categoryList;
    DirectorCategoryDetailsAdapter categoryAdapter;
    RecyclerView.LayoutManager layoutManager;
    RequestQueue queue;
    HashMap<String, String> map;
    Dialog progressDialog;
    String mapData;
    InternetAvailabilityChecker availabilityChecker;
    String CATEGORY_URL = Constants.DIRECTOR_URL + Constants.GET_CATEGORY_MEMBER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_category_details);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        Constants.pref = getApplicationContext().getSharedPreferences("MCC",0);
        Constants.editor = Constants.pref.edit();

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("CATEGORY DETAILS");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        map = (HashMap<String, String>) getIntent().getExtras().get("data");

        if (!map.isEmpty()){
            mapData = map.get("title");
            Constants.editor.putString("mapData", map.get("title"));
            Constants.editor.apply();
            Constants.editor.commit();
        }else {
           mapData = Constants.pref.getString("mapdata", "");
        }

        queue = Volley.newRequestQueue(DirectorCategoryDetailsActivity.this);

        categoryList = new ArrayList<>();
        recyclerView = findViewById(R.id.rv_category_details);
        layoutManager = new LinearLayoutManager(DirectorCategoryDetailsActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        jsonData();

    }

    private void jsonData() {

        progressDialog = new Dialog(DirectorCategoryDetailsActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, CATEGORY_URL,
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
                                    String fname = object.getString("f_name");
                                    String lname = object.getString("l_name");
                                    String profile = object.getString("profile");
                                    String category = object.getString("category");
                                    String language = object.getString("language");
                                    String email = object.getString("email");
                                    String phone = object.getString("contact");
                                    String address = object.getString("address");
                                    String city = object.getString("city");
                                    String age = object.getString("age");
                                    String dob = object.getString("dob");
                                    String gender = object.getString("gender");
                                    String qualify = object.getString("qualify");

                                    map.put("id", id);
                                    map.put("f_name", fname);
                                    map.put("l_name", lname);
                                    map.put("profile", profile);
                                    map.put("category", category);
                                    map.put("language", language);
                                    map.put("email", email);
                                    map.put("contact", phone);
                                    map.put("address", address);
                                    map.put("city", city);
                                    map.put("age", age);
                                    map.put("dob", dob);
                                    map.put("gender", gender);
                                    map.put("qualify", qualify);

                                    categoryList.add(map);

                                }

                                categoryAdapter = new DirectorCategoryDetailsAdapter(DirectorCategoryDetailsActivity.this, categoryList);
                                recyclerView.setAdapter(categoryAdapter);

                            }else {
                                progressDialog.hide();
                                KToast.errorToast(DirectorCategoryDetailsActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.hide();
                            KToast.errorToast(DirectorCategoryDetailsActivity.this,
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
                        KToast.errorToast(DirectorCategoryDetailsActivity.this,
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
                params.put("cat", mapData);
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
            KToast.warningToast(DirectorCategoryDetailsActivity.this,
                    "Check your Internet Connection",
                    Gravity.BOTTOM,
                    KToast.LENGTH_SHORT);
        }
    }
}
