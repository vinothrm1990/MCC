package com.app.mcc.director;

import android.Manifest;
import android.app.ActionBar;
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
import com.app.mcc.activity.StartActivity;
import com.app.mcc.helper.Constants;
import com.app.mcc.helper.FilePath;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.onurkaganaldemir.ktoastlib.KToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

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
import java.util.HashMap;
import java.util.Map;

public class DirectorProfileActivity extends AppCompatActivity implements InternetConnectivityListener {

    ImageView ivEdit;
    CircularImageView ivProfile;
    AppCompatEditText etName, etEmail, etPhone, etLanguage, etCategory;
    Button btnUpdate;
    InternetAvailabilityChecker availabilityChecker;
    public static final int PICK_REQUEST = 1;
    public static final int PERMISSION_CODE = 2;
    String path, name, email, fname;
    Bitmap bitmap;
    RequestQueue queue;
    Uri filePath;
    Dialog progressDialog;
    String UPLOAD_URL = Constants.DIRECTOR_URL + Constants.PROFILE_UPLOAD;
    String UPDATE_URL = Constants.DIRECTOR_URL + Constants.UPDATE_PROFILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_profile);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        Constants.pref = getApplicationContext().getSharedPreferences("MCC",0);
        Constants.editor = Constants.pref.edit();

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("MY PROFILE");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        ivEdit = findViewById(R.id.dir_profile_edit);
        ivProfile = findViewById(R.id.dir_profile_iv);
        etName = findViewById(R.id.dir_profile_name);
        etEmail = findViewById(R.id.dir_profile_email);
        etPhone = findViewById(R.id.dir_profile_phone);
        etLanguage = findViewById(R.id.dir_profile_lang);
        etCategory = findViewById(R.id.dir_profile_cat);
        btnUpdate = findViewById(R.id.dir_profile_update);

        requestPermission();
        queue = Volley.newRequestQueue(this);

        etPhone.setEnabled(false);
        etLanguage.setEnabled(false);
        etCategory.setEnabled(false);

        etName.setText(Constants.pref.getString("name", ""));
        etEmail.setText(Constants.pref.getString("email", ""));
        etPhone.setText(Constants.pref.getString("mobileno", ""));
        etLanguage.setText(Constants.pref.getString("language", ""));
        etCategory.setText(Constants.pref.getString("category", ""));

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_REQUEST);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean validation = false;

                name = etName.getText().toString().trim();
                if (!isValidName(name)){
                    etPhone.setError("Invalid Number");
                    validation =  true;
                }
                email = etEmail.getText().toString().trim();
                if (!isValidEmail(email)){
                    etEmail.setError("Invalid Email");
                    validation = true;
                }
                if (validation == false){

                    updateProfile();
                }

            }
        });

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
                KToast.successToast(DirectorProfileActivity.this,
                        "Permission Granted",
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            } else {
                //Displaying another toast if permission is not granted
                KToast.warningToast(DirectorProfileActivity.this,
                        "Permission Denied",
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            path = FilePath.getPath(this, filePath);
            if (path != null && !path.equals("")){
                fname = path.substring(path.lastIndexOf("/")+1);
            }
            try{
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                ivProfile.setImageBitmap(bitmap);
                uploadProfile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProfile() {

        progressDialog = new Dialog(DirectorProfileActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //creating new thread to handle Http Operations
                uploadImage(path);
            }
        }).start();
    }

    private int uploadImage(final String path) {

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
                    KToast.warningToast(DirectorProfileActivity.this,
                            "Source File Doesn't Exist :" + path,
                            Gravity.BOTTOM,
                            KToast.LENGTH_SHORT);
                }
            });
            return 0;
        }else{
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(UPLOAD_URL);
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

                            KToast.successToast(DirectorProfileActivity.this,
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
                        KToast.errorToast(DirectorProfileActivity.this,
                                "File not Found" + fileName,
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                KToast.errorToast(DirectorProfileActivity.this,
                        "URL Error" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);

            } catch (IOException e) {
                e.printStackTrace();
                KToast.errorToast(DirectorProfileActivity.this,
                        "Cannot Read/Write File" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            }
            progressDialog.dismiss();
            return serverResponseCode;
        }
    }

    private void updateProfile() {

        progressDialog = new Dialog(DirectorProfileActivity.this);
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

                                startActivity(new Intent(DirectorProfileActivity.this, HomeActivity.class));

                                KToast.successToast(DirectorProfileActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else  if (jsonObject.getString("status")
                                    .equalsIgnoreCase("failed")){

                                KToast.errorToast(DirectorProfileActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }else  if (jsonObject.getString("status")
                                    .equalsIgnoreCase("empty")){

                                KToast.errorToast(DirectorProfileActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }
                            else {
                                KToast.errorToast(DirectorProfileActivity.this,
                                        "Something Went Wrong!",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            KToast.errorToast(DirectorProfileActivity.this,
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

                        KToast.errorToast(DirectorProfileActivity.this,
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
                params.put("mobileno", Constants.pref.getString("mobileno", ""));
                params.put("name", name);
                params.put("email", email);
                if (path != null && !path.equals("")){
                    fname = path.substring(path.lastIndexOf("/")+1);
                    params.put("image", fname);
                }else {
                    params.put("image", "");
                }
                return params;
            }
        };

        queue.add(request);
    }

    private boolean isValidName(String name) {

        if (name != null && name.length() > 0){
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        availabilityChecker.removeInternetConnectivityChangeListener(this);
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (!isConnected) {
            KToast.warningToast(DirectorProfileActivity.this,
                    "Check your Internet Connection",
                    Gravity.BOTTOM,
                    KToast.LENGTH_SHORT);
        }
    }
}
