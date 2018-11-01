package com.app.mcc.member;

import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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

public class MemberUploadActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    ImageView ivVideo, ivAudio, ivPhoto, ivAudioVerified, ivPhotoVerified, ivVideoVerified;
    String PHOTO_URL = Constants.MEMBER_URL + Constants.PHOTO_UPLOAD;
    String VIDEO_URL = Constants.MEMBER_URL + Constants.VIDEO_UPLOAD;
    String AUDIO_URL = Constants.MEMBER_URL + Constants.AUDIO_UPLOAD;
    String UPLOAD_URL = Constants.MEMBER_URL + Constants.UPLOAD_MEDIA;
    public static final int AUDIO_REQUEST = 3;
    public static final int VIDEO_REQUEST = 4;
    public static final int PERMISSION_CODE = 6;
    public static final int PHOTO_REQUEST = 5;
    RequestQueue queue;
    Bitmap bitmap;
    Button btnUpload;
    Uri filePath;
    String path;
    Dialog progressDialog;
    String audiofilename, videofilename, photofilename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_upload);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        Constants.pref = getApplicationContext().getSharedPreferences("MCC",0);
        Constants.editor = Constants.pref.edit();

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("UPLOAD MEDIA");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        requestPermission();
        queue = Volley.newRequestQueue(this);

        ivAudio = findViewById(R.id.mem_profile_audio);
        ivVideo = findViewById(R.id.mem_profile_video);
        ivPhoto= findViewById(R.id.mem_profile_photo);
        ivPhotoVerified = findViewById(R.id.mem_profile_photo_verified);
        ivAudioVerified = findViewById(R.id.mem_profile_audio_verified);
        ivVideoVerified = findViewById(R.id.mem_profile_video_verified);
        btnUpload = findViewById(R.id.mem_media_upload);

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

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadMedia();
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
                KToast.successToast(MemberUploadActivity.this,
                        "Permission Granted",
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            } else {
                //Displaying another toast if permission is not granted
                KToast.warningToast(MemberUploadActivity.this,
                        "Permission Denied",
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

         if (requestCode == AUDIO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

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

        progressDialog = new Dialog(MemberUploadActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setTitle(R.string.uploading);
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
                    KToast.warningToast(MemberUploadActivity.this,
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
                            KToast.successToast(MemberUploadActivity.this,
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
                        KToast.errorToast(MemberUploadActivity.this,
                                "File not Found" + fileName,
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                KToast.errorToast(MemberUploadActivity.this,
                        "URL Error" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);

            } catch (IOException e) {
                e.printStackTrace();
                KToast.errorToast(MemberUploadActivity.this,
                        "Cannot Read/Write File" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            }
            progressDialog.dismiss();
            return serverResponseCode;
        }
    }

    private void uploadPhoto() {

        progressDialog = new Dialog(MemberUploadActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setTitle(R.string.uploading);
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
                    KToast.warningToast(MemberUploadActivity.this,
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
                            KToast.successToast(MemberUploadActivity.this,
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
                        KToast.errorToast(MemberUploadActivity.this,
                                "File not Found" + fileName,
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                KToast.errorToast(MemberUploadActivity.this,
                        "URL Error" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);

            } catch (IOException e) {
                e.printStackTrace();
                KToast.errorToast(MemberUploadActivity.this,
                        "Cannot Read/Write File" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            }
            progressDialog.dismiss();
            return serverResponseCode;
        }
    }

    private void uploadAudio() {

        progressDialog = new Dialog(MemberUploadActivity.this);
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
                    KToast.warningToast(MemberUploadActivity.this,
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
                            KToast.successToast(MemberUploadActivity.this,
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
                        KToast.errorToast(MemberUploadActivity.this,
                                "File not Found" + fileName,
                                Gravity.BOTTOM,
                                KToast.LENGTH_SHORT);
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                KToast.errorToast(MemberUploadActivity.this,
                        "URL Error" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);

            } catch (IOException e) {
                e.printStackTrace();
                KToast.errorToast(MemberUploadActivity.this,
                        "Cannot Read/Write File" + fileName,
                        Gravity.BOTTOM,
                        KToast.LENGTH_SHORT);
            }
            progressDialog.dismiss();
            return serverResponseCode;
        }
    }

    private void uploadMedia() {

        progressDialog = new Dialog(MemberUploadActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status")
                                    .equalsIgnoreCase("success")){
                                progressDialog.hide();
                                startActivity(new Intent(MemberUploadActivity.this, HomeActivity.class));

                                KToast.successToast(MemberUploadActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }else  if (jsonObject.getString("status")
                                    .equalsIgnoreCase("failed")){
                                progressDialog.hide();
                                KToast.errorToast(MemberUploadActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }else  if (jsonObject.getString("status")
                                    .equalsIgnoreCase("empty")){
                                progressDialog.hide();
                                KToast.errorToast(MemberUploadActivity.this,
                                        jsonObject.getString("message"),
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);
                            }
                            else {
                                progressDialog.hide();
                                KToast.errorToast(MemberUploadActivity.this,
                                        "Something Went Wrong!",
                                        Gravity.BOTTOM,
                                        KToast.LENGTH_SHORT);

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            progressDialog.hide();
                            KToast.errorToast(MemberUploadActivity.this,
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
                        KToast.errorToast(MemberUploadActivity.this,
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
                params.put("photo", photofilename);
                params.put("audio", audiofilename);
                params.put("video", videofilename);
                params.put("mobileno", Constants.pref.getString("phone",""));
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
            KToast.warningToast(MemberUploadActivity.this,
                    "Check your Internet Connection",
                    Gravity.BOTTOM,
                    KToast.LENGTH_SHORT);
        }
    }
}
