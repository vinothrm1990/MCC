package com.app.mcc.director;

import android.app.ActionBar;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class DirectorFeedbackActivity extends AppCompatActivity implements InternetConnectivityListener {

    AppCompatEditText etTitle, etDesc;
    Button btnSubmit;
    InternetAvailabilityChecker availabilityChecker;
    RequestQueue queue;
    Dialog progressDialog;
    String FEEDBACK_URL = Constants.DIRECTOR_URL + Constants.SEND_FEEDBACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_feedback);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        Constants.pref = getApplicationContext().getSharedPreferences("MCC", MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("SEND FEEDBACK");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        queue = Volley.newRequestQueue(this);

        etTitle = findViewById(R.id.dir_feed_title);
        etDesc = findViewById(R.id.dir_feed_desc);
        btnSubmit = findViewById(R.id.dir_feed_submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean validation = false;

                String title = etTitle.getText().toString().trim();
                if (title.equalsIgnoreCase("")){
                    validation = true;
                    etTitle.setError("Empty Feilds");
                }
                String desc = etDesc.getText().toString().trim();
                if (desc.equalsIgnoreCase("")){
                    validation = true;
                    etDesc.setError("Empty Feilds");
                }
                if (validation == false){

                    sendFeedback();
                }
            }
        });

    }

    private void sendFeedback() {

        progressDialog = new Dialog(DirectorFeedbackActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, FEEDBACK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status")
                                    .equalsIgnoreCase("success")){
                                progressDialog.hide();
                                KToast.successToast(DirectorFeedbackActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);


                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("failed")){
                                progressDialog.hide();
                                KToast.errorToast(DirectorFeedbackActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else {
                                progressDialog.hide();
                                KToast.errorToast(DirectorFeedbackActivity.this,
                                        "Something went Wrong",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.hide();
                            KToast.errorToast(DirectorFeedbackActivity.this,
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
                        KToast.errorToast(DirectorFeedbackActivity.this,
                                error.getMessage(),
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                String title = etTitle.getText().toString().trim();
                String desc = etDesc.getText().toString().trim();

                Map<String, String>  params = new HashMap<String, String>();
                params.put("fname", Constants.pref.getString("name", ""));
                //params.put("lname", Constants.pref.getString("mobileno", ""));
                params.put("email", Constants.pref.getString("email", ""));
                params.put("mobileno", Constants.pref.getString("mobileno", ""));
                params.put("title", title);
                params.put("desc", desc);
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
            KToast.warningToast(DirectorFeedbackActivity.this, "Check your Internet Connection",
                    Gravity.BOTTOM,
                    KToast.LENGTH_LONG);
        }
    }
}
