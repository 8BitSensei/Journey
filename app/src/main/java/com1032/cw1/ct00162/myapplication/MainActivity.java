package com1032.cw1.ct00162.myapplication;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


/**
 * ------------------------------------------------------------------------------------
 * @author Charles Tizzard [ct001]
 * @version 1.0
 * @since 25/05/2015
 *
 * MainActivity.class is the launch activity that the user lands in.
 * It simply contains a list view which is populated with all the locations within a 5
 * mile radius.
 * ------------------------------------------------------------------------------------
 * */

public class MainActivity extends ListActivity implements com.google.android.gms.location.LocationListener {

    private static int USER_RADIUS = 5;
    static CameraPosition USER = null;
    static String latitude;
    static String longitude;
    static Button menu;
    static Button add;
    static ListDB db;
    static GPSTracker gpsTracker;
    private Context c;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        * ------------------------------------------------------------------------------------
        *   Here we initialise all the variables for this Activity
        * ------------------------------------------------------------------------------------
        * */

        c = this;

       gpsTracker = new GPSTracker(this);

        db = ListDB.initialize(this);

        menu = (Button)findViewById(R.id.menu);
        add = (Button)findViewById(R.id.plus);


        if (gpsTracker.getIsGPSTrackingEnabled()) {
            latitude = String.valueOf(gpsTracker.getLatitude());
            longitude = String.valueOf(gpsTracker.getLongitude());

            USER = new CameraPosition.Builder().target(new LatLng(Float.valueOf(latitude), Float.valueOf(longitude)))
                    .zoom(12.5f)
                    .bearing(0)
                    .tilt(25)
                    .build();
        }




        NavDrawerItem[] list = new NavDrawerItem[]{
                new NavDrawerItem(R.drawable.ic_launcher,"List"),
                new NavDrawerItem(R.drawable.ic_launcher,"Map"),
                new NavDrawerItem(R.drawable.ic_launcher,"Settings"),
                new NavDrawerItem(R.drawable.ic_launcher,"Add location")
        };

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new NavDrawerListAdapter(this, R.layout.drawer_list_item, list));

       // db.insertData("Place","Type",latitude,longitude,2.0,"");

        /*
        * ------------------------------------------------------------------------------------
        * Here we set the List adapter and populate it
        * ------------------------------------------------------------------------------------
        * */

        List<Attraction> locationList = db.printTable();
        List<Attraction> localAttractions = new ArrayList<>();

        for(Attraction a : locationList){
            Log.i("NAME",a.name);
            if(a.distance <= USER_RADIUS)
                localAttractions.add(a);

        }

        MobileArrayAdapter ma = new MobileArrayAdapter(this, localAttractions);
        setListAdapter(ma);

        /*
        * ------------------------------------------------------------------------------------
        * Se the on click listener for the menu button, add button and the nav drawer items
        * ------------------------------------------------------------------------------------
        * */

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mDrawerLayout.isDrawerOpen(mDrawerList))
                mDrawerLayout.openDrawer(mDrawerList);
                else
                    mDrawerLayout.closeDrawer(mDrawerList);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(c, LocationAdder.class);
                c.startActivity(i);
            }
        });

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = null;
                switch (i){
                    case 0:
                        intent = new Intent(c, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        c.startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(c, MapLayout.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        c.startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(c, MapLayout.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        c.startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(c, LocationAdder.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        c.startActivity(intent);
                        break;
                    default:
                }
            }
        });
    }

    /*
    * ------------------------------------------------------------------------------------
    * Here we override the onLocationChanged method, now when ever our location changes
    * we update our gps co-ordinates and update our distance from the locations listed.
    * ------------------------------------------------------------------------------------
    * */

    @Override
    public void onLocationChanged(Location location) {

        gpsTracker.updateGPSCoordinates();

        latitude = String.valueOf(gpsTracker.getLatitude());
        longitude = String.valueOf(gpsTracker.getLongitude());

        List<Attraction> locationList = db.printTable();
        List<Attraction> localAttractions = new ArrayList<>();

        for(Attraction a : locationList){
            Log.i("NAME",a.name);
            if(a.distance <= USER_RADIUS)
                localAttractions.add(a);
        }

        MobileArrayAdapter ma = new MobileArrayAdapter(this, localAttractions);
        setListAdapter(ma);

        for(Attraction a : locationList) {
            db.updateDistance(distance(Double.valueOf(a.latitude), Double.valueOf(a.longitude), Double.valueOf(latitude), Double.valueOf(longitude), 'M'), a._id);
        }

        Log.i("LOCATION CHANGED","Name: "+locationList.get(1).name+" LatLong: "+locationList.get(1).latitude+","+locationList.get(1).longitude+" Distance: "+locationList.get(1).distance);

    }


    /*
    * ------------------------------------------------------------------------------------
    * onResume we update our gps co-ordinates and the distance from locations in the database.
    * ------------------------------------------------------------------------------------
    * */
    @Override
    public void onResume(){
        super.onResume();
        gpsTracker.updateGPSCoordinates();

        latitude = String.valueOf(gpsTracker.getLatitude());
        longitude = String.valueOf(gpsTracker.getLongitude());

        List<Attraction> locationList = db.printTable();
        List<Attraction> localAttractions = new ArrayList<>();

        for(Attraction a : locationList){
            Log.i("NAME",a.name);
            if(a.distance <= USER_RADIUS)
                localAttractions.add(a);
        }

        MobileArrayAdapter ma = new MobileArrayAdapter(this, localAttractions);
        setListAdapter(ma);

        for(Attraction a : locationList) {
            db.updateDistance(distance(Double.valueOf(a.latitude), Double.valueOf(a.longitude), Double.valueOf(latitude), Double.valueOf(longitude), 'M'), a._id);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

     /*
    ------------------------------------------------------------------------------------
    * Here we use a method to calculate the distance from in miles from the location.
    *
    * src: http://www.geodatasource.com/developers/java
    * ------------------------------------------------------------------------------------
    * */

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }


    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        Log.i("DISTANCE ", ""+dist);
        dist = Math.acos(dist);
        Log.i("DISTANCE ", ""+dist);
        dist = rad2deg(dist);
        Log.i("DISTANCE ", ""+dist);
        dist = dist * 60 * 1.1515;
        Log.i("DISTANCE ", ""+dist);
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        Log.i("DISTANCE RETURN", ""+dist);
        return (dist);
    }


}




