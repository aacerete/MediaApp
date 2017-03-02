package com.example.aacerete.mediaapp;

/**
 * Created by aacerete on 17/02/17.
 */

public class Gallery {
    String name;
    String absolute;
    private double latitude;
    private double longitude;

    public Gallery() {
    }

    public Gallery(String name, String absolute) {
        this.name = name;
        this.absolute = absolute;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
