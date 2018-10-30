package com.app.mcc.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
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
import com.onurkaganaldemir.ktoastlib.KToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import static com.app.mcc.helper.Constants.editor;

/**
 * A simple {@link Fragment} subclass.
 */
public class DirectorLoginFragment extends Fragment {

    AppCompatEditText etPhone, etPass;
    Button btnLogin;
    TextView tvForgot;
    String phone, pass, oldPass;
    Dialog progressDialog;
    String LOGIN_URL = Constants.DIRECTOR_URL + Constants.LOGIN;
    String FORGOT_URL = Constants.DIRECTOR_URL + Constants.FORGOT_PASSWORD;
    RequestQueue queue;
    AlertDialog passDialog;

    public DirectorLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_director, container, false);

        Constants.pref = getActivity().getApplicationContext().getSharedPreferences("MCC", 0);
        Constants.editor = Constants.pref.edit();

        etPhone = view.findViewById(R.id.dir_log_phone);
        etPass = view.findViewById(R.id.dir_log_pass);
        tvForgot = view.findViewById(R.id.dir_log_forgot);
        btnLogin = view.findViewById(R.id.dir_log_login);

        queue = Volley.newRequestQueue(getActivity());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean validation = false;

                phone = etPhone.getText().toString().trim();
                if (!isValidPhone(phone)){
                    etPhone.setError("Invalid Number");
                    validation =  true;
                }
                pass = etPass.getText().toString().trim();
                if (!isValidPass(pass)){
                    etPass.setError("Invalid Password");
                    validation =  true;
                }
                if (validation ==  false){

                    jsonData();
                }
            }
        });

        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                forgotPassword();
            }
        });
        return view;
    }

    private void forgotPassword() {

        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.password_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        Button getPass = dialogView.findViewById(R.id.btn_get_pass);
        final AppCompatEditText etPhone = dialogView.findViewById(R.id.dir_pass_phone);
        ImageView ivClose = dialogView.findViewById(R.id.dir_pass_close);

        passDialog = dialogBuilder.create();

        getPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                oldPass = etPhone.getText().toString().trim();
                getPassword();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                passDialog.dismiss();
            }
        });
        passDialog.show();
    }

    private void getPassword() {

        progressDialog = new Dialog(getActivity());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, FORGOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equalsIgnoreCase("success")){

                                String password = jsonObject.getString("password");
                                KToast.successToast(getActivity(),
                                        "Your Password is : " + password,
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else {
                                KToast.errorToast(getActivity(),
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            KToast.errorToast(getActivity(),
                                    e.getMessage(),
                                    Gravity.BOTTOM,
                                    KToast.LENGTH_LONG);
                        }

                        progressDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        KToast.errorToast(getActivity(),
                                error.getMessage(),
                                Gravity.BOTTOM,
                                KToast.LENGTH_LONG);
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobileno", oldPass);
                return params;
            }
        };
        queue.add(request);
    }

    private void jsonData() {

        progressDialog = new Dialog(getActivity());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equalsIgnoreCase("success")){

                                String type = jsonObject.getString("type");
                                Constants.editor.putString("type", type);
                                Constants.editor.putBoolean("isLogged", true);
                                Constants.editor.apply();
                                Constants.editor.commit();
                                String data = jsonObject.getString("data");
                                JSONArray array = new JSONArray(data);
                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject object = array.getJSONObject(i);
                                    String id = object.getString("id");
                                    String mobile = object.getString("mobileno");
                                    String password = object.getString("password");
                                    String name = object.getString("name");
                                    String email = object.getString("email");
                                    Constants.editor.putString("id", id);
                                    Constants.editor.putString("mobileno", mobile);
                                    Constants.editor.putString("password", password);
                                    Constants.editor.putString("name", name);
                                    Constants.editor.putString("email", email);
                                    Constants.editor.apply();
                                    Constants.editor.commit();
                                }

                                KToast.successToast(getActivity(),
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                                startActivity(new Intent(getActivity(), HomeActivity.class));

                            }else {
                                KToast.errorToast(getActivity(),
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            KToast.errorToast(getActivity(),
                                    e.getMessage(),
                                    Gravity.BOTTOM,
                                    KToast.LENGTH_LONG);
                        }

                        progressDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        KToast.errorToast(getActivity(),
                                error.getMessage(),
                                Gravity.BOTTOM,
                                KToast.LENGTH_LONG);
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobileno", phone);
                params.put("password", pass);
                return params;
            }
        };
        queue.add(request);

    }

    private boolean isValidPhone(String phone) {

        if (phone != null && phone.length() >= 10){
            return true;
        }
        return false;
    }

    private boolean isValidPass(String pass) {

        if (pass != null && pass.length() > 3) {
            return true;
        }
        return false;
    }

}
