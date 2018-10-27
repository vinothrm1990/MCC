package com.app.mcc.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.app.mcc.R;
import com.app.mcc.adapter.DirectorAdapter;
import com.app.mcc.adapter.MemberAdapter;
import com.app.mcc.helper.Constants;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;
import spencerstudios.com.bungeelib.Bungee;

public class StartActivity extends AppCompatActivity implements InternetConnectivityListener {

    TextView tvDirector, tvMember, tvSkip;
    TabLayout tabLayout;
    ViewPager directorViewPager, memberViewPager;
    DirectorAdapter directorAdapter;
    MemberAdapter memberAdapter;
    InternetAvailabilityChecker availabilityChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        Constants.pref = getApplicationContext().getSharedPreferences("MCC", MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        tvDirector = findViewById(R.id.director_tv);
        tvMember = findViewById(R.id.member_tv);
        tvSkip = findViewById(R.id.skip_tv);
        tabLayout = findViewById(R.id.tab_layout);
        directorViewPager = findViewById(R.id.director_view_pager);
        memberViewPager = findViewById(R.id.member_view_pager);

        directorAdapter = new DirectorAdapter(StartActivity.this, getSupportFragmentManager());
        directorViewPager.setAdapter(directorAdapter);
        tabLayout.setupWithViewPager(directorViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        memberAdapter = new MemberAdapter(StartActivity.this, getSupportFragmentManager());
        memberViewPager.setAdapter(memberAdapter);
        tabLayout.setupWithViewPager(memberViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.editor.putString("type", "guest");
                Constants.editor.apply();
                Constants.editor.commit();
                startActivity(new Intent(StartActivity.this, HomeActivity.class));
                Bungee.zoom(StartActivity.this);
            }
        });

        tvMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvMember.getText().toString().equalsIgnoreCase("MEMBER")){
                    directorViewPager.setVisibility(View.GONE);
                    memberViewPager.setVisibility(View.VISIBLE);
                    memberAdapter = new MemberAdapter(StartActivity.this, getSupportFragmentManager());
                    memberViewPager.setAdapter(memberAdapter);
                    tabLayout.setupWithViewPager(memberViewPager);
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                    tvMember.setText("DIRECTOR");
                    tvDirector.setText("MEMBER");
                } else if (tvMember.getText().toString().equalsIgnoreCase("DIRECTOR")) {
                    directorViewPager.setVisibility(View.VISIBLE);
                    memberViewPager.setVisibility(View.GONE);
                    directorAdapter = new DirectorAdapter(StartActivity.this, getSupportFragmentManager());
                    directorViewPager.setAdapter(directorAdapter);
                    tabLayout.setupWithViewPager(directorViewPager);
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                    tvMember.setText("MEMBER");
                    tvDirector.setText("DIRECTOR");
                }
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                directorViewPager.setCurrentItem(tab.getPosition());
                memberViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        availabilityChecker.removeInternetConnectivityChangeListener(this);
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (!isConnected) {
            KToast.warningToast(StartActivity.this, "Check your Internet Connection",
                    Gravity.BOTTOM,
                    KToast.LENGTH_LONG);
        }
    }
}
