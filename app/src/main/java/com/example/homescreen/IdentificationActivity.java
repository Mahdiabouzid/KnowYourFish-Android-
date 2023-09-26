package com.example.homescreen;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class IdentificationActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 123;
    private static final int SELECT_PICTURE = 200;
    private static int VOLLEY_TIMEOUT = 0;
    private JSONObject best_label = null;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);
        best_label = null;
        Button photoButton = (Button) this.findViewById(R.id.take_photo_button);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        Button back = (Button) this.findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.navigation_identification);
        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(IdentificationActivity.this, MainActivity.class));
                    break;
                case R.id.navigation_identification:
                    break;
                case R.id.navigation_statistics:
                    startActivity(new Intent(IdentificationActivity.this, Fangstatistik.class));
                    break;
                case R.id.navigation_grounds:
                    break;
            }
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        best_label = null;
        if (requestCode == CAMERA_REQUEST) {
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                openLoadingDialog(photo);
            } catch (NullPointerException e) {
                Log.e("Camera ACTIVITY", "no photo taken", e);
            }
        }

        if (requestCode == SELECT_PICTURE) {
            InputStream imageStream = null;
            try {
                imageStream = this.getContentResolver().openInputStream(data.getData());
                Bitmap image = BitmapFactory.decodeStream(imageStream);
                openLoadingDialog(image);
            } catch (FileNotFoundException e) {
               Log.e("Exception", "FileNotFoundException", e);
            }
        }
    }

    /**
     * Select an image from galley
     */
    private void imageChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    /**
     * wait 3 seconds until API is called
     */
    public void openLoadingDialog(Bitmap image) {
        Log.i("INFO", "opening loading Dialog");
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Bitte Warten");
        progressDialog.setMessage("Laden ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        Thread backgroundThread = new Thread(new Runnable()
        {
            public void run()
            {
                Looper.prepare(); // prevent force close error
                try {
                    callAPI(convertToBase64(image));
                    while (best_label == null && VOLLEY_TIMEOUT != 1) {
                        Log.i("WAITING_PROCESS", "sleeping !");
                        Thread.sleep(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        progressDialog.dismiss();
                        try {
                            showAlert(image);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        backgroundThread.start();
    }


    /**
     * start new Intent
     * @param image image of the fish
     * @param fish_type type of the fish
     */
    private void addEntryToStatistik(Bitmap image, String fish_type) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Intent intent = new Intent(IdentificationActivity.this, FischBearbeiten.class);
        intent.putExtra("picture", byteArray);
        intent.putExtra("type", fish_type);
        intent.putExtra("statu", 3);
        startActivity(intent);
    }

    /**
     * show alert Box to ask if you want to add fish
     * on click yes: send image with type to activity
     */
    private void showAlert(Bitmap image) throws JSONException {

        AlertDialog.Builder builder = new AlertDialog.Builder(IdentificationActivity.this);

        // if API didn't respond yet
        if (best_label == null) {
            Log.i("Info", "best label is null");
            VOLLEY_TIMEOUT = 0;
            builder.setTitle("Server antwortet nicht :( ").setMessage("Möchten Sie nochmal versuchen ?")
                    .setCancelable(false)
                    .setPositiveButton(R.string.try_again_text, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            openLoadingDialog(image);
                        }
                    })
                    .setNegativeButton(R.string.alert_no_text, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Toast toast = Toast.makeText(getApplicationContext(), "Aktion abgebrochen", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            float probability = Float.parseFloat(best_label.getString("prob"));
            // if probability is less than 40% then fish retry
            if (probability < 0.40 ) {
                builder.setMessage("Versuchen Sie ein anderes Foto zu machen.").setTitle("Art wurde nicht erkannt !")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            } else {
                builder.setMessage(R.string.dialog_message).setTitle("Es ist ein " + best_label.getString("name"))
                        .setCancelable(false)
                        .setPositiveButton(R.string.alert_yes_text, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    addEntryToStatistik(image, best_label.getString("name"));
                                } catch (JSONException e) {
                                    Log.e("Exception", "error sending JSON to addEntry", e);
                                }
                            }
                        })
                        .setNegativeButton(R.string.alert_no_text, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Toast toast = Toast.makeText(getApplicationContext(), "Aktion abgebrochen", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    /**
     * convert image to base64 String
     * @param photo Bitmap
     * @return base64 string
     */
    private String convertToBase64(Bitmap photo) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * call API and return type of fish
     * @param base64 String
     */
    public void callAPI(String base64) {
        Log.i("API", "calling API");
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String URL = "https://api.ximilar.com/recognition/v2/classify/";
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("task", "5db55df7-8cf4-4653-8039-34ad29032a10");
                jsonBody.put("version", "4");

                JSONArray records = new JSONArray();
                JSONObject base = new JSONObject();
                base.put("_base64", "data:image/jpeg;base64," + base64);
                records.put(base);
                jsonBody.put("records", records);
                final String requestBody = jsonBody.toString();
                Log.v("json body", requestBody);
                JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("records");
                            best_label = array.getJSONObject(0).getJSONObject("best_label");
                            Log.i("VOLLEY", best_label.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", error.toString());
                        VOLLEY_TIMEOUT = 1;
                    }
                }) {

                    @Override
                    public byte[] getBody()  {
                        try {
                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", "Token f185326c83331f331aa363f600887a486985f6ee");
                        return headers;
                    }

                };

                requestQueue.add(stringRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
}