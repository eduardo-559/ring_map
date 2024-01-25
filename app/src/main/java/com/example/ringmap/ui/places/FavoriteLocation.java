package com.example.ringmap.ui.places;

import com.google.firebase.firestore.GeoPoint;

public class FavoriteLocation {
    private String locationName;
    private GeoPoint locationPoint;
    private int radius;
    private String Id;
    // Construtores, getters e setters

    public FavoriteLocation() {
        // Construtor padrão necessário para Firebase
    }

    public FavoriteLocation(String locationName, GeoPoint locationPoint, int radius) {
        this.locationName = locationName;
        this.locationPoint = locationPoint;
        this.radius = radius;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public GeoPoint getLocationPoint() {
        return locationPoint;
    }

    public void setLocationPoint(GeoPoint locationPoint) {
        this.locationPoint = locationPoint;
    }
    public int getRadius() {return radius;}

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getId() {return Id;}

    public void setId(String Id) {
        this.Id = Id;
    }


}
