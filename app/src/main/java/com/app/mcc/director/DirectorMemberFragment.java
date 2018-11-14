package com.app.mcc.director;


import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.mcc.R;
import com.app.mcc.helper.Constants;
import com.onurkaganaldemir.ktoastlib.KToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class DirectorMemberFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<HashMap<String,String>> memberList;
    DirectorMemberAdapter memberAdapter;
    RecyclerView.LayoutManager layoutManager;
    RequestQueue queue;
    Dialog progressDialog;
    String MEMBER_URL = Constants.DIRECTOR_URL + Constants.GET_MEMBER;

    public DirectorMemberFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_director_member, container, false);
        Constants.pref = getActivity().getApplicationContext().getSharedPreferences("MCC",0);
        Constants.editor = Constants.pref.edit();

        queue = Volley.newRequestQueue(getActivity());

        memberList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rv_member);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        jsonData();

        return view;
    }

    private void jsonData() {

        progressDialog = new Dialog(getActivity());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, MEMBER_URL,
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
                                    String photo = object.getString("upload_pic");
                                    String audio = object.getString("upload_audio");
                                    String video = object.getString("video");

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
                                    map.put("upload_pic", photo);
                                    map.put("upload_audio", audio);
                                    map.put("video", video);

                                    memberList.add(map);

                                }

                                memberAdapter = new DirectorMemberAdapter(getActivity(), memberList);
                                recyclerView.setAdapter(memberAdapter);

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
                });
        queue.add(request);
    }

}
