package com.app.mcc.guest;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.antonionicolaspina.revealtextview.RevealTextView;
import com.app.mcc.R;
import com.app.mcc.activity.StartActivity;
import com.app.mcc.director.DirectorCategoryAdapter;
import com.app.mcc.helper.Constants;
import com.onurkaganaldemir.ktoastlib.KToast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

import pl.droidsonroids.gif.GifImageView;
import spencerstudios.com.bungeelib.Bungee;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuestHomeFragment extends Fragment {

    RecyclerView recyclerView, rvCategory;
    ArrayList<HashMap<String,String>> homeList;
    GuestHomeAdapter homeAdapter;
    ArrayList<HashMap<String,String>> categoryList;
    GuestCategoryAdapter categoryAdapter;
    RecyclerView.LayoutManager layoutManager;
    RequestQueue queue;
    Dialog progressDialog;
    GifImageView ivRegister;
    String CATEGORY_URL = Constants.DIRECTOR_URL + Constants.GET_CATEGORY;
    String HOME_URL = Constants.GUEST_URL + Constants.HOME_DATA;


    public GuestHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_guest, container, false);

        Constants.pref = getActivity().getApplicationContext().getSharedPreferences("MCC",0);
        Constants.editor = Constants.pref.edit();

        queue = Volley.newRequestQueue(getActivity());

        ivRegister = view.findViewById(R.id.gif_iv);

        homeList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rv_home);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        categoryList = new ArrayList<>();
        rvCategory = view.findViewById(R.id.rv_category);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        rvCategory.setLayoutManager(layoutManager);

        jsonData();

        getCategory();

        ivRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), StartActivity.class));
                Bungee.fade(getActivity());
            }
        });


        return view;
    }

    private void getCategory() {

        StringRequest request = new StringRequest(Request.Method.GET, CATEGORY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status")
                                    .equalsIgnoreCase("success")){
                                String data = jsonObject.getString("message");
                                JSONArray array = new JSONArray(data);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);

                                    HashMap<String, String> map = new HashMap<>();

                                    String id = object.getString("id");
                                    String fname = object.getString("title");
                                    String lname = object.getString("image");

                                    map.put("id", id);
                                    map.put("title", fname);
                                    map.put("image", lname);

                                    categoryList.add(map);

                                }

                                categoryAdapter = new GuestCategoryAdapter(getActivity(), categoryList);
                                rvCategory.setAdapter(categoryAdapter);

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

    private void jsonData() {

        progressDialog = new Dialog(getActivity());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, HOME_URL,
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
                                    String title = object.getString("title");
                                    String image = object.getString("image");

                                    map.put("id", id);
                                    map.put("title", title);
                                    map.put("image", image);

                                    homeList.add(map);
                                }

                                homeAdapter = new GuestHomeAdapter(getActivity(), homeList);
                                recyclerView.setAdapter(homeAdapter);

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
                });
        queue.add(request);
    }

}
