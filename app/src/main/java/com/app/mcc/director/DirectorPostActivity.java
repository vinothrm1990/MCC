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
import android.text.Editable;
import android.text.TextWatcher;
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
import com.app.mcc.helper.Constants;
import com.app.mcc.helper.FilePath;
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

public class DirectorPostActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    AppCompatEditText etTitle;
    TextView tvImage;
    ImageView ivPost;
    Button btnSumbit, btnChoose, btnUpload;
    Dialog progressDialog;
    public static final int PICK_REQUEST = 1;
    public static final int PERMISSION_CODE = 2;
    String filename, path;
    Bitmap bitmap;
    RequestQueue queue;
    Uri filePath;
    String UPLOAD_URL = Constants.DIRECTOR_URL + Constants.POST_UPLOAD;
    String POST_URL = Constants.DIRECTOR_URL + Constants.ADD_POST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_post);

        Constants.pref = getApplicationContext().getSharedPreferences("MCC",0);
        Constants.editor = Constants.pref.edit();

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("ADD POST");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        ivPost = findViewById(R.id.dir_post_iv);
        tvImage = findViewById(R.id.dir_post_image_name);
        btnChoose = findViewById(R.id.btn_post_choose);
        btnUpload = findViewById(R.id.btn_post_upload);
        etTitle = findViewById(R.id.dir_post_title);
        btnSumbit = findViewById(R.id.dir_post_submit);

        queue = Volley.newRequestQueue(DirectorPostActivity.this);
        requestPermission();

        btnSumbit.setEnabled(false);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_REQUEST);

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (path != null){
                    progressDialog = new Dialog(DirectorPostActivity.this);
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
                }else {
                    KToast.warningToast(DirectorPostActivity.this,
                            "Select the Image for the Post",
                            Gravity.BOTTOM,
                            KToast.LENGTH_SHORT);
                }

            }
        });

        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (etTitle.getText().toString().trim().length() != 0 &&
                        tvImage.getText().toString().trim().length() != 0){
                    btnSumbit.setEnabled(true);
                }else {
                    KToast.infoToast(DirectorPostActivity.this,
                            "Feilds are Missing",
                            Gravity.BOTTOM,
                            KToast.LENGTH_SHORT);
                    btnSumbit.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnSumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageDetails();
            }
        });


    }

    private void uploadImageDetails() {

        progressDialog = new Dialog(DirectorPostActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status")
                                    .equalsIgnoreCase("success")){

                                KToast.successToast(DirectorPostActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else if (jsonObject.getString("status")
                                    .equalsIgnoreCase("failed")){
                                KToast.errorToast(DirectorPostActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else {
                                KToast.errorToast(DirectorPostActivity.this,
                                        "Something Went Wrong!",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            KToast.errorToast(DirectorPostActivity.this,
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

                        KToast.errorToast(DirectorPostActivity.this,
                                error.getMessage(),
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                String title = etTitle.getText().toString().trim();
                String image = tvImage.getText().toString().trim();

                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobileno", Constants.pref.getString("mobileno", ""));
                params.put("title", title);
                params.put("image", image);
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
                KToast.successToast(DirectorPostActivity.this,
                        "Permission Granted",
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            } else {
                //Displaying another toast if permission is not granted
                KToast.warningToast(DirectorPostActivity.this,
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
                filename=path.substring(path.lastIndexOf("/")+1);
                tvImage.setText(filename);
                tvImage.setSelected(true);
            }
            try{
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                ivPost.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
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
                    KToast.warningToast(DirectorPostActivity.this,
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

                            KToast.successToast(DirectorPostActivity.this,
                                    "File Uploaded : " + fileName,
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
                        KToast.errorToast(DirectorPostActivity.this,
                                "File not Found" + fileName,
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                KToast.errorToast(DirectorPostActivity.this,
                        "URL Error" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);

            } catch (IOException e) {
                e.printStackTrace();
                KToast.errorToast(DirectorPostActivity.this,
                        "Cannot Read/Write File" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
                }
            progressDialog.dismiss();
            return serverResponseCode;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        availabilityChecker.removeInternetConnectivityChangeListener(this);
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (!isConnected) {
            KToast.warningToast(DirectorPostActivity.this,
                    "Check your Internet Connection",
                    Gravity.BOTTOM,
                    KToast.LENGTH_SHORT);
        }
    }
}
