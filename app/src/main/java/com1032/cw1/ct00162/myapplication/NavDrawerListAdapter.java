package com1032.cw1.ct00162.myapplication;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ------------------------------------------------------------------------------------
 * @author Charles Tizzard [ct001]
 * @version 1.0
 * @since 25/05/2015
 *
 * NavDrawerListAdapter.class takes in an array of NavDrawerItems and converts them into
 * a view in the getView() method, which is then used in the relevant classes for the
 * navDrawer frame.
 * ------------------------------------------------------------------------------------
 * */

class NavDrawerListAdapter extends ArrayAdapter<NavDrawerItem> {

    Context mContext;
    int layoutResourceId;
    NavDrawerItem data[] = null;

    public NavDrawerListAdapter(Context mContext, int layoutResourceId, NavDrawerItem[] data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View rowView = inflater.inflate(R.layout.drawer_list_item, parent, false);

        ImageView imageViewIcon = (ImageView) rowView.findViewById(R.id.icon);
        TextView textViewName = (TextView) rowView.findViewById(R.id.title);

        textViewName.setText(data[position].name);
        //imageViewIcon.set(data[position].icon);

        return rowView;
    }

}