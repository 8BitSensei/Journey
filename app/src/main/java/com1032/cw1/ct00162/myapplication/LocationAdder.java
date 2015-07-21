package com1032.cw1.ct00162.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * ------------------------------------------------------------------------------------
 * @author Charles Tizzard [ct001]
 * @version 1.0
 * @since 25/05/2015
 *
 * LocationAdder.class gives the user an interface to add new locations/attractions
 * to the database.
 * ------------------------------------------------------------------------------------
 * */

 public class LocationAdder extends Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private EditText title;
    private EditText description;
    private Spinner type;
    private Button save;
    private Button menu;
    private Button add;
    private TextView location;
    private Context c;
    private RatingBar rating;
    private float ratingValue = 0;


    @Override
    public  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_location);

        /*
        * ------------------------------------------------------------------------------------
        * Initialising all the elements of the class
        * ------------------------------------------------------------------------------------
        * */

        c = this;

        title = (EditText) findViewById(R.id.title_input);
        description = (EditText) findViewById(R.id.desc_input);
        type = (Spinner) findViewById(R.id.type_spinner);
        save = (Button) findViewById(R.id.save);
        rating = (RatingBar) findViewById(R.id.rating);
        menu = (Button) findViewById(R.id.menu);
        add = (Button) findViewById(R.id.plus);
        location = (TextView) findViewById(R.id.location);

        location.setText(MainActivity.gpsTracker.getLatitude()+", "+MainActivity.gpsTracker.getLongitude());

        Spinner spinner = (Spinner) findViewById(R.id.type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


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
        * Here we set the multiple on click listeners for the rating bar, save button,
        * menu button, add button and the nav drawer options.
        * ------------------------------------------------------------------------------------
        * */

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                ratingValue = ratingBar.getRating();
            }
        });

        /*
        * ------------------------------------------------------------------------------------
        * In the save on click listener we take all the necessary information and pass it
        * into the database creating a new attraction.
        * ------------------------------------------------------------------------------------
        * */

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleInput = title.getText().toString();
                String typeInput = type.getSelectedItem().toString();
                String descInput = description.getText().toString();

                if(!titleInput.equals("") && !typeInput.equals("") && !descInput.equals("")) {
                    Intent i = new Intent(c, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    c.startActivity(i);
                    MainActivity.db.insertData(titleInput, typeInput, String.valueOf(MainActivity.gpsTracker.getLatitude()), String.valueOf(MainActivity.gpsTracker.getLongitude()), 0.0, descInput,ratingValue);
                }
            }
        });


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
    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    }
