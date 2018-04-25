package com.vnr.smartcitytraveller;

import com.google.android.gms.maps.model.LatLng;

 class Place {
    private String name;
    private String rating= null;
    private LatLng location = null;
    private String icon = null;
    private double distance = 0;
    private String neighbourhood;
     private String description;
     private String id;
     private boolean open;

    Place(String name,String rating,LatLng location,String icon,double distance,String neighbourhood,String des,String id,boolean open)
    {
        this.name = name;
        this.rating = rating;
        this.location = location;
        this.icon = icon;
        this.distance=distance;
        this.neighbourhood = neighbourhood;
        this.description=des;
        this.id = id;
        this.open=open;
    }


     public String getName() {
         return name;
     }

     public void setName(String name) {
         this.name = name;
     }

      String getRating() {
         return rating;
     }

      void setRating(String rating) {
         this.rating = rating;
     }

      LatLng getLocation() {
         return location;
     }

      void setLocation(LatLng location) {
         this.location = location;
     }

      String getIcon() {
         return icon;
     }

     public void setIcon(String icon) {
         this.icon = icon;
     }

      double getDistance() {
         return distance;
     }

      void setDistance(double distance) {
         this.distance = distance;
     }

      String getNeighbourhood() {
         return neighbourhood;
     }

     void setNeighbourhood(String neighbourhood) {
         this.neighbourhood = neighbourhood;
     }

      String getDescription() {
         return description;
     }

     void setDescription(String description) {
         this.description = description;
     }

     public String getId() {
         return id;
     }

     public void setId(String id) {
         this.id = id;
     }

      boolean isOpen() {
         return open;
     }

     void setOpen(boolean open) {
         this.open = open;
     }
 }
