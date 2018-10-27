package com.app.mcc.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatEditText;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.mcc.R;
import com.app.mcc.director.DirectorFeedbackActivity;
import com.app.mcc.director.DirectorHomeFragment;
import com.app.mcc.director.DirectorPostActivity;
import com.app.mcc.director.DirectorProfileActivity;
import com.app.mcc.director.DirectorViewActivity;
import com.app.mcc.director.DirectorWishlistActivity;
import com.app.mcc.guest.GuestAboutFragment;
import com.app.mcc.guest.GuestHomeFragment;
import com.app.mcc.helper.Constants;
import com.app.mcc.member.MemberHomeFragment;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.StreamHandler;

import spencerstudios.com.bungeelib.Bungee;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, InternetConnectivityListener {

    String type;
    AlertDialog alertDialog, changeDialog;
    Dialog progressDialog;
    String oldPass, newPass, confirmPass;
    RequestQueue queue;
    String CHANGE_URL = Constants.DIRECTOR_URL + Constants.CHANGE_PASSWORD;
    InternetAvailabilityChecker availabilityChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.pref = getApplicationContext().getSharedPreferences("MCC", MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();
        type = Constants.pref.getString("type", "");
        if (type.equalsIgnoreCase("guest")) {
            setContentView(R.layout.activity_home);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, new GuestHomeFragment());
            fragmentTransaction.commit();
            Bungee.zoom(HomeActivity.this);

        }else if (type.equalsIgnoreCase("director")){
            setContentView(R.layout.activity_director);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, new DirectorHomeFragment());
            fragmentTransaction.commit();
            Bungee.zoom(HomeActivity.this);

        }else if (type.equalsIgnoreCase("member")){
            setContentView(R.layout.activity_member);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, new MemberHomeFragment());
            fragmentTransaction.commit();
            Bungee.zoom(HomeActivity.this);

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("MY CINEMA CHANCE");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(title);

        queue = Volley.newRequestQueue(HomeActivity.this);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem item1 = menu.findItem(R.id.action_settings);
        item1.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        if (type.equalsIgnoreCase("guest")){
            int id = item.getItemId();
            if (id == R.id.nav_home) {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, new GuestHomeFragment());
                fragmentTransaction.commit();
                Bungee.zoom(HomeActivity.this);

            } else if (id == R.id.nav_about) {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, new GuestAboutFragment());
                fragmentTransaction.commit();
                Bungee.zoom(HomeActivity.this);

            } else if (id == R.id.nav_exit) {
                logoutDialog();
            }

        } else if (type.equalsIgnoreCase("director")) {
            int id = item.getItemId();
            if (id == R.id.nav_home) {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, new DirectorHomeFragment());
                fragmentTransaction.commit();

            } else if (id == R.id.nav_profile) {

                startActivity(new Intent(HomeActivity.this, DirectorProfileActivity.class));
                Bungee.zoom(HomeActivity.this);

            }else if (id == R.id.nav_post) {

                startActivity(new Intent(HomeActivity.this, DirectorPostActivity.class));
                Bungee.zoom(HomeActivity.this);

            }else if (id == R.id.nav_wishlist) {

                startActivity(new Intent(HomeActivity.this, DirectorWishlistActivity.class));
                Bungee.zoom(HomeActivity.this);

            }else if (id == R.id.nav_change) {

                changePassword();

            }else if (id == R.id.nav_member) {

                startActivity(new Intent(HomeActivity.this, DirectorViewActivity.class));
                Bungee.zoom(HomeActivity.this);

            }else if (id == R.id.nav_feedback) {

                startActivity(new Intent(HomeActivity.this, DirectorFeedbackActivity.class));
                Bungee.zoom(HomeActivity.this);

            }else if (id == R.id.nav_about) {

            } else if (id == R.id.nav_exit) {
                logoutDialog();
            }

        } else if (type.equalsIgnoreCase("member")) {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Handle the camera action
            } else if (id == R.id.nav_profile) {

            }else if (id == R.id.nav_upload) {

            }else if (id == R.id.nav_change) {

            }else if (id == R.id.nav_feedback) {

            }else if (id == R.id.nav_about) {

            } else if (id == R.id.nav_exit) {
                logoutDialog();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changePassword() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.change_password_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        Button btnUpdate = dialogView.findViewById(R.id.btn_update_change);
        ImageView ivClose = dialogView.findViewById(R.id.dir_change_close);
        final AppCompatEditText etOld = dialogView.findViewById(R.id.dir_change_old);
        final AppCompatEditText etNew = dialogView.findViewById(R.id.dir_change_new);
        final AppCompatEditText etConfirm = dialogView.findViewById(R.id.dir_change_confirm);

        changeDialog = dialogBuilder.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean validation = false;

                oldPass = etOld.getText().toString().trim();
                if (!isValidOPass(oldPass)){
                    etOld.setError("Invalid Password or Password didn't Match");
                    validation = true;
                }
                newPass = etNew.getText().toString().trim();
                if (!isValidNPass(newPass)){
                    etNew.setError("Invalid Password");
                    validation = true;
                }
                confirmPass = etConfirm.getText().toString().trim();
                if (!isValidCPass(confirmPass, newPass)){
                    etConfirm.setError("Password didn't Match");
                    validation = true;
                }
                if (validation == false){
                    //updatePassword();
                }
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeDialog.dismiss();
            }
        });
        changeDialog.show();
    }

    private boolean isValidCPass(String confirmPass, String newPass) {
        if (confirmPass != null && confirmPass.matches(newPass) && confirmPass.length() > 0) {
            return true;
        }
        return false;
    }

    private boolean isValidNPass(String newPass) {
        if (newPass != null && newPass.length() > 3) {
            return true;
        }
        return false;
    }

    private boolean isValidOPass(String oldPass) {
        String old = Constants.pref.getString("password", "");
        if (oldPass != null && oldPass.matches(old)) {
            return true;
        }
        return false;
    }

    private void updatePassword() {

        progressDialog = new Dialog(HomeActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, CHANGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equalsIgnoreCase("success")){

                                changeDialog.dismiss();
                                KToast.successToast(HomeActivity.this,
                                        "Your Password Changed Successfully",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else {
                                KToast.errorToast(HomeActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            KToast.errorToast(HomeActivity.this,
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

                        KToast.errorToast(HomeActivity.this,
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
                params.put("old", oldPass);
                params.put("new", newPass);
                params.put("confirm", confirmPass);
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
            KToast.warningToast(HomeActivity.this,
                    "Check your Internet Connection",
                    Gravity.BOTTOM,
                    KToast.LENGTH_LONG);
        }
    }

    private void logoutDialog() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.logout_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        Button btnyes = dialogView.findViewById(R.id.btn_yes_logout);
        Button btnno = dialogView.findViewById(R.id.btn_no_logout);

        alertDialog = dialogBuilder.create();

        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Constants.editor.clear();
                Constants.editor.commit();
                finish();
                Intent p = new Intent(HomeActivity.this, StartActivity.class);
                startActivity(p);
                Bungee.inAndOut(HomeActivity.this);
            }
        });
        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
