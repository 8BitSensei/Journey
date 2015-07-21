package com1032.cw1.ct00162.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.widget.RatingBar.OnRatingBarChangeListener;

/**
 * ------------------------------------------------------------------------------------
 * @author Charles Tizzard [ct001]
 * @version 1.0
 * @since 25/05/2015
 *
 * LocationDetails.class handles the activity giving the user more information about
 * locations, including there distance from current location and direction on a compass.
 * ------------------------------------------------------------------------------------
 * */

 public class LocationDetails extends Activity implements SensorEventListener, com.google.android.gms.location.LocationListener{

    TextView title;
    TextView type;
    TextView desc;
    TextView dist;
    Button add;
    RatingBar rating;
    static Button menu;
    ImageView compass;
    String latitude;
    String longitude;
    private SensorManager sensorManager;
    private float currentDegree = 0f;
    Location userLocation;
    Location targetLocation;
    GeomagneticField geoField;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private double distance;
    private Intent i;
    private Context c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_layout);

        /**
         * ------------------------------------------------------------------------------------
         * Initialising all the variables of the class.
         * ------------------------------------------------------------------------------------
         * */

        c = this;
        i = getIntent();

        title = (TextView)findViewById(R.id.title);
        type = (TextView)findViewById(R.id.type);
        desc = (TextView)findViewById(R.id.description);
        dist = (TextView)findViewById(R.id.distance);
        compass = (ImageView)findViewById(R.id.compass);
        menu = (Button)findViewById(R.id.menu);
        add = (Button) findViewById(R.id.plus);
        rating=(RatingBar)findViewById(R.id.rating);

        distance = i.getDoubleExtra("DIST", 0.0);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        title.setText(i.getStringExtra("TITLE"));
        type.setText(i.getStringExtra("TYPE"));
        desc.setText(i.getStringExtra("DESC"));
        dist.setText(String.format("%.1f", distance));
        latitude = i.getStringExtra("LAT");
        longitude = i.getStringExtra("LONG");

        Log.i("RATING",""+MainActivity.db.getRating(MainActivity.db.getId(i.getStringExtra("TITLE"))));
        rating.setRating(MainActivity.db.getRating(MainActivity.db.getId(i.getStringExtra("TITLE"))));


        userLocation = new Location("");//provider name is unecessary
        userLocation.setLatitude(Double.valueOf(i.getStringExtra("USER_LAT")));//your coords of course
        userLocation.setLongitude(Double.valueOf(i.getStringExtra("USER_LONG")));

        targetLocation = new Location("");//provider name is unecessary
        targetLocation.setLatitude(Double.valueOf(latitude));//your coords of course
        targetLocation.setLongitude(Double.valueOf(longitude));
        Log.i("TARGET",latitude+","+longitude);
        Log.i("USER",i.getStringExtra("USER_LAT")+","+i.getStringExtra("USER_LONG"));

        geoField = new GeomagneticField(
                Double.valueOf(targetLocation.getLatitude()).floatValue(),
                Double.valueOf(targetLocation.getLongitude()).floatValue(),
                Double.valueOf(0).floatValue(),
                System.currentTimeMillis()
        );

        NavDrawerItem[] list = new NavDrawerItem[]{
                new NavDrawerItem(R.drawable.ic_launcher,"List"),
                new NavDrawerItem(R.drawable.ic_launcher,"Map"),
                new NavDrawerItem(R.drawable.ic_launcher,"Settings"),
                new NavDrawerItem(R.drawable.ic_launcher,"Add location")
        };

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new NavDrawerListAdapter(this, R.layout.drawer_list_item, list));

        /*
        * ------------------------------------------------------------------------------------
        * Setting all the on click listeners for the menu button, add button, nav drawer items
        * and the rating bar.
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
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
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

        /*
        * ------------------------------------------------------------------------------------
        * When the rating in the rating bar has been changed this automatically updates the
        * rating for this item in the database.
        * ------------------------------------------------------------------------------------
        * */

        rating.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
              MainActivity.db.updateRating(ratingBar.getRating(),
                      MainActivity.db.getId(i.getStringExtra("TITLE")));
                Log.i("RATING",""+MainActivity.db.getRating(MainActivity.db.getId(i.getStringExtra("TITLE"))));
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        sensorManager.unregisterListener(this);
    }

    /*
    * ------------------------------------------------------------------------------------
    * Below we override the method onSensorChanged, we use the co-ordinates of the location
    * to calculate it's bearing from true north then add this on to the compass bearing.
    * ------------------------------------------------------------------------------------
    * */
    @Override
    public void onSensorChanged(SensorEvent event) {

        float myBearing = userLocation.bearingTo(targetLocation);
        float degree = Math.round(event.values[0]);

        degree += geoField.getDeclination();
        degree = (myBearing - degree) * -1;
        degree = normalizeDegree(degree);
        rotateArrow(degree);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /*
    * ------------------------------------------------------------------------------------
    * Here we normalize are calculations to a degree value that is useful to us.
    * ------------------------------------------------------------------------------------
    * */

    private float normalizeDegree(float value) {
        if (value >= 0.0f && value <= 180.0f) {
            return value;
        } else {
            return 180 + (180 + value);
        }
    }

    /*
    * ------------------------------------------------------------------------------------
    * This method is used to automatically rotate are compass image view so that it
    * points towards the degree we have calculated.
    * ------------------------------------------------------------------------------------
    * */

    private void rotateArrow(float angle){

        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -angle,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        ra.setDuration(210);

        ra.setFillAfter(true);

        compass.startAnimation(ra);
        currentDegree = -angle;
    }
    /*
    * ------------------------------------------------------------------------------------
    * Here we override the method onLocationChanged so that we update our distance
    * form the location when it is triggered.
    * ------------------------------------------------------------------------------------
    * */
    @Override
    public void onLocationChanged(Location location) {
        MainActivity.gpsTracker.updateGPSCoordinates();

        latitude = String.valueOf(MainActivity.gpsTracker.getLatitude());
        longitude = String.valueOf(MainActivity.gpsTracker.getLongitude());

        List<Attraction> locationList =   MainActivity.db.printTable();


        for(Attraction a : locationList) {
            MainActivity.db.updateDistance(distance(Double.valueOf(a.latitude), Double.valueOf(a.longitude), Double.valueOf(latitude), Double.valueOf(longitude), 'M'), a._id);
            if(a.name.equals(i.getStringExtra("TITLE"))){
                distance = distance(Double.valueOf(a.latitude), Double.valueOf(a.longitude), Double.valueOf(latitude), Double.valueOf(longitude), 'M');
            }
        }



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
