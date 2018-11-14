package com.app.mcc.member;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.mcc.R;
import com.app.mcc.director.DirectorAboutActivity;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

public class MemberAboutActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_about);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("ABOUT US");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        availabilityChecker.removeInternetConnectivityChangeListener(this);
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (!isConnected) {
            KToast.warningToast(MemberAboutActivity.this, "Check your Internet Connection",
                    Gravity.BOTTOM,
                    KToast.LENGTH_LONG);
        }
    }
}
