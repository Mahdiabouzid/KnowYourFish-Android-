package com.example.homescreen;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FischBearbeiten extends AppCompatActivity {
    private static final double MINIMUM_MASS = 50;
    private static final int CAMERA_REQUEST = 123;

    ImageView imageView;
    Button addPictureFromStorageButton;
    Button takePictureButton;
    Button deleteSelectedPictureFromImageViewButton;
    final int requcode = 3;
    Uri bilduri;
    Bitmap bm;
    InputStream is;
    private BottomNavigationView bottomNav;
    private Calendar myCalendar=Calendar.getInstance();


    public String old_fischArt, old_gewicht, old_groesse, old_datum, old_ort;
    byte [] old_bild, bild_url;
    public EditText editTextFischArt, editTextGewicht, editTextGroesse, editTextOrt, editTextDatum;
    private EditText datumText;
    public Button saveButton;
    public int statu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fisch_bearbeiten);
        datumText = (EditText) findViewById(R.id.datumText);

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);

                updateLabel();
            }
        };
        datumText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(FischBearbeiten.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        imageView = (ImageView) findViewById(R.id.fischImageView);
        addPictureFromStorageButton = (Button) findViewById(R.id.addPictureFromStorageButton);
        takePictureButton = (Button) findViewById(R.id.takePictureButton);

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        deleteSelectedPictureFromImageViewButton = (Button) findViewById(R.id.deletePictureFromImageView);
        saveButton = findViewById(R.id.speichernButton);
        saveButton.setOnClickListener(this::onClickHinzufuegen);

        deleteSelectedPictureFromImageViewButton.setVisibility(View.INVISIBLE);
        deleteSelectedPictureFromImageViewButton.setOnClickListener(this::onClickDeletePictureFromImageView);


        statu = getIntent().getIntExtra("statu", 2);

        if(statu == 1){
            readSendedData();
        }

        if (statu == 3) {
            Bundle extras = getIntent().getExtras();
            byte[] byteArray = extras.getByteArray("picture");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imageView.setImageBitmap(bmp);
            addPictureFromStorageButton.setVisibility(View.INVISIBLE);
            takePictureButton.setVisibility(View.INVISIBLE);
            deleteSelectedPictureFromImageViewButton.setVisibility(View.VISIBLE);

            String fish_type = extras.getString("type");
            EditText text = (EditText) findViewById(R.id.fischArtText);
            text.setText(fish_type);
        }

        bottomNav=findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.navigation_statistics);
        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(FischBearbeiten.this, MainActivity.class));
                    break;
                case R.id.navigation_identification:
                    startActivity(new Intent(FischBearbeiten.this, IdentificationActivity.class));
                    break;
                case R.id.navigation_statistics:
                    startActivity(new Intent(FischBearbeiten.this, Fangstatistik.class));
                    break;
                case R.id.navigation_grounds:

                    break;
            }
            return true;
        });
    }
    public void updateLabel(){
        String myFormat="dd.MM.yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.GERMANY);
        datumText.setText(dateFormat.format(myCalendar.getTime()));
    }

    public void readSendedData(){

        EditText textView =  findViewById(R.id.fischArtText);
        editTextFischArt = textView;

        old_fischArt = getIntent().getStringExtra("fischArt");
        textView.setText(old_fischArt);

        textView = findViewById(R.id.fischGewichtText);
        editTextGewicht = textView;
        old_gewicht = getIntent().getStringExtra("gewicht");
        textView.setText(old_gewicht);

        textView = findViewById(R.id.fischGroesseText);
        editTextGroesse = textView;
        old_groesse = getIntent().getStringExtra("groesse");
        textView.setText(old_groesse);

        textView = findViewById(R.id.fischOrtText);
        editTextOrt = textView;
        old_ort = getIntent().getStringExtra("ort");
        textView.setText(old_ort);

        textView = findViewById(R.id.datumText);
        editTextDatum = textView;

        old_datum = getIntent().getStringExtra("datum");
        if(old_datum==""){
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            datumText.setText(df.toString());
        }
        textView.setText(old_datum);

        old_bild = getIntent().getByteArrayExtra("bild");


        if(old_bild != null){
            Bitmap bmp = BitmapFactory.decodeByteArray(old_bild, 0, old_bild.length);
            imageView.setImageBitmap(bmp);

            deleteSelectedPictureFromImageViewButton.setVisibility(View.VISIBLE);
            addPictureFromStorageButton.setVisibility(View.INVISIBLE);
            takePictureButton.setVisibility(View.INVISIBLE);
        }else{
            imageView.setImageBitmap(null);
        }
    }

    public void onClickAbbruch(View view){
        Intent myIntent = new Intent(FischBearbeiten.this, Fangstatistik.class);
        myIntent.putExtra("statu", 2);
        startActivity(myIntent);
    }

    public void onClickHinzufuegen(View view) {

            double currentMass;
            EditText tempMass = (EditText) findViewById(R.id.fischGewichtText);
            String p = tempMass.getText().toString();
            try {
                currentMass = Double.parseDouble(p);
            } catch (NumberFormatException f) {
                currentMass = 0;

            }
            if (currentMass < MINIMUM_MASS || p.length() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FischBearbeiten.this);
                Log.i("Info", "Fish undersized");
                builder.setTitle("Unterfängiger Fisch").setMessage("Der Fisch darf nicht entnommen werden da er unter dem zulässigen Gewicht liegt!" +
                                "Das Mindestgewicht liegt bei 50g.")
                        .setCancelable(false)
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .setNegativeButton(R.string.alert_no_text, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Intent myIntent = new Intent(FischBearbeiten.this, Fangstatistik.class);

                myIntent.putExtra("statu", statu);

                if (statu == 1) {
                    myIntent.putExtra("oldFischArt", old_fischArt);
                    myIntent.putExtra("oldGewicht", old_gewicht);
                    myIntent.putExtra("oldGroesse", old_groesse);
                    myIntent.putExtra("oldOrt", old_ort);
                    if(old_datum==""){
                        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
                        old_datum=df.toString();
                    }
                    myIntent.putExtra("oldDatum", old_datum);
                    myIntent.putExtra("oldBild", old_bild);
                }

                EditText editText = findViewById(R.id.fischArtText);
                myIntent.putExtra("fischArt", editText.getText().toString());
                Log.i("INFO FISH",editText.getText().toString() );

                editText = findViewById(R.id.fischGewichtText);
                myIntent.putExtra("gewicht", editText.getText().toString());

                editText = findViewById(R.id.fischGroesseText);
                myIntent.putExtra("groesse", editText.getText().toString());

                editText = findViewById(R.id.fischOrtText);
                myIntent.putExtra("ort", editText.getText().toString());

                editText = findViewById(R.id.datumText);
                myIntent.putExtra("datum", editText.getText().toString());
                try {
                    Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    myIntent.putExtra("bild", byteArray);
                } catch (Exception e) {
                    e.printStackTrace();
                    byte[] byteArray = null;
                    myIntent.putExtra("bild", byteArray);
                }
                startActivity(myIntent);
            }
        }

    public void onClickAddPicture(View view){

        Intent myIntent = new Intent(Intent.ACTION_GET_CONTENT);
        myIntent.setType("image/*");
        startActivityForResult(myIntent, requcode);
    }

    public void onClickDeletePictureFromImageView(View view){

        imageView.setImageBitmap(null);
        addPictureFromStorageButton.setVisibility(View.VISIBLE);
        takePictureButton.setVisibility(View.VISIBLE);
        deleteSelectedPictureFromImageViewButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == requcode) {
                bilduri = data.getData();

                try {
                    is = getContentResolver().openInputStream(bilduri);
                    bm = BitmapFactory.decodeStream(is);
                    imageView.setImageBitmap(bm);

                    addPictureFromStorageButton.setVisibility(View.INVISIBLE);
                    takePictureButton.setVisibility(View.INVISIBLE);
                    deleteSelectedPictureFromImageViewButton.setVisibility(View.VISIBLE);

                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == CAMERA_REQUEST) {
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
                addPictureFromStorageButton.setVisibility(View.INVISIBLE);
                takePictureButton.setVisibility(View.INVISIBLE);
                deleteSelectedPictureFromImageViewButton.setVisibility(View.VISIBLE);
            } catch (NullPointerException e) {
                Log.e("Camera ACTIVITY", "no photo taken", e);
            }
        }
       super.onActivityResult(requestCode, resultCode, data);
    }
}