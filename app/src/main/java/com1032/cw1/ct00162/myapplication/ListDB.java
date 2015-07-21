package com1032.cw1.ct00162.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * ------------------------------------------------------------------------------------
 * @author Charles Tizzard [ct001]
 * @version 1.0
 * @since 04/05/2015
 *
 * ListDB.class is the database management class, here we have all the methods used
 * throughout the application that allow us to interface with the local SQLite
 * database.
 * ------------------------------------------------------------------------------------
 * */

public class ListDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="todo";
    private static final  int DATABASE_VERSION = 1;
    private static final String TABLE_TODO = "todos";

    private SQLiteDatabase db = this.getWritableDatabase();
    private static ListDB mInstance = null;

    private List<Integer> syncArray = null;

    private static final String CREATE_TABLE_TODO =   "CREATE TABLE IF NOT EXISTS " + TABLE_TODO + "(" + "_id INTEGER PRIMARY KEY" + " AUTOINCREMENT, " +
            "Name TEXT, " + "Type TEXT, Lat TEXT, Long TEXT, Rating INT(255), Distance INT(255), Description TEXT);";

    /*
    * ------------------------------------------------------------------------------------
    * To avoid having multiple instances open, the LitDB.class is a singleton object,
    * to create it you need to call the initialize method below, which will check if there
    * is an instance of it still active. If there is it will return that instance; if not
    * it will call the LitDB constructor passing through the Context 'c'.
    * This will call the super() and then the overridden onCreate method, passing through
    * it's self as a writable database. If the database doesn't already exist it will
    * create it a new.
    * ------------------------------------------------------------------------------------
    * */

    public static ListDB initialize(Context c){

        if (mInstance == null) {
            mInstance = new ListDB(c.getApplicationContext());
        }
        return mInstance;
    }

    ListDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        onCreate(this.getWritableDatabase());
        syncArray = syncId();
    }

    @Override
    public void onCreate(SQLiteDatabase dbList) {
        dbList.execSQL(CREATE_TABLE_TODO);
    }

    /*
    * ------------------------------------------------------------------------------------
    * The below method getSyncId() will take the String 'content' as the `Name` of the desired
    * object and return it's `_id`.
    * ------------------------------------------------------------------------------------
    * */
    public int getSyncId(String content){

       // SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT _id FROM "+TABLE_TODO+" WHERE `Name` = '"+content+"'";
        Cursor cursor = db.rawQuery(sqlQuery, null );
        cursor.moveToFirst();

        return cursor.getInt(0);
    }

    /*
    * ------------------------------------------------------------------------------------
    * the below method getDate() will take the int 'id' as the `_id` of the desired object
    * and return it's `DueDate` in a String form.
    * ------------------------------------------------------------------------------------
    * */
    public Double[] getLatLong(int id){
        //SQLiteDatabase db = this.getReadableDatabase();
        String sqlQueryLat = "SELECT Lat FROM "+TABLE_TODO+" WHERE `_id` = '"+id+"'";
        String sqlQueryLong = "SELECT Long FROM "+TABLE_TODO+" WHERE `_id` = '"+id+"'";

        Cursor cursor = db.rawQuery(sqlQueryLat, null );
        cursor.moveToFirst();
        Double[] latLong = new Double[2];
        latLong[0]=Double.valueOf(cursor.getString(0));

        cursor = db.rawQuery(sqlQueryLong, null );
        cursor.moveToFirst();
        latLong[1]=Double.valueOf(cursor.getString(0));

        return latLong;
    }

    public int getId(String name){
       // SQLiteDatabase db = this.getReadableDatabase();
        String sqlQueryId = "SELECT _id FROM "+TABLE_TODO+" WHERE `Name` = '"+name+"'";
        Cursor cursor = db.rawQuery(sqlQueryId, null );
        cursor.moveToFirst();

        return cursor.getInt(0);
    }

    public String getName(int id){
        //SQLiteDatabase db = this.getReadableDatabase();
        String sqlQueryId = "SELECT Name FROM "+TABLE_TODO+" WHERE `_id` = '"+id+"'";
        Cursor cursor = db.rawQuery(sqlQueryId, null );
        cursor.moveToFirst();

        return cursor.getString(0);
    }

    public int getRating(int id){
        //SQLiteDatabase db = this.getReadableDatabase();
        String sqlQueryId = "SELECT Rating FROM "+TABLE_TODO+" WHERE `_id` = '"+id+"'";
        Cursor cursor = db.rawQuery(sqlQueryId, null );
        cursor.moveToFirst();

        return cursor.getInt(0);
    }

    /*
    * ------------------------------------------------------------------------------------
    * The below onUpgrade() method will drop the current Database and create a new version
    * of it.
    * ------------------------------------------------------------------------------------
    * */
    public void onUpgrade(SQLiteDatabase dbList, int
            oldVersion, int newVersion) {
        String dropSQL = "DROP TABLE IF EXISTS " + TABLE_TODO+";";
        //db.execSQL(dropSQL);
        db.execSQL(CREATE_TABLE_TODO);
        Log.d("in onUpgrade CountryDB", "onUpgrade");
    }

    /*
    * ------------------------------------------------------------------------------------
    * The below method isTableExists() it will check whether a table exists and then
    * return a boolean based on the results.
    * ------------------------------------------------------------------------------------
    * */
    public boolean isTableExists(SQLiteDatabase dbList, String tableName) {

        Cursor cursor = dbList.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }


    /*
    * ------------------------------------------------------------------------------------
    * The below method update name takes a String as the new name and an int 'id' to find
    * the desired object, it then updates it with the new name. The method below that
    * updateDate() does the same but for the `DueDate` of the object in question.
    * ------------------------------------------------------------------------------------
    * */
    public void updateName(String name,int id){
       // SQLiteDatabase db =
         //       this.getWritableDatabase();
        String dbQuery = "UPDATE "+TABLE_TODO+" SET Name='"+name+"' WHERE _id ="+id;
        db.execSQL(dbQuery);
    }
    public void updateDistance(double distance, int id){
       // SQLiteDatabase db =
         //       this.getWritableDatabase();
        String dbQuery = "UPDATE "+TABLE_TODO+" SET distance ='"+distance+"' WHERE _id ="+id;
        db.execSQL(dbQuery);
    }

    public void updateRating(float rating, int id){
        //SQLiteDatabase db =
          //      this.getWritableDatabase();
        String dbQuery = "UPDATE "+TABLE_TODO+" SET Rating ='"+rating+"' WHERE _id ="+id;
        db.execSQL(dbQuery);
    }


    /*
    * ------------------------------------------------------------------------------------
    * The below method clearRecord() takes an int 'id' to find a specific object in the
    * database and then deletes it.
    * ------------------------------------------------------------------------------------
    * */
    public void clearRecord(int id){
        //SQLiteDatabase db = this.getWritableDatabase();
        String dropRecord = "DELETE FROM " + TABLE_TODO +
                " WHERE _id = "+id+";";
        db.execSQL(dropRecord);
        Log.i("SQL", dropRecord);
    }

    /*
    * ------------------------------------------------------------------------------------
    * The below method hideRecord() will set the desired objects `Deleted` column to 1,
    * this does not actually delete the record but hides it from the ListView and marks it
    * for later deletion.
    * ------------------------------------------------------------------------------------
    * */
    public void hideRecord(int id){
        //SQLiteDatabase db = this.getWritableDatabase();
        String setHiddden = "UPDATE "+TABLE_TODO+" SET Deleted = '1' WHERE _id = "+id;
        db.execSQL(setHiddden);
    }

    /*
    * ------------------------------------------------------------------------------------
    * The below method valueCount() will return the number of items with a given name.
    * It is used for checking whether an item already exists.
    * ------------------------------------------------------------------------------------
    * */
    public int valueCount(String name) {
        Cursor cursor = null;
        try {
           // db = this.getReadableDatabase();
            String sqlQuery = "SELECT * FROM "+TABLE_TODO+" WHERE `Name` = '"+name+"'";
            cursor = this.getReadableDatabase().rawQuery(sqlQuery, null );
            cursor.moveToFirst();

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }

        }
    }

 /*
    * ------------------------------------------------------------------------------------
    * The below method insertData() wil take a Sting 'name' and inserts a new 'to do' object
    * into the local database with 'name' as `Name`, it calculates the Date and Time in the
    * proper format for thr database and sets the current date as the `DueDate` automatically,
    * it also sets `Completed` and `Deleted` to 0 as default.
    * ------------------------------------------------------------------------------------
    * */

    public long insertData(String name, String type, String latitude, String longitude, Double distance, String description, float rating){

       // SQLiteDatabase db = this.getWritableDatabase();
        long todo_id;

        String sqlQuery = "SELECT * FROM "+TABLE_TODO+" WHERE `Name` = '"+name+"'";
        Cursor cursor = this.getReadableDatabase().rawQuery(sqlQuery, null );
        cursor.moveToFirst();

        if(rating > 5)
            rating = 5;
        else if (rating < 0)
            rating = 0;

        ContentValues values = new ContentValues();

        values.put("Name", name);
        values.put("Type", type);
        values.put("Lat", latitude);
        values.put("Long", longitude);
        values.put("Distance", distance);
        values.put("Rating", rating);
        values.put("Description", description);

        if (valueCount(name) == 0) {
            todo_id = db.insert(TABLE_TODO, null, values);
            cursor.close();
            return todo_id;
        }
        else return 0;

    }

    /*
    * ------------------------------------------------------------------------------------
    * The below methods printTable() and printTable() completed will return a List of all
    * the names in the local database and secondly return a List containing there completion
    * status, these two methods are always used in unison.
    *------------------------------------------------------------------------------------
    * */
    public List printTable(){
        Cursor crs = this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_TODO+" ORDER BY Distance ASC Limit 100", null);
        List<Attraction> array1 = new ArrayList<>();
        int i = 0;

        while(crs.moveToNext()){

            int _id = crs.getInt(crs.getColumnIndex("_id"));
            String name = crs.getString(crs.getColumnIndex("Name"));
            String type = crs.getString(crs.getColumnIndex("Type"));
            String latitude = crs.getString(crs.getColumnIndex("Lat"));
            String longitude = crs.getString(crs.getColumnIndex("Long"));
            int rating = crs.getInt(crs.getColumnIndex("Rating"));
            double distance = crs.getDouble(crs.getColumnIndex("Distance"));
            String description = crs.getString(crs.getColumnIndex("Description"));

            Attraction lc = new Attraction(_id,name,type, latitude, longitude, distance, rating, description);
            array1.add(lc);
            i++;
        }
        return array1;
    }
    /*
    * ------------------------------------------------------------------------------------
    * the method syncId() will return a Lst<Integer> containing all _id's, this is used
    * for internal synchronisation tasks.
    * ------------------------------------------------------------------------------------
    * */
    private List<Integer> syncId()
    {
        SQLiteDatabase myDataBase = this.getReadableDatabase();
        String searchQuery = "SELECT _id FROM " + TABLE_TODO;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null );
        cursor.moveToFirst();
        List<Integer> x  = new ArrayList<>();


        for(int i = 1; i < cursor.getColumnCount(); i++){
            x.add(cursor.getInt(i));
        }

        return x;
    }

    /*
    * ------------------------------------------------------------------------------------
    * The below method getResults() returns all items in the local database in the form of
    * a List<JSONObject>, this is used for synchronisation with the external database.
    * ------------------------------------------------------------------------------------
    * */
    private List getResults(SQLiteDatabase dbList)
    {

        String searchQuery = "SELECT  * FROM " + TABLE_TODO;
        Cursor cursor = dbList.rawQuery(searchQuery, null);

        List<JSONObject> resultSet = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                            Log.d("TAG_NAME", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        Log.d("TAG_NAME", e.getMessage()  );
                    }
                }
            }
            resultSet.add(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("TAG_NAME", resultSet.toString() );
        return resultSet;
    }



}
