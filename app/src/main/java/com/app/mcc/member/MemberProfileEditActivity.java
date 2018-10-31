package com.app.mcc.member;

import android.Manifest;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
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
import com.app.mcc.activity.HomeActivity;
import com.app.mcc.director.DirectorProfileActivity;
import com.app.mcc.helper.Constants;
import com.app.mcc.helper.FilePath;
import com.bumptech.glide.Glide;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MemberProfileEditActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    AppCompatEditText etFName, etLName, etAge, etQualify, etMid, etFid, etPhone, etEmail,
            etAddress, etCity, etPincode, etAbout, etLanguage, etAchive, etIndustry, etCv, etFav,
            etDate, etProfile;
    TextView etDob;
    ImageButton ibBio, ibImage;
    ImageView ivVideo, ivAudio, ivPhoto, ivBioVerified, ivProfileVerfied, ivAudioVerified, ivPhotoVerified, ivVideoVerified;
    Button btnUpdate;
    public static final int PROFILE_REQUEST = 1;
    public static final int BIO_REQUEST = 2;
    public static final int AUDIO_REQUEST = 3;
    public static final int VIDEO_REQUEST = 4;
    public static final int PHOTO_REQUEST = 5;
    public static final int PERMISSION_CODE = 6;
    Bitmap bitmap;
    String path, fname, lname, mobile, email, dob, age, qualify, mid, fid, address, city, pincode, about, lang, achive, indus, cv, fav, date, profile;
    RequestQueue queue;
    Uri filePath;
    Calendar calendar;
    CheckBox cbMale, cbFemale;
    Dialog progressDialog;
    String profilefilename, biofilename, audiofilename, videofilename, photofilename, gender;
    String PROFILE_URL = Constants.MEMBER_URL + Constants.PROFILE_UPLOAD;
    String BIO_URL = Constants.MEMBER_URL + Constants.BIO_UPLOAD;
    String PHOTO_URL = Constants.MEMBER_URL + Constants.PHOTO_UPLOAD;
    String VIDEO_URL = Constants.MEMBER_URL + Constants.VIDEO_UPLOAD;
    String AUDIO_URL = Constants.MEMBER_URL + Constants.AUDIO_UPLOAD;
    String UPDATE_URL = Constants.MEMBER_URL + Constants.UPDATE_PROFILE;
    String GET_URL = Constants.MEMBER_URL + Constants.GET_PROFILE;
    String myFormat = "yyyy-MM-dd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profile_edit);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        Constants.pref = getApplicationContext().getSharedPreferences("MCC",0);
        Constants.editor = Constants.pref.edit();

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("EDIT PROFILE");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        requestPermission();
        queue = Volley.newRequestQueue(this);

        etFName = findViewById(R.id.mem_profile_fname);
        etLName = findViewById(R.id.mem_profile_lname);
        etDob = findViewById(R.id.mem_profile_dob);
        etAge = findViewById(R.id.mem_profile_age);
        etQualify = findViewById(R.id.mem_profile_qualify);
        etMid = findViewById(R.id.mem_profile_mid);
        etFid = findViewById(R.id.mem_profile_fid);
        etPhone = findViewById(R.id.mem_profile_mobile);
        etEmail = findViewById(R.id.mem_profile_email);
        etAddress = findViewById(R.id.mem_profile_address);
        etCity = findViewById(R.id.mem_profile_city);
        etPincode = findViewById(R.id.mem_profile_pincode);
        etAbout = findViewById(R.id.mem_profile_about);
        etLanguage = findViewById(R.id.mem_profile_lang);
        etAchive = findViewById(R.id.mem_profile_achive);
        etIndustry = findViewById(R.id.mem_profile_industry);
        etCv = findViewById(R.id.mem_profile_cv);
        etFav = findViewById(R.id.mem_profile_fav);
        etDate = findViewById(R.id.mem_profile_rdate);
        etProfile = findViewById(R.id.mem_profile_image);
        ivAudio = findViewById(R.id.mem_profile_audio);
        ivVideo = findViewById(R.id.mem_profile_video);
        ivPhoto= findViewById(R.id.mem_profile_photo);
        ibBio = findViewById(R.id.mem_profile_upload_cv);
        ibImage = findViewById(R.id.mem_profile_upload_profile);
        btnUpdate = findViewById(R.id.mem_profile_update);
        ivBioVerified = findViewById(R.id.mem_profile_cv_verified);
        ivProfileVerfied = findViewById(R.id.mem_profile_image_verified);
        ivPhotoVerified = findViewById(R.id.mem_profile_photo_verified);
        ivAudioVerified = findViewById(R.id.mem_profile_audio_verified);
        ivVideoVerified = findViewById(R.id.mem_profile_video_verified);
        cbMale = findViewById(R.id.mem_profile_male);
        cbFemale = findViewById(R.id.mem_profile_female);

        getProfile();

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

        ibBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Document"), BIO_REQUEST);
            }
        });

        ibImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PROFILE_REQUEST);
            }
        });

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Constants.editor.remove("photo");
                    Constants.editor.apply();
                    Constants.editor.commit();

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), PHOTO_REQUEST);


            }
        });

        ivAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Constants.editor.remove("audio");
                Constants.editor.apply();
                Constants.editor.commit();

                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Audio"), AUDIO_REQUEST);
            }
        });

        ivVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Constants.editor.remove("video");
                Constants.editor.apply();
                Constants.editor.commit();

                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO_REQUEST);
            }
        });

        calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener pdate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setDateFormat();
            }
        };

        etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(MemberProfileEditActivity.this, pdate, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean validation = false;

                fname = etFName.getText().toString().trim();
                if (!isValidFName(fname)){
                    etFName.setError("First Name Field is Empty");
                    validation =  true;
                }
                lname = etLName.getText().toString().trim();
                if (!isValidLName(lname)){
                    etLName.setError("Last Name Field is Empty");
                    validation =  true;
                }
                email = etEmail.getText().toString().trim();
                if (!isValidEmail(email)){
                    etEmail.setError("Invalid Email Address");
                    validation =  true;
                }
                dob = etDob.getText().toString().trim();
                if (!isValidDob(dob)){
                    etDob.setError("DOB Feild is Empty");
                    validation = true;
                }
                age = etAge.getText().toString().trim();
                if (!isValidAge(age)){
                    etAge.setError("Age Feild is Empty");
                    validation = true;
                }
                qualify = etQualify.getText().toString().trim();
                if (!isValidQualify(qualify)){
                    etQualify.setError("Qualification Feild is Empty");
                    validation = true;
                }
                mid = etMid.getText().toString().trim();
                if (!isValidMid(mid)){
                    etMid.setError("Member ID Feild is Empty");
                    validation = true;
                }
                fid = etFid.getText().toString().trim();
                if (!isValidFid(fid)){
                    etFid.setError("Member ID Feild is Empty");
                    validation = true;
                }
                address = etAddress.getText().toString().trim();
                if (!isValidAddress(address)){
                    etAddress.setError("Address Feild is Empty");
                    validation = true;
                }
                city = etCity.getText().toString().trim();
                if (!isValidCity(city)){
                    etCity.setError("City Feild is Empty");
                    validation = true;
                }
                pincode = etPincode.getText().toString().trim();
                if (!isValidPincode(pincode)){
                    etPincode.setError("Pincode Feild is Empty");
                    validation = true;
                }
                about = etAbout.getText().toString().trim();
                if (!isValidAbout(about)){
                    etAbout.setError("About You Feild is Empty");
                    validation = true;
                }
                lang = etLanguage.getText().toString().trim();
                if (!isValidLanguage(lang)){
                    etLanguage.setError("Language Known Feild is Empty");
                    validation = true;
                }
                achive = etAchive.getText().toString().trim();
                if (!isValidAchive(achive)){
                    etAchive.setError("Previous Achivement Feild is Empty");
                    validation = true;
                }
                indus = etIndustry.getText().toString().trim();
                if (!isValidIndustry(indus)){
                    etIndustry.setError("Industry Register Feild is Empty");
                    validation = true;
                }
                cv = etCv.getText().toString().trim();
                if (!isValidBio(cv)){
                    etCv.setError("Bio Data Feild is Empty");
                    validation = true;
                }
                fav = etFav.getText().toString().trim();
                if (!isValidFavourite(fav)){
                    etFav.setError("Favourite Actor/Actress Feild is Empty");
                    validation = true;
                }
                date = etDate.getText().toString().trim();
                if (!isValidDate(date)){
                    etDate.setError("Register Date Feild is Empty");
                    validation = true;
                }
                profile = etProfile.getText().toString().trim();
                if (!isValidProfile(profile)){
                    etProfile.setError("Profile Image Feild is Empty");
                    validation = true;
                }
                mobile = etPhone.getText().toString().trim();
                if (!isValidPhone(mobile)){
                    etPhone.setError("Invalid  Number");
                    validation = true;
                }
                if (validation == false){

                    updateProfile();
                }

            }
        });
    }

    private void setDateFormat() {

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDob.setText(sdf.format(calendar.getTime()));
    }

    private void getProfile() {

        progressDialog = new Dialog(MemberProfileEditActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, GET_URL,
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
                                JSONObject object = array.getJSONObject(0);

                                String fname = object.getString("f_name");
                                String lname = object.getString("l_name");
                                String fid = object.getString("fb_id");
                                String email = object.getString("email");
                                String mobile = object.getString("contact");
                                String dob = object.getString("dob");
                                String age = object.getString("age");
                                String gender = object.getString("gender");
                                String address = object.getString("address");
                                String city = object.getString("city");
                                String pincode = object.getString("pincode");
                                String qualify = object.getString("qualify");
                                String language = object.getString("language");
                                String fav = object.getString("fav_hero");
                                String about = object.getString("about_us");
                                String achive = object.getString("prev_acheive");
                                String indus = object.getString("ind_register");
                                String mid = object.getString("member_id");
                                String date = object.getString("member_ship_date");
                                String bio = object.getString("upload_biodata");
                                String profile = object.getString("profile");
                                String photo = object.getString("upload_pic");
                                String audio = object.getString("upload_audio");
                                String video = object.getString("video");

                                etFName.setText(fname);
                                etLName.setText(lname);
                                etFid.setText(fid);
                                etEmail.setText(email);
                                etPhone.setText(mobile);
                                etDob.setText(dob);
                                etAge.setText(age);
                                etAddress.setText(address);
                                etCity.setText(city);
                                etPincode.setText(pincode);
                                etQualify.setText(qualify);
                                etFav.setText(fav);
                                etAbout.setText(about);
                                etAchive.setText(achive);
                                etIndustry.setText(indus);
                                etMid.setText(mid);
                                etDate.setText(date);
                                etCv.setText(bio);
                                etLanguage.setText(language);
                                etProfile.setText(profile);
                                etCv.setText(bio);
                                etDob.setText(date);

                                if (gender.equalsIgnoreCase("Male")){
                                    cbMale.setChecked(true);
                                    cbFemale.setChecked(false);
                                }else if (gender.equalsIgnoreCase("Female")){
                                    cbMale.setChecked(false);
                                    cbFemale.setChecked(true);
                                }
                                if (!photo.isEmpty()){
                                    Constants.editor.putString("photo", photo);
                                    Constants.editor.apply();
                                    Constants.editor.commit();
                                }
                                if (!audio.isEmpty()){
                                    Constants.editor.putString("audio", audio);
                                    Constants.editor.apply();
                                    Constants.editor.commit();
                                }
                                if (!video.isEmpty()){
                                    Constants.editor.putString("video", video);
                                    Constants.editor.apply();
                                    Constants.editor.commit();
                                }

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("failed")){
                                KToast.warningToast(MemberProfileEditActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("empty")){
                                KToast.warningToast(MemberProfileEditActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }else {
                                KToast.errorToast(MemberProfileEditActivity.this,
                                        "Something Went Wrong!",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            KToast.errorToast(MemberProfileEditActivity.this,
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

                        KToast.errorToast(MemberProfileEditActivity.this,
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
                params.put("mobileno", Constants.pref.getString("phone", ""));
                return params;
            }
        };
        queue.add(request);
    }

    private void requestPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                KToast.successToast(MemberProfileEditActivity.this,
                        "Permission Granted",
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            } else {
                //Displaying another toast if permission is not granted
                KToast.warningToast(MemberProfileEditActivity.this,
                        "Permission Denied",
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PROFILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            path = FilePath.getPath(this, filePath);
            if (path != null && !path.equals("")){
                profilefilename = path.substring(path.lastIndexOf("/")+1);
                etProfile.setText(profilefilename);
            }
            try{
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                uploadProfile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if (requestCode == BIO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            filePath = data.getData();
            path = FilePath.getPath(this, filePath);
            if (path != null && !path.equals("")){
                biofilename = path.substring(path.lastIndexOf("/")+1);
                etCv.setText(biofilename);
            }
            try{
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                uploadBio();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if (requestCode == AUDIO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            filePath = data.getData();
            path = FilePath.getPath(this, filePath);
            if (path != null && !path.equals("")){
                audiofilename = path.substring(path.lastIndexOf("/")+1);
            }
            try{
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                uploadAudio();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            filePath = data.getData();
            path = FilePath.getPath(this, filePath);
            if (path != null && !path.equals("")){
                photofilename = path.substring(path.lastIndexOf("/")+1);
            }
            try{
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                uploadPhoto();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if (requestCode == VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            filePath = data.getData();
            path = FilePath.getPath(this, filePath);
            if (path != null && !path.equals("")){
                videofilename = path.substring(path.lastIndexOf("/")+1);
            }
            try{
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                uploadVideo();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadVideo() {

        progressDialog = new Dialog(MemberProfileEditActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //creating new thread to handle Http Operations
                uploadVideo(path);
            }
        }).start();
    }

    private int uploadVideo(final String path) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(path);

        String[] parts = path.split("/");
        final String fileName = parts[parts.length-1];

        if (!selectedFile.isFile()){
            progressDialog.hide();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    KToast.warningToast(MemberProfileEditActivity.this,
                            "Source File Doesn't Exist :" + path,
                            Gravity.BOTTOM,
                            KToast.LENGTH_SHORT);
                }
            });
            return 0;
        }else{
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(VIDEO_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",path);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + path + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer,0,bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0){
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i("TAG", "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ivVideo.setVisibility(View.GONE);
                            ivVideoVerified.setVisibility(View.VISIBLE);
                            KToast.successToast(MemberProfileEditActivity.this,
                                    "Video Uploaded : " + fileName,
                                    Gravity.BOTTOM,
                                    KToast.LENGTH_SHORT);
                        }
                    });
                }
                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        KToast.errorToast(MemberProfileEditActivity.this,
                                "File not Found" + fileName,
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                KToast.errorToast(MemberProfileEditActivity.this,
                        "URL Error" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);

            } catch (IOException e) {
                e.printStackTrace();
                KToast.errorToast(MemberProfileEditActivity.this,
                        "Cannot Read/Write File" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            }
            progressDialog.dismiss();
            return serverResponseCode;
        }
    }

    private void uploadPhoto() {

        progressDialog = new Dialog(MemberProfileEditActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //creating new thread to handle Http Operations
                uploadPhoto(path);
            }
        }).start();
    }

    private int uploadPhoto(final String path) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(path);

        String[] parts = path.split("/");
        final String fileName = parts[parts.length-1];

        if (!selectedFile.isFile()){
            progressDialog.hide();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    KToast.warningToast(MemberProfileEditActivity.this,
                            "Source File Doesn't Exist :" + path,
                            Gravity.BOTTOM,
                            KToast.LENGTH_SHORT);
                }
            });
            return 0;
        }else{
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(PHOTO_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",path);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + path + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer,0,bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0){
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i("TAG", "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ivPhoto.setVisibility(View.GONE);
                            ivPhotoVerified.setVisibility(View.VISIBLE);
                            KToast.successToast(MemberProfileEditActivity.this,
                                    "Photo Uploaded : " + fileName,
                                    Gravity.BOTTOM,
                                    KToast.LENGTH_SHORT);
                        }
                    });
                }
                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        KToast.errorToast(MemberProfileEditActivity.this,
                                "File not Found" + fileName,
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                KToast.errorToast(MemberProfileEditActivity.this,
                        "URL Error" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);

            } catch (IOException e) {
                e.printStackTrace();
                KToast.errorToast(MemberProfileEditActivity.this,
                        "Cannot Read/Write File" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            }
            progressDialog.dismiss();
            return serverResponseCode;
        }
    }

    private void uploadAudio() {

        progressDialog = new Dialog(MemberProfileEditActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //creating new thread to handle Http Operations
                uploadAudio(path);
            }
        }).start();
    }

    private int uploadAudio(final String path) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(path);

        String[] parts = path.split("/");
        final String fileName = parts[parts.length-1];

        if (!selectedFile.isFile()){
            progressDialog.hide();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    KToast.warningToast(MemberProfileEditActivity.this,
                            "Source File Doesn't Exist :" + path,
                            Gravity.BOTTOM,
                            KToast.LENGTH_SHORT);
                }
            });
            return 0;
        }else{
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(AUDIO_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",path);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + path + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer,0,bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0){
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i("TAG", "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ivAudio.setVisibility(View.GONE);
                            ivAudioVerified.setVisibility(View.VISIBLE);
                            KToast.successToast(MemberProfileEditActivity.this,
                                    "Audio Uploaded : " + fileName,
                                    Gravity.BOTTOM,
                                    KToast.LENGTH_SHORT);
                        }
                    });
                }
                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        KToast.errorToast(MemberProfileEditActivity.this,
                                "File not Found" + fileName,
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                KToast.errorToast(MemberProfileEditActivity.this,
                        "URL Error" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);

            } catch (IOException e) {
                e.printStackTrace();
                KToast.errorToast(MemberProfileEditActivity.this,
                        "Cannot Read/Write File" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            }
            progressDialog.dismiss();
            return serverResponseCode;
        }
    }

    private void uploadBio() {

        progressDialog = new Dialog(MemberProfileEditActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //creating new thread to handle Http Operations
                uploadBio(path);
            }
        }).start();
    }

    private int uploadBio(final String path) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(path);

        String[] parts = path.split("/");
        final String fileName = parts[parts.length-1];

        if (!selectedFile.isFile()){
            progressDialog.hide();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    KToast.warningToast(MemberProfileEditActivity.this,
                            "Source File Doesn't Exist :" + path,
                            Gravity.BOTTOM,
                            KToast.LENGTH_SHORT);
                }
            });
            return 0;
        }else{
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(BIO_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",path);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + path + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer,0,bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0){
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i("TAG", "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ibBio.setVisibility(View.GONE);
                            ivBioVerified.setVisibility(View.VISIBLE);
                            KToast.successToast(MemberProfileEditActivity.this,
                                    "Document Uploaded : " + fileName,
                                    Gravity.BOTTOM,
                                    KToast.LENGTH_SHORT);
                        }
                    });
                }
                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        KToast.errorToast(MemberProfileEditActivity.this,
                                "File not Found" + fileName,
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                KToast.errorToast(MemberProfileEditActivity.this,
                        "URL Error" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);

            } catch (IOException e) {
                e.printStackTrace();
                KToast.errorToast(MemberProfileEditActivity.this,
                        "Cannot Read/Write File" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            }
            progressDialog.dismiss();
            return serverResponseCode;
        }

    }

    private void uploadProfile() {

        progressDialog = new Dialog(MemberProfileEditActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //creating new thread to handle Http Operations
                uploadProfile(path);
            }
        }).start();
    }

    private int uploadProfile(final String path) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(path);

        String[] parts = path.split("/");
        final String fileName = parts[parts.length-1];

        if (!selectedFile.isFile()){
            progressDialog.hide();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    KToast.warningToast(MemberProfileEditActivity.this,
                            "Source File Doesn't Exist :" + path,
                            Gravity.BOTTOM,
                            KToast.LENGTH_SHORT);
                }
            });
            return 0;
        }else{
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(PROFILE_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",path);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + path + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer,0,bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0){
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i("TAG", "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ibImage.setVisibility(View.GONE);
                            ivProfileVerfied.setVisibility(View.VISIBLE);
                            KToast.successToast(MemberProfileEditActivity.this,
                                    "Profile Image Uploaded : " + fileName,
                                    Gravity.BOTTOM,
                                    KToast.LENGTH_SHORT);
                        }
                    });
                }
                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        KToast.errorToast(MemberProfileEditActivity.this,
                                "File not Found" + fileName,
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                KToast.errorToast(MemberProfileEditActivity.this,
                        "URL Error" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);

            } catch (IOException e) {
                e.printStackTrace();
                KToast.errorToast(MemberProfileEditActivity.this,
                        "Cannot Read/Write File" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            }
            progressDialog.dismiss();
            return serverResponseCode;
        }
    }

    private boolean isValidProfile(String profile) {

        if (profile != null && profile.length() > 0) {
            return true;
        }
        return false;
    }
    private boolean isValidDate(String date) {

        if (date != null && date.length() > 0) {
            return true;
        }
        return false;
    }
    private boolean isValidFavourite(String fav) {

        if (fav != null && fav.length() > 0) {
            return true;
        }
        return false;
    }
    private boolean isValidBio(String cv) {

        if (cv != null && cv.length() > 0) {
            return true;
        }
        return false;
    }
    private boolean isValidIndustry(String indus) {

        if (indus != null && indus.length() > 0) {
            return true;
        }
        return false;
    }
    private boolean isValidAchive(String achive) {

        if (achive != null && achive.length() > 0) {
            return true;
        }
        return false;
    }
    private boolean isValidLanguage(String lang) {

        if (lang != null && lang.length() > 0) {
            return true;
        }
        return false;
    }

    private boolean isValidAbout(String about) {

        if (about != null && about.length() > 0) {
            return true;
        }
        return false;
    }

    private boolean isValidPincode(String pincode) {

        if (pincode != null && pincode.length() > 0) {
            return true;
        }
        return false;
    }

    private boolean isValidCity(String city) {

        if (city != null && city.length() > 0) {
            return true;
        }
        return false;
    }

    private boolean isValidAddress(String address) {

        if (address != null && address.length() > 0) {
            return true;
        }
        return false;
    }

    private boolean isValidFid(String fid) {

        if (fid != null && fid.length() > 0) {
            return true;
        }
        return false;
    }

    private boolean isValidMid(String mid) {

        if (mid != null && mid.length() > 0) {
            return true;
        }
        return false;
    }

    private boolean isValidQualify(String qualify) {

        if (qualify != null && qualify.length() > 0) {
            return true;
        }
        return false;
    }

    private boolean isValidAge(String age) {

        if (age != null && age.length() > 0) {
            return true;
        }
        return false;
    }

    private boolean isValidDob(String dob) {

        if (dob != null && dob.length() > 0) {
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

    private void updateProfile() {

        progressDialog = new Dialog(MemberProfileEditActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, UPDATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status")
                                    .equalsIgnoreCase("success")){
                                progressDialog.hide();
                                startActivity(new Intent(MemberProfileEditActivity.this, HomeActivity.class));

                                KToast.successToast(MemberProfileEditActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else  if (jsonObject.getString("status")
                                    .equalsIgnoreCase("failed")){
                                progressDialog.hide();
                                KToast.errorToast(MemberProfileEditActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }else  if (jsonObject.getString("status")
                                    .equalsIgnoreCase("empty")){
                                progressDialog.hide();
                                KToast.errorToast(MemberProfileEditActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }
                            else {
                                progressDialog.hide();
                                KToast.errorToast(MemberProfileEditActivity.this,
                                        "Something Went Wrong!",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            progressDialog.hide();
                            KToast.errorToast(MemberProfileEditActivity.this,
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
                        KToast.errorToast(MemberProfileEditActivity.this,
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
                params.put("fname", fname);
                params.put("lname", lname);
                params.put("fid", fid);
                params.put("email", email);
                params.put("dob", dob);
                params.put("age", age);
                params.put("gender", gender);
                params.put("address", address);
                params.put("city", city);
                params.put("pincode", pincode);
                params.put("qualify", qualify);
                params.put("profile", profile);
                params.put("language", lang);
                params.put("fav", fav);
                params.put("about", about);
                params.put("achive", achive);
                params.put("indus", indus);
                params.put("mid", mid);
                params.put("date", date);
                params.put("bio", cv);
                if (photofilename != null){
                    params.put("photo", photofilename);
                }else {
                    params.put("photo", "");
                }
                if (videofilename != null){
                    params.put("video", videofilename);
                }else {
                    params.put("video", "");
                }
                if (audiofilename != null){
                    params.put("audio", audiofilename);
                }else {
                    params.put("audio", "");
                }
                params.put("mobileno", mobile);
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
            KToast.warningToast(MemberProfileEditActivity.this,
                    "Check your Internet Connection",
                    Gravity.BOTTOM,
                    KToast.LENGTH_SHORT);
        }
    }
}
