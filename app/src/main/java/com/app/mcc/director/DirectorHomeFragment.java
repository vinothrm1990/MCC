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
import com.app.mcc.guest.GuestHomeAdapter;
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
public class DirectorHomeFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<HashMap<String,String>> homeList;
    DirectorHomeAdapter homeAdapter;
    RecyclerView.LayoutManager layoutManager;
    RequestQueue queue;
    Dialog progressDialog;
    String HOME_URL = Constants.DIRECTOR_URL + Constants.HOME_DATA;

    public DirectorHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_director, container, false);

        Constants.pref = getActivity().getApplicationContext().getSharedPreferences("MCC",0);
        Constants.editor = Constants.pref.edit();

        queue = Volley.newRequestQueue(getActivity());

        homeList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rv_home);
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
                                    String fname = object.getString("f_name");
                                    String lname = object.getString("l_name");
                                    String profile = object.getString("profile");
                                    String category = object.getString("category");

                                    map.put("id", id);
                                    map.put("f_name", fname);
                                    map.put("l_name", lname);
                                    map.put("profile", profile);
                                    map.put("category", category);

                                    homeList.add(map);

                                }

                                homeAdapter = new DirectorHomeAdapter(getActivity(), homeList);
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
