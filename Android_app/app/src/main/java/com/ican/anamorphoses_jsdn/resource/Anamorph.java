package com.ican.anamorphoses_jsdn.resource;

import android.graphics.Bitmap;

public class Anamorph {
    private int id;
    private String detectorName;
    private Bitmap image;

    public Anamorph(int id, String detectorName, Bitmap image) {
        this.id = id;
        this.detectorName = detectorName;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getDetectorName() {
        return detectorName;
    }

    public Bitmap getImage() {
        return image;
    }
}
