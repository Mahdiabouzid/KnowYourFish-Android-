package com.example.homescreen;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FischCharakteristik extends AppCompatActivity {

    Button editButton;
    Button backButton;
    public static TextView textViewFischArt, textViewGroesse, textViewGewicht, textViewOrt, textViewDatum;
    public byte[] bild_url;
    public ImageView imageView;
    private BottomNavigationView bottomNav;
    private Button deleteFish;

    public int status;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fisch_charakteristik);
        editButton = (Button) findViewById(R.id.bearbeitenButton);
        backButton = (Button) findViewById(R.id.zurueckButton);
        imageView = (ImageView) findViewById(R.id.imageView);

        status = getIntent().getIntExtra("statu", 2);

        readSendedData();
        deleteFish=(Button) findViewById(R.id.deleteFishButton);
        deleteFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Info", "best label is null");
                AlertDialog.Builder builder = new AlertDialog.Builder(FischCharakteristik.this);
                Log.i("Info", "Delete Fish");
                builder.setTitle("Eintrag löschen").setMessage("Möchten sie den Eintrag löschen?")
                        .setCancelable(false)
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent myintent=new Intent(FischCharakteristik.this, Fangstatistik.class);
                                myintent.putExtra("delete", 1);
                                myintent.putExtra("oldFischArt", textViewFischArt.getText().toString());
                                myintent.putExtra("oldGewicht", textViewGewicht.getText().toString());
                                myintent.putExtra("oldGroesse", textViewGroesse.getText().toString());
                                myintent.putExtra("oldOrt", textViewOrt.getText().toString());
                                myintent.putExtra("oldDatum", textViewDatum.getText().toString());
                                myintent.putExtra("oldBild", bild_url);

                                startActivity(myintent);

                            }
                        })
                        .setNegativeButton(R.string.alert_no_text, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        editButton.setOnClickListener(this::onClickBearbeiten);
        backButton.setOnClickListener(this::onClickZurueck);
        bottomNav=findViewById(R.id.bottomNavigationView);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.navigation_statistics);
        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(FischCharakteristik.this, MainActivity.class));
                    break;
                case R.id.navigation_identification:
                    startActivity(new Intent(FischCharakteristik.this, IdentificationActivity.class));
                    break;
                case R.id.navigation_statistics:
                    startActivity(new Intent(FischCharakteristik.this, Fangstatistik.class));
                    break;
                case R.id.navigation_grounds:

                    break;
            }
            return true;
        });
    }

    public void readSendedData(){
    TextView textView =  findViewById(R.id.textFischArtRead);
    textViewFischArt = textView;
    textView.setText(getIntent().getStringExtra("fischArt"));

    textView = findViewById(R.id.textViewGewichtRead);
    textViewGewicht = textView;
    textView.setText(getIntent().getStringExtra("gewicht"));

    textView = findViewById(R.id.textViewGroesseRead);
    textViewGroesse = textView;
    textView.setText(getIntent().getStringExtra("groesse"));

    textView = findViewById(R.id.textViewOrtRead);
    textViewOrt = textView;
    textView.setText(getIntent().getStringExtra("ort"));

    textView = findViewById(R.id.texViewDatumReaded);
    textViewDatum = textView;
    textView.setText(getIntent().getStringExtra("datum"));

    bild_url = getIntent().getByteArrayExtra("bild");

    if(bild_url != null){
        Bitmap bmp = BitmapFactory.decodeByteArray(bild_url, 0, bild_url.length);
        imageView.setImageBitmap(bmp);
    }else{
        imageView.setImageBitmap(null);
    }

    }

    public void onClickBearbeiten(View view){
        Intent myIntent = new Intent(view.getContext(), FischBearbeiten.class);
        myIntent.putExtra("statu", 1);

        myIntent.putExtra("fischArt", textViewFischArt.getText().toString());
        myIntent.putExtra("gewicht", textViewGewicht.getText().toString());
        myIntent.putExtra("groesse", textViewGroesse.getText().toString());
        myIntent.putExtra("ort", textViewOrt.getText().toString());
        myIntent.putExtra("datum", textViewDatum.getText().toString());
        myIntent.putExtra("bild", bild_url);

        startActivity(myIntent);
    }

    public void onClickZurueck(View view){
        Intent myIntent = new Intent(view.getContext(), Fangstatistik.class);
        myIntent.putExtra("statu", 2);
        startActivity(myIntent);
    }
}