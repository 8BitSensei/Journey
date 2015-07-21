package com1032.cw1.ct00162.myapplication;

/**
 * ------------------------------------------------------------------------------------
 * @author Charles Tizzard [ct001]
 * @version 1.0
 * @since 04/05/2015
 *
 * MobileArrayAdapter.class manages the objects and population of the ListView object
 * in the MainActivity.class.
 * ------------------------------------------------------------------------------------
 * */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MobileArrayAdapter extends ArrayAdapter<String> {

    private final Context context;
    static private List<Attraction> values = new ArrayList<>();

    /*
    * ------------------------------------------------------------------------------------
    * Below we have a parametrised constructor for the class. It takes in a Context 'context',
    * List<String> table (this holds a list of all the available objects to display from the
    * database) and List<Integer> compTable and list along with it telling us what objects
    * we can and can't display.
    *
    * We pass te context and layout for or host class in the super(), we then set the local
    * variables with our passed in ones.
    * ------------------------------------------------------------------------------------
    * */

    public MobileArrayAdapter(Context context, List<Attraction> table) {
        super(context, R.layout.activity_main);
        this.context = context;
        values = table;

    }

    @Override
    public int getCount() {
        return values.size();
    }



    /*
    * ------------------------------------------------------------------------------------
    * Below is the getView() method which will return a View. In this method we have
    * to gather all the data we will want to display on the item in the ListView.
    * So firstly get the Due Date of the current task from the database, we then convert
    * the year, month and day into String variables.
    *
    * After that we set the View, TextViews and CheckBox from the list_mobile.xml layout,
    * we then set all these with appropriate data we have pulled from the database and set
    * an on click listener to the Button 'edit'.
    * This on click listener when activated will use an intent to send us to LocationDetails.class
    * with the Name and ID of this object among other attributes.
    *
    * All this is finally returned as a View.
    * ------------------------------------------------------------------------------------
    * */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View rowView = inflater.inflate(R.layout.list_mobile, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.secondLine);
        TextView typeView = (TextView) rowView.findViewById(R.id.dateLine);
        final Button nav = (Button) rowView.findViewById(R.id.nav);

        textView.setText(values.get(position).name);
        typeView.setText(values.get(position).type);



        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity host = (Activity) nav.getContext();
                Intent intent = new Intent(host, LocationDetails.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("TITLE",values.get(position).name);
                intent.putExtra("TYPE",values.get(position).type);
                intent.putExtra("DESC", values.get(position).description);
                intent.putExtra("LAT", values.get(position).latitude);
                intent.putExtra("LONG", values.get(position).longitude);
                intent.putExtra("USER_LAT", MainActivity.latitude);
                intent.putExtra("USER_LONG", MainActivity.longitude);
                intent.putExtra("DIST", values.get(position).distance);
                intent.putExtra("RATING", values.get(position).rating);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                host.startActivity(intent);
            }
        });

        return rowView;
    }
}