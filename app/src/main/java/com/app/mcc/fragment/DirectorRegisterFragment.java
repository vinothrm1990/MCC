package com.app.mcc.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.mcc.R;
import com.app.mcc.activity.HomeActivity;
import com.app.mcc.activity.StartActivity;
import com.app.mcc.helper.Constants;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.poovam.pinedittextfield.LinePinField;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */

public class DirectorRegisterFragment extends Fragment {

    AppCompatEditText etFName, etLName, etEmail, etPhone, etPass, etCPass;
    ScrollView directorLayout;
    CheckBox cbAgree;
    Button btnRegister, btnVerify;
    ImageView ivVerified;
    Dialog progressDialog;
    String URL = Constants.DIRECTOR_URL + Constants.REGISTER;
    String OTP_URL = Constants.DIRECTOR_URL + Constants.GET_OTP;
    String VALIDATE_URL = Constants.DIRECTOR_URL + Constants.VALIDATE_OTP;
    String fname, lname, email, phone, cpass;
    RequestQueue queue;
    Spinner spinLanguage, spinCategory;
    String categories, languages, flag;
    AlertDialog getOtpDialog, validateOtpDialog;
    LinePinField etOtp;

    public DirectorRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_director, container, false);

        Constants.pref = getActivity().getApplicationContext().getSharedPreferences("MCC", MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        Constants.editor.remove("flag");
        Constants.editor.apply();
        Constants.editor.commit();

        directorLayout = view.findViewById(R.id.director_layout);
        spinLanguage = view.findViewById(R.id.dir_spin_lang);
        spinCategory = view.findViewById(R.id.dir_spin_cat);
        etFName = view.findViewById(R.id.dir_reg_fname);
        etLName = view.findViewById(R.id.dir_reg_lname);
        etEmail = view.findViewById(R.id.dir_reg_email);
        etPhone = view.findViewById(R.id.dir_reg_phone);
        etPass = view.findViewById(R.id.dir_reg_pass);
        etCPass = view.findViewById(R.id.dir_reg_cpass);
        cbAgree = view.findViewById(R.id.dir_reg_agree);
        btnRegister = view.findViewById(R.id.dir_reg_register);
        btnVerify = view.findViewById(R.id.dir_reg_verify);
        ivVerified = view.findViewById(R.id.dir_reg_verified);

        queue = Volley.newRequestQueue(getActivity());

        List<String> language = new ArrayList<>();
        language.add("Tamil");
        language.add("Malayalam");
        language.add("Telugu");
        language.add("Kannada");
        language.add("English");
        language.add("Other Languages");

        // Creating adapter for spinner
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, language);
        // Drop down layout style - list view with radio button
        languageAdapter.setDropDownViewResource(R.layout.spinner_layout);
        // attaching data adapter to spinner
        spinLanguage.setAdapter(languageAdapter);

        spinLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                languages = adapterView.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        List<String> category = new ArrayList<>();
        category.add("Director");
        category.add("Producer");
        category.add("Music Director");

        // Creating adapter for spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, category);
        // Drop down layout style - list view with radio button
        categoryAdapter.setDropDownViewResource(R.layout.spinner_layout);
        // attaching data adapter to spinner
        spinCategory.setAdapter(categoryAdapter);

        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                categories = adapterView.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnVerify.setEnabled(false);

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().trim().length() < 10){
                    btnVerify.setEnabled(false);
                }else {
                    btnVerify.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.otp_layout, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);

                Button btnGet = dialogView.findViewById(R.id.btn_get_otp);
                final AppCompatEditText etPhoneVerify = dialogView.findViewById(R.id.dir_otp_get);
                ImageView ivClose = dialogView.findViewById(R.id.dir_otp_close);

                String phone = etPhone.getText().toString().trim();
                etPhoneVerify.setText(phone);

                getOtpDialog = dialogBuilder.create();

                btnGet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        getOtp();
                    }
                });
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        getOtpDialog.dismiss();
                    }
                });
                getOtpDialog.show();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean validation = false;

                fname = etFName.getText().toString().trim();
                if (!isValidFName(fname)){
                    etFName.setError("Empty Feilds");
                    validation = true;
                }
                lname = etLName.getText().toString().trim();
                if (!isValidLName(lname)){
                    etLName.setError("Empty Feilds");
                    validation = true;
                }
                email = etEmail.getText().toString().trim();
                if (!isValidEmail(email)){
                    etEmail.setError("Invalid Email");
                    validation = true;
                }
                phone = etPhone.getText().toString().trim();
                if (!isValidPhone(phone)){
                    etPhone.setError("Invalid Number");
                    validation = true;
                }
                flag = Constants.pref.getString("flag", "");
                if (!isVerified(flag)){
                    etPhone.setError("Number not Verified");
                    validation = true;
                }
                String pass = etPass.getText().toString().trim();
                if (!isValidPass(pass)){
                    etPass.setError("Invalid Password");
                    validation = true;
                }
                cpass = etCPass.getText().toString().trim();
                if (!isValidCPass(cpass)){
                    etCPass.setError("Invalid Confirm Password");
                    validation = true;
                }
                if (!cbAgree.isChecked()) {
                    KToast.warningToast(getActivity(),"You must have agree to our Terms & Conditions",
                            Gravity.BOTTOM,
                            KToast.LENGTH_LONG);
                    validation = true;
                }
                if (validation == false){
                    jsonData();
                }
            }
        });

        return view;
    }

    private void getOtp() {

        progressDialog = new Dialog(getActivity());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, OTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equalsIgnoreCase("success")){
                                getOtpDialog.dismiss();
                                String otp = jsonObject.getString("otp");
                                Constants.editor.putString("otp", otp);
                                Constants.editor.apply();
                                Constants.editor.commit();
                                verifyDialog();
                                KToast.successToast(getActivity(),
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else {

                                KToast.errorToast(getActivity(),
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            KToast.errorToast(getActivity(),e.getMessage(),
                                    Gravity.BOTTOM,
                                    KToast.LENGTH_SHORT);
                        }
                        progressDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                phone = etPhone.getText().toString().trim();
                params.put("mobileno", phone);
                return params;
            }
        };
        queue.add(request);

    }

    private void verifyDialog() {


        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.confirm_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        Button btnValidate = dialogView.findViewById(R.id.btn_dir_validate);
        etOtp = dialogView.findViewById(R.id.dir_otp_feild);
        ImageView ivClose = dialogView.findViewById(R.id.dir_validate_close);

        validateOtpDialog = dialogBuilder.create();

        String otp = Constants.pref.getString("otp", "");
        etOtp.setText(otp);

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateOtp();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateOtpDialog.dismiss();
            }
        });
        validateOtpDialog.show();

    }

    private void validateOtp() {

        progressDialog = new Dialog(getActivity());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, VALIDATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equalsIgnoreCase("success")){

                                String status = jsonObject.getString("flag");
                                Constants.editor.putString("flag", status);
                                Constants.editor.apply();
                                Constants.editor.commit();
                                btnVerify.setVisibility(View.GONE);
                                ivVerified.setVisibility(View.VISIBLE);
                                validateOtpDialog.dismiss();
                                KToast.successToast(getActivity(),
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else {

                                KToast.errorToast(getActivity(),
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            KToast.errorToast(getActivity(),e.getMessage(),
                                    Gravity.BOTTOM,
                                    KToast.LENGTH_SHORT);
                        }
                        progressDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                String phone = etPhone.getText().toString().trim();
                String otp = Constants.pref.getString("otp", "");
                params.put("mobileno", phone);
                params.put("otp", otp);
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

        StringRequest request = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equalsIgnoreCase("success")){

                                startActivity(new Intent(getActivity(), StartActivity.class));

                                KToast.successToast(getActivity(),
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else {

                                KToast.errorToast(getActivity(),
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            KToast.errorToast(getActivity(),e.getMessage(),
                                    Gravity.BOTTOM,
                                    KToast.LENGTH_SHORT);
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
                                KToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("name", fname + " " + lname);
                params.put("email", email);
                params.put("mobile", phone);
                params.put("password", cpass);
                params.put("language", languages);
                params.put("category", categories);
                return params;
            }
        };

        queue.add(request);

    }

    private boolean isVerified(String flag) {

        if (flag.equalsIgnoreCase("1")){
            return true;
        }
        return false;
    }

    private boolean isValidCPass(String cpass) {

        String pass = etPass.getText().toString().trim();
        if (cpass != null && cpass.matches(pass) && cpass.length() > 0) {
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

    private boolean isValidPhone(String phone) {

        if (phone != null && phone.length() >= 10){
            return true;
        }
        return false;
    }

    private boolean isValidEmail(String email) {

        String pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email != null && email.matches(pattern)){
            return true;
        }
        return false;
    }

    private boolean isValidLName(String lname) {


        if (lname != null && lname.length() > 0){
            return true;
        }
        return false;
    }

    private boolean isValidFName(String fname) {

        if (fname != null && fname.length() > 0){
            return true;
        }
        return false;
    }

}
