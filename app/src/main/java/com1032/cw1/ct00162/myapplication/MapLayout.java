package com1032.cw1.ct00162.myapplication;
import android.app.Activity;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * ------------------------------------------------------------------------------------
 * @author Charles Tizzard [ct001]
 * @version 1.0
 * @since 25/05/2015
 *
 * MapLayout.class is an Activity which holds a map fragment for the user to get a more
 * detailed view of locations around them.
 * ------------------------------------------------------------------------------------
 * */


public class MapLayout extends Activity implements  OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback, com.google.android.gms.location.LocationListener {



    private GoogleMap map;
    private static int USER_RADIUS = 5;
    static CameraPosition USER = null;
    static String latitude;
    static String longitude;
    static Button menu;
    static Button add;

    private ListDB db;

    private Context c;
    Marker lastOpened = null;


    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     /*
     * ------------------------------------------------------------------------------------
     *   Here we initialise all the variables for this Activity
     * ------------------------------------------------------------------------------------
     * */

        c = this;
        setContentView(R.layout.map_layout);

       MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mf.getMapAsync(this);

        GPSTracker gpsTracker = new GPSTracker(this);

        db = ListDB.initialize(this);

        menu = (Button)findViewById(R.id.menu);
        add = (Button)findViewById(R.id.plus);


        if (gpsTracker.getIsGPSTrackingEnabled()) {
            latitude = String.valueOf(gpsTracker.latitude);
            longitude = String.valueOf(gpsTracker.longitude);

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

        /*
        * ------------------------------------------------------------------------------------
        * Set the list adapter for the navigation Drawer
        * ------------------------------------------------------------------------------------
        * */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new NavDrawerListAdapter(this, R.layout.drawer_list_item, list));

        /*
        * ------------------------------------------------------------------------------------
        * Set the on click listener for the menu button, add button and navigation drawer items.
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



    @Override
    public void onMapLoaded() {

    }

    /*
    * ------------------------------------------------------------------------------------
    * When the map is ready we go to the users location and set a number of other attributes.
    * We set a new onMarkerClickListener for the map so that clicking on markers does
    * not move the map around.
    * We then load the locations onto the map if they are within the 5 mile radius.
    * ------------------------------------------------------------------------------------
    * */

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        this.map.setOnMapLoadedCallback(this);
        this.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.map.setMyLocationEnabled(true);
        this.map.getUiSettings().setZoomControlsEnabled(false);
        this.map.getUiSettings().setAllGesturesEnabled(false);
        this.map.animateCamera(CameraUpdateFactory.newCameraPosition(USER));
        this.map.setOnMapLoadedCallback(this);


        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                // Check if there is an open info window
                if (lastOpened != null) {
                    // Close the info window
                    lastOpened.hideInfoWindow();

                    // Is the marker the same marker that was already open
                    if (lastOpened.equals(marker)) {
                        // Nullify the lastOpened object
                        lastOpened = null;
                        // Return so that the info window isn't opened again
                        return true;
                    }
                }

                // Open the info window for the marker
                marker.showInfoWindow();
                // Re-assign the last opened such that we can close it later
                lastOpened = marker;

                // Event was handled by our code do not launch default behaviour.
                return true;
            }
        });

        List<Attraction> locationList = db.printTable();

        for(Attraction a : locationList) {
            if (a.distance <= USER_RADIUS) {

                Log.i("DISTANCE", String.valueOf(a.distance));
                this.map.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.valueOf(a.latitude), Double.valueOf(a.longitude)))
                        .title(a.name)
                        .snippet(a.type));
            }
        }


    }

    /*
    * ------------------------------------------------------------------------------------
    * Here we override the onLocationChanged() method and set it up so that when are
    * location changes we re-load the markers into the map based on the new gps co-ordinates.
    * ------------------------------------------------------------------------------------
    * */

    @Override
    public void onLocationChanged(Location location) {

        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());

        List<Attraction> locationList = db.printTable();
        Log.i("DATA","Name: "+locationList.get(1).name+" LatLong: "+locationList.get(1).latitude+","+locationList.get(1).longitude+" Distance: "+locationList.get(1).distance);
        for(Attraction a : locationList) {
            db.updateDistance(distance(Double.valueOf(a.latitude), Double.valueOf(a.longitude), Double.valueOf(latitude), Double.valueOf(longitude), 'M'), a._id);
        }
        locationList = db.printTable();


        for(Attraction a : locationList){
            db.updateDistance(distance(Double.valueOf(a.latitude), Double.valueOf(a.longitude), Double.valueOf(latitude), Double.valueOf(longitude), 'M'), a._id);
            if(a.distance <= USER_RADIUS) {
                Log.i("DISTANCE", String.valueOf(a.distance));
                this.map.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.valueOf(a.latitude), Double.valueOf(a.longitude)))
                        .title(a.name)
                        .snippet(a.type));
            }
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




