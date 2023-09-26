package com.example.homescreen;

import android.graphics.drawable.Drawable;

public class ParentItem {

    String kategorie;
    byte[] icon_bild;

    public ParentItem(String kategorie) {
        this.kategorie = kategorie;
        this.icon_bild = null;

    }

    public void setIcon_bild(byte[] drawable){
        this.icon_bild = drawable;
    }
}
