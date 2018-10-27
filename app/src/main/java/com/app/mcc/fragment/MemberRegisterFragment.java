package com.app.mcc.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.CompoundButton;
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
public class MemberRegisterFragment extends Fragment {

    ScrollView memberLayout;
    LinePinField etOtp;
    AppCompatEditText etFName, etLName, etEmail, etPhone, etPass, etCPass;
    CheckBox cbMale, cbFemale, cbAgree;
    Button btnRegister, btnVerify;
    ImageView ivVerified;
    Dialog progressDialog;
    RequestQueue queue;
    String URL = Constants.MEMBER_URL + Constants.REGISTER;
    String OTP_URL = Constants.MEMBER_URL + Constants.GET_OTP;
    String VALIDATE_URL = Constants.MEMBER_URL + Constants.VALIDATE_OTP;
    String fname, lname, email, phone, cpass, gender, categories, flag;
    Spinner spinCategory;
    android.app.AlertDialog getOtpDialog , validateOtpDialog;

    public MemberRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_member_register, container, false);

        Constants.pref = getActivity().getApplicationContext().getSharedPreferences("MCC", MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        Constants.editor.remove("flag");
        Constants.editor.apply();
        Constants.editor.commit();

        memberLayout = view.findViewById(R.id.member_layout);
        etFName = view.findViewById(R.id.mem_reg_fname);
        etLName = view.findViewById(R.id.mem_reg_lname);
        etEmail = view.findViewById(R.id.mem_reg_email);
        etPhone = view.findViewById(R.id.mem_reg_phone);
        etPass = view.findViewById(R.id.mem_reg_pass);
        etCPass = view.findViewById(R.id.mem_reg_cpass);
        cbMale = view.findViewById(R.id.mem_reg_male);
        cbFemale = view.findViewById(R.id.mem_reg_female);
        cbAgree = view.findViewById(R.id.mem_reg_agree);
        btnRegister = view.findViewById(R.id.mem_reg_register);
        spinCategory = view.findViewById(R.id.mem_spin_cat);
        btnVerify = view.findViewById(R.id.btn_mem_verify);
        ivVerified = view.findViewById(R.id.mem_reg_verify);

        queue = Volley.newRequestQueue(getActivity());

        List<String> category = new ArrayList<>();
            category.add("Acting");
            category.add("Direction");
            category.add("Cinematograph");
            category.add("Editing");
            category.add("Makeup");
            category.add("Costume");
            category.add("Art");
            category.add("Music");
            category.add("Sound");
            category.add("Singer");
            category.add("Lyricist");
            category.add("Dance");
            category.add("Stunt");
            category.add("Public Relation");
            category.add("DI VFX");
            category.add("Designer");
            category.add("Visual Effects");
            category.add("Animation");
            category.add("Stills");
            category.add("Dubbing & Mimicry");
            category.add("Short Film");
            category.add("Dubsmash");

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
        cbMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (cbMale.isChecked()){
                    gender = cbMale.getText().toString().trim();
                    cbFemale.setChecked(false);
                }

            }
        });

        cbFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (cbFemale.isChecked()){
                    gender = cbFemale.getText().toString().trim();
                    cbMale.setChecked(false);
                }
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
                if (!cbMale.isChecked() && !cbFemale.isChecked()){
                    KToast.warningToast(getActivity(),"Gender feilds are Missing",
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
                params.put("f_name", fname);
                params.put("l_name", lname);
                params.put("email", email);
                params.put("mobile", phone);
                params.put("gender", gender);
                params.put("password", cpass);
                params.put("type", "Member");
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

        if (fname != null && fname.length() > 0) {
            return true;
        }
        return false;
    }

}
