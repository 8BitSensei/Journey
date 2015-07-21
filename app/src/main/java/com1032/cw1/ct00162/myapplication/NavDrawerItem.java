package com1032.cw1.ct00162.myapplication;

/**
 * ------------------------------------------------------------------------------------
 * @author Charles Tizzard [ct001]
 * @version 1.0
 * @since 25/05/2015
 *
 * NavDrawerItem.class is a very simple object used to hold data for each option in
 * the navigation drawer.
 * ------------------------------------------------------------------------------------
 * */

public class NavDrawerItem {

    public int icon;
    public String name;

    // Constructor.
    public NavDrawerItem(int icon, String name) {

        this.icon = icon;
        this.name = name;
    }
}