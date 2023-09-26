package com.example.homescreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import org.apache.commons.codec.binary.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Vector;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class Fangstatistik extends AppCompatActivity implements MyAdapter.OnKategorieClick, AdapterView.OnItemSelectedListener, MemberAdp.OnClickOnSubKategorie {


    @Override
    public void OnKategorieClick(int position) {
        if(kategorieItemList.get(position).isEmpty()){

            kategorieItemList.set(position, kategorieItemListCopy.get(position));

            myAdapter = new MyAdapter(this,parentItemArrayList,kategorieItemList, this, this);
            RVparent.setAdapter(myAdapter);

        }else{
            ArrayList<ArrayList> empty = new ArrayList<>();
            kategorieItemList.set(position, empty);
            try {
                ParentItem parentItem = parentItemArrayList.get(position);
                parentItem.icon_bild = ((ChildItem) kategorieItemListCopy.get(position).get(0)).currentFisch.bild_URL;
                parentItemArrayList.set(position, parentItem);
            } catch (Exception e) {
                e.printStackTrace();
            }

            myAdapter = new MyAdapter(this, parentItemArrayList, kategorieItemList, this, this);
            RVparent.setAdapter(myAdapter);

        }


    }

    @Override
    public void OnClickOnSubFisch(ChildItem childItem) {
        Intent myIntent = new Intent(Fangstatistik.this, FischCharakteristik.class);
        myIntent.putExtra("statu", 2);

        myIntent.putExtra("fischArt", childItem.currentFisch.fischArt);
        myIntent.putExtra("gewicht", childItem.currentFisch.gewicht);
        myIntent.putExtra("groesse", childItem.currentFisch.groesse);
        myIntent.putExtra("ort", childItem.currentFisch.ort);
        myIntent.putExtra("datum", childItem.currentFisch.datum);
        myIntent.putExtra("bild", childItem.currentFisch.bild_URL);
        startActivity(myIntent);
    }

    @Override
    public void OnClickOnEdit(ChildItem childItem) {
        Intent myIntent = new Intent(Fangstatistik.this, FischBearbeiten.class);
        myIntent.putExtra("statu", 1);

        myIntent.putExtra("fischArt", childItem.currentFisch.fischArt);
        myIntent.putExtra("gewicht", childItem.currentFisch.gewicht);
        myIntent.putExtra("groesse", childItem.currentFisch.groesse);
        myIntent.putExtra("ort", childItem.currentFisch.ort);
        myIntent.putExtra("datum", childItem.currentFisch.datum);
        myIntent.putExtra("bild", childItem.currentFisch.bild_URL);

        startActivity(myIntent);
    }
    public void deleteEntry(){
        for(int i = 0; i < alleFische.size(); i++){
            if(checkFischIsIdenticall(alleFische.elementAt(i))){
                alleFische.remove(i);
                return;
            }
        }

    }
    public static class FischCopy{
        public String fischArt;
        public String groesse;
        public String gewicht;
        public String ort;
        public String datum;
        public String bild_URL;
    }

    public static class Fisch {
        public String fischArt;
        public String groesse;
        public String gewicht;
        public String ort;
        public String datum;
        public byte[] bild_URL;

    }

    public Spinner spinnerDropDownFilter;
    public ImageButton exportButton;
    public ImageButton filterButton;
    private BottomNavigationView bottomNav;
    public MyAdapter myAdapter;
    public static ArrayList<ParentItem> parentItemArrayList;
    public ArrayList<ChildItem> childItemArrayList;
    public static ArrayList<ArrayList> kategorieItemList;
    public static ArrayList<ArrayList> kategorieItemListCopy;
    public RecyclerView RVparent;


    public static Vector<Fisch> alleFische = new Vector();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fangstatistik);

        readAllFische();
        checkDataSend();

        spinnerDropDownFilter = (Spinner) findViewById(R.id.selectKategorieSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.dropDownFilterFangstatistik, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDropDownFilter.setAdapter(adapter);
        spinnerDropDownFilter.setOnItemSelectedListener(this);
        filterButton = findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerDropDownFilter.performClick();


            }
        });

        bottomNav=findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.navigation_statistics);
        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(Fangstatistik.this, MainActivity.class));
                    break;
                case R.id.navigation_identification:
                    startActivity(new Intent(Fangstatistik.this, IdentificationActivity.class));
                    break;
                case R.id.navigation_statistics:
                    break;
                case R.id.navigation_grounds:
                    break;
            }
            return true;
        });


        RVparent = findViewById(R.id.RVparent);

        createNestedView("Datum");

    }
    public boolean exportFische(){


        return true;
    }
    private void createNestedView(String kategorie) {
        int[] imageId = {R.drawable.fisch1,R.drawable.fisch2,R.drawable.fisch3,R.drawable.fisch4, R.drawable.fisch5, R.drawable.fisch6};
        parentItemArrayList = new ArrayList<>();
        kategorieItemListCopy = new ArrayList<>();

        kategorieItemList = new ArrayList<>();

        //filter for kategorie datum
        Vector alleDatums = new Vector();

        if(kategorie.equals("Datum")){
            for(int i = 0; i < alleFische.size(); i++){
                String datum = alleFische.elementAt(i).datum;
                datum = revertDatum(datum);
                if(datumNotFound(alleDatums, datum)){
                    alleDatums.add(alleFische.elementAt(i).datum);
                }
            }
        }

        if(kategorie.equals("FangOrt")){
            for(int i = 0; i < alleFische.size(); i++){
                String datum = alleFische.elementAt(i).ort;
                if(datumNotFound(alleDatums, datum)){
                    alleDatums.add(alleFische.elementAt(i).ort);
                }
            }
        }

        if(kategorie.equals("FischArt")){
            for(int i = 0; i < alleFische.size(); i++){
                String datum = alleFische.elementAt(i).fischArt;
                if(datumNotFound(alleDatums, datum)){
                    alleDatums.add(alleFische.elementAt(i).fischArt);
                }
            }
        }


        int n = alleDatums.size();
        String temp = "";

        for(int i = 0; i < n; i++){
            for(int j = 1; j < (n - i); j++){
                if(checkLhsBiggerThanRhs(revertDatum(alleDatums.elementAt(j).toString()), revertDatum(alleDatums.elementAt(j - 1).toString()))){
                    temp = alleDatums.elementAt(j - 1).toString();
                    alleDatums.setElementAt(alleDatums.elementAt(j), j - 1);
                    alleDatums.setElementAt(temp, j);
                }
            }
        }

        for (int i = 0 ; i < alleDatums.size(); i++ ){
            childItemArrayList = new ArrayList<>();
            ParentItem parentItem = new ParentItem(alleDatums.elementAt(i).toString());
            parentItemArrayList.add(parentItem);

            //Child Item Object
            if(kategorie.equals("Datum")){
                for(int j = 0; j < alleFische.size(); j++){
                    if(alleDatums.elementAt(i).equals(alleFische.elementAt(j).datum)){
                        ChildItem childItem = new ChildItem(alleFische.elementAt(j));
                        childItemArrayList.add(childItem);
                    }
                }
            }

            if(kategorie.equals("FischArt")){
                for(int j = 0; j < alleFische.size(); j++){
                    if(alleDatums.elementAt(i).equals(alleFische.elementAt(j).fischArt)){
                        ChildItem childItem = new ChildItem(alleFische.elementAt(j));
                        childItemArrayList.add(childItem);
                    }
                }
            }

            if(kategorie.equals("FangOrt")){
                for(int j = 0; j < alleFische.size(); j++){
                    if(alleDatums.elementAt(i).equals(alleFische.elementAt(j).ort)){
                        ChildItem childItem = new ChildItem(alleFische.elementAt(j));
                        childItemArrayList.add(childItem);
                    }
                }
            }
            kategorieItemList.add(childItemArrayList);
            kategorieItemListCopy.add(childItemArrayList);
        }


        myAdapter = new MyAdapter(this,parentItemArrayList,kategorieItemList, this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RVparent.setLayoutManager(layoutManager);
        RVparent.setAdapter(myAdapter);
    }

    public String revertDatum(String datum){

        if(datum.length() >= 10){
            String tmp = datum.substring(6,10) + "." + datum.substring(3,6) + datum.substring(0,2);
            return tmp;
        }else {
            return datum;
        }


    }

    public boolean checkLhsBiggerThanRhs(String lhs, String rhs){
        int size = 0;

        if(lhs.length() > rhs.length()){
            size = rhs.length();
        }else{
            size = lhs.length();
        }

        for(int i = 0; i < size; i++){
            if(lhs.charAt(i) > rhs.charAt(i)){
                return true;
            }
            if(lhs.charAt(i) < rhs.charAt(i)){
                return false;
            }
        }

        if(lhs.length() > rhs.length()){
            return true;
        }else{
            return false;
        }
    }

    public boolean datumNotFound(Vector alleDatums, String datum){
    for(int j = 0; j < alleDatums.size(); j++) {
        String datum_tmp = revertDatum(alleDatums.elementAt(j).toString());
        if( datum_tmp.equals(datum)) {
            return false;
        }
    }
    return true;
}

    public void checkDataSend(){
        int status = getIntent().getIntExtra("statu", 2);
        int delete = getIntent().getIntExtra("delete", 0);

        Log.d("DEBUG", "status :" + status);
        //Ist es ein neuer Fisch
        if(status == 0 || status == 3){
            Log.d("DEBUG", "status 0 option");
            takeSendDataFromIntentAndStoresInAlleFische(-1);
            return;
        }
        //Fisch wurde von einem Datensatz bearbeitet
        if(status == 1){
            removeOldFischAndStoreNewFischInAlleFisch();
            return;
        }

        if(delete==1){
            deleteEntry();
        }
    }

    private void removeOldFischAndStoreNewFischInAlleFisch() {


        for(int i = 0; i < alleFische.size(); i++){
            if(checkFischIsIdenticall(alleFische.elementAt(i))){
                Log.d("DEBUG", "alter Fisch wurde gelöscht");
                takeSendDataFromIntentAndStoresInAlleFische(i);
                return;
            }
        }
    }

    public boolean checkFischIsIdenticall(Fisch completFisch){
        String fischArt = getIntent().getStringExtra("oldFischArt");
        String gewicht = getIntent().getStringExtra("oldGewicht");
        String fangOrt = getIntent().getStringExtra("oldOrt");
        String groesse = getIntent().getStringExtra("oldGroesse");
        String datum = getIntent().getStringExtra("oldDatum");
        byte[] speicherOrtBild = getIntent().getByteArrayExtra("oldBild");

        Fisch fisch = new Fisch();
        fisch.fischArt = fischArt;
        fisch.gewicht = gewicht;
        fisch.ort = fangOrt;
        fisch.groesse = groesse;
        fisch.datum = datum;
        fisch.bild_URL = speicherOrtBild;

        if(!completFisch.fischArt.equals(fisch.fischArt)){
            return false;
        }
        if(!completFisch.groesse.equals(fisch.groesse)){
            return false;
        }
        if(!completFisch.gewicht.equals(fisch.gewicht)){
            return false;
        }
        if(!completFisch.ort.equals(fisch.ort)){
            return false;
        }
        if(!completFisch.datum.equals(fisch.datum)){
            return false;
        }
        /*if(Arrays.deepEquals(completFisch.bild_URL, fisch.bild_URL)){
            return false;
        }*/
        return true;
    }

    private void takeSendDataFromIntentAndStoresInAlleFische(int index) {
        //Log.d("DEBUG", )
        String fischArt = getIntent().getStringExtra("fischArt");
        String gewicht = getIntent().getStringExtra("gewicht");
        String fangOrt = getIntent().getStringExtra("ort");
        String groesse = getIntent().getStringExtra("groesse");
        String datum = getIntent().getStringExtra("datum");
        byte[] speicherOrtBild = getIntent().getByteArrayExtra("bild");

        Fisch fisch = new Fisch();
        fisch.fischArt = fischArt;
        fisch.gewicht = gewicht;
        fisch.ort = fangOrt;
        fisch.groesse = groesse;
        fisch.datum = datum;
        fisch.bild_URL = speicherOrtBild;

        if(index == -1){
            Log.d("DEBUG", "Data is push into alleFische");
            alleFische.add(fisch);
        }else{
            alleFische.remove(index);
            alleFische.add(fisch);
        }


    }

    public void onClickFischHinzufuegen(View view){
        Intent myIntent = new Intent(Fangstatistik.this , FischBearbeiten.class);
        myIntent.putExtra("statu", 0);
        startActivity(myIntent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedItem = spinnerDropDownFilter.getSelectedItem().toString();

        if(selectedItem.equals("Datum")){
            createNestedView("Datum");
        }

        if(selectedItem.equals("FangOrt")){
            createNestedView("FangOrt");
        }

        if(selectedItem.equals("Fischart")){
            createNestedView("FischArt");
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void onClickFilterButton(View view){

    }

    public void onClickArterkennung(View view){
        Intent myIntent = new Intent(Fangstatistik.this, IdentificationActivity.class);
        startActivity(myIntent);
    }

    public void readAllFische(){

        // JSON-Datei einlesen
        ObjectMapper mapper = new ObjectMapper();
        try {
            Vector<FischCopy> alleFischeCopy = new Vector<FischCopy>();
            File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            alleFischeCopy = mapper.readValue(new File(dir, "alleFische.json"), new TypeReference<Vector<FischCopy>>() {
            });

            alleFische = new Vector<>();
            for(int i = 0; i < alleFischeCopy.size(); i++){
                Fisch fisch = new Fisch();
                fisch.fischArt = alleFischeCopy.elementAt(i).fischArt;
                fisch.ort = alleFischeCopy.elementAt(i).ort;
                fisch.datum = alleFischeCopy.elementAt(i).datum;
                fisch.gewicht = alleFischeCopy.elementAt(i).gewicht;
                fisch.groesse = alleFischeCopy.elementAt(i).groesse;
                fisch.bild_URL = Base64.decodeBase64(alleFischeCopy.elementAt(i).bild_URL);
                alleFische.add(fisch);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause(){
        super.onPause();
        ObjectMapper mapper = new ObjectMapper();

        try {

            Vector<FischCopy> alleFischeCopy = new Vector<>();

            for(int i = 0; i < alleFische.size(); i++){
                FischCopy fischCopy = new FischCopy();
                fischCopy.fischArt = alleFische.elementAt(i).fischArt;
                fischCopy.datum = alleFische.elementAt(i).datum;
                fischCopy.gewicht = alleFische.elementAt(i).gewicht;
                fischCopy.ort = alleFische.elementAt(i).ort;
                fischCopy.groesse = alleFische.elementAt(i).groesse;
                fischCopy.bild_URL = Base64.encodeBase64String(alleFische.elementAt(i).bild_URL);
                alleFischeCopy.add(fischCopy);
            }

            File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            mapper.writeValue(new File(dir,"alleFische.json"), alleFischeCopy);
        } catch (IOException e) {
            e.printStackTrace();
        }

        getIntent().removeExtra("statu");
    }

    protected void onDestroy() {
        super.onDestroy();
        ObjectMapper mapper = new ObjectMapper();

        try {
            Log.d("DEBUG", "Datei wird gespeichert");
            File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

            Vector<FischCopy> alleFischeCopy = new Vector<>();

            for(int i = 0; i < alleFische.size(); i++){
                FischCopy fischCopy = new FischCopy();
                fischCopy.fischArt = alleFische.elementAt(i).fischArt;
                fischCopy.datum = alleFische.elementAt(i).datum;
                fischCopy.gewicht = alleFische.elementAt(i).gewicht;
                fischCopy.ort = alleFische.elementAt(i).ort;
                fischCopy.groesse = alleFische.elementAt(i).groesse;
                fischCopy.bild_URL = Base64.encodeBase64String(alleFische.elementAt(i).bild_URL);
                alleFischeCopy.add(fischCopy);
            }


            mapper.writeValue(new File(dir,"alleFische.json"), alleFischeCopy);
        } catch (IOException e) {
            e.printStackTrace();
        }

        getIntent().removeExtra("statu");
    }

}