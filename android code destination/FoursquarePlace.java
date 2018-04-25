package com.vnr.smartcitytraveller;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by siva on 14/03/2018.
 */

public class FoursquarePlace {
    private String name;
    private String id;
    private LatLng location;
    private double score;
    private String add;
    FoursquarePlace (String name,String id,LatLng location,double score)
    {
        this.name = name;
        this.id = id;
        this.location = location;
        this.score = score;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    double getScore() {
        return score;
    }

    void setScore(double score) {
        this.score = score;
    }

    public String getAdd() {
        return add;
    }
}
