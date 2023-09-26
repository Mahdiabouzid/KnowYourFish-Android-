package com.example.homescreen;

public class ChildItem {

    String itemName, itemQty, itemPrice;
    Fangstatistik.Fisch currentFisch;

    public ChildItem(Fangstatistik.Fisch fisch) {
        currentFisch = fisch;
        this.itemName = fisch.fischArt;
        this.itemQty = "Gewicht : ";
        this.itemPrice = fisch.gewicht;
    }
}