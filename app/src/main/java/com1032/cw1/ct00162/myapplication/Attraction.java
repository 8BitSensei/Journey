package com1032.cw1.ct00162.myapplication;

/**
 * ------------------------------------------------------------------------------------
 * @author Charles Tizzard [ct001]
 * @version 1.0
 * @since 25/05/2015
 *
 * Attraction.class is a basic object here to simply hold data about each attraction
 * and easily pass this information along.
 * ------------------------------------------------------------------------------------
 * */

 public class Attraction {

    public int _id;
    public String name;
    public String type;
    public String latitude;
    public String longitude;
    public int rating;
    public double distance;
    public String description;


    public Attraction(int _id, String name, String type, String latitude, String longitude, double distance, int rating, String description){
        this._id = _id;
        this.name = name;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.rating = rating;
        this.description = description;
    }
}
