package com.sb.arapp.ar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleecus on 1/12/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "RenyooLoc";

    // Contacts table name
    private static final String TABLE_LOCATION = "Location";
    private static final String TABLE_USERACTIVITY = "UserActivity";
    private static final String TABLE_MQTT = "MqttTable";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LON = "longitude";
    private static final String KEY_ADD = "address";
    private static final String KEY_DATETIME = "datetime";
    private static final String KEY_FROM_NETWORK = "fromnetwork";
    private static final String KEY_INTERNET_STATUS = "internetstatus";
    private static final String KEY_MQTT_REQ = "mqttrequest";

    private static final String KEY_MQTT_RES = "mqttresponse";

    // Contacts Table Columns names
    private static final String KEY_USERACTIVITY_ID = "id";
    private static final String KEY_CONFIDENCE = "confidence";
    private static final String KEY_RECOGNIZEACTIVITY = "recognizeactivity";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATION + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_LAT + " TEXT,"
                + KEY_LON + " TEXT,"
                + KEY_ADD + " TEXT,"
                + KEY_DATETIME + " TEXT,"
                + KEY_FROM_NETWORK + " TEXT,"
                + KEY_INTERNET_STATUS + " TEXT,"
                + KEY_MQTT_REQ + " TEXT,"
                + KEY_MQTT_RES+ " TEXT" + ")";


        String CREATE_USERACTIVITY_TABLE = "CREATE TABLE " + TABLE_USERACTIVITY + "("
                + KEY_USERACTIVITY_ID + " INTEGER PRIMARY KEY," + KEY_CONFIDENCE + " TEXT,"
                + KEY_RECOGNIZEACTIVITY + " TEXT" + ")";

        String CREATE_MQTT_TABLE = "CREATE TABLE " + TABLE_MQTT + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_LAT + " TEXT,"
                + KEY_LON + " TEXT,"
                + KEY_DATETIME + " TEXT,"
                + KEY_MQTT_REQ + " TEXT,"
                + KEY_MQTT_RES+ " TEXT" + ")";

        db.execSQL(CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_USERACTIVITY_TABLE);
        db.execSQL(CREATE_MQTT_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERACTIVITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MQTT);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new location
    public void addLocation(LocationModel contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
       // values.put(KEY_ID, contact.getID());
        values.put(KEY_LAT, contact.getLatitude()); // Contact Name
        values.put(KEY_LON, contact.getLongitude()); // Contact Phone
        values.put(KEY_ADD, contact.getAddress()); // Contact Phone
        values.put(KEY_DATETIME, contact.getDateTime()); // Contact Phone
        values.put(KEY_FROM_NETWORK, contact.getLocTakenFrom()); // Contact Phone
        values.put(KEY_INTERNET_STATUS, contact.getInternetStatus()); // Contact Phone
        //values.put(KEY_MQTT_REQ, contact.getMqttReq());
        //values.put(KEY_MQTT_RES, contact.getMqttRes());

        // Inserting Row
        db.insert(TABLE_LOCATION, null, values);

        db.close(); // Closing database connection
    }




    // Getting single location
    LocationModel getLocation(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LOCATION, new String[] { KEY_ID,
                        KEY_LAT, KEY_LON, KEY_ADD, KEY_DATETIME, KEY_FROM_NETWORK, KEY_INTERNET_STATUS }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        LocationModel contact = new LocationModel(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2)
                , cursor.getString(3), cursor.getString(4)
                , cursor.getString(5), cursor.getString(6));
        // return contact
        return contact;
    }

    // Getting All Location
    public List<LocationModel> getAllLocations() {
        List<LocationModel> contactList = new ArrayList<LocationModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATION;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LocationModel locationModel = new LocationModel();
                locationModel.setID(Integer.parseInt(cursor.getString(0)));
                locationModel.setLatitude(cursor.getString(1));
                locationModel.setLongitude(cursor.getString(2));
                locationModel.setAddress(cursor.getString(3));
                locationModel.setDateTime(cursor.getString(4));
                locationModel.setLocTakenFrom(cursor.getString(5));
                locationModel.setInternetStatus(cursor.getString(6));
                //locationModel.setMqttReq(cursor.getString(7));
                //locationModel.setMqttRes(cursor.getString(8));
                // Adding contact to list
                contactList.add(locationModel);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }



    // Deleting all locations
    public String getLastLocation() {
        String lastLocation = null;
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATION;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToLast()){
            lastLocation=cursor.getString(3);
        }
        return lastLocation;
    }

    // Deleting all locations
    public LocationModel getLastLocation1() {
        LocationModel locationModel = null;
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATION;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToLast()){
            locationModel = new LocationModel();
            locationModel.setID(Integer.parseInt(cursor.getString(0)));
            locationModel.setLatitude(cursor.getString(1));
            locationModel.setLongitude(cursor.getString(2));
            locationModel.setAddress(cursor.getString(3));
            locationModel.setDateTime(cursor.getString(4));
            locationModel.setLocTakenFrom(cursor.getString(5));
            locationModel.setInternetStatus(cursor.getString(6));
           // locationModel.setMqttReq(cursor.getString(7));
            //locationModel.setMqttRes(cursor.getString(8));
        }
        return locationModel;
    }



    // Deleting all locations
    public void deleteAllLocations() {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "delete from "+TABLE_LOCATION;
        db.execSQL(deleteQuery);
        db.close();
    }


    // Deleting all user activity
    public void deleteAllUserActivity() {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "delete from "+TABLE_USERACTIVITY;
        db.execSQL(deleteQuery);
        db.close();
    }

    // Deleting all locations
    public void deleteAllmqttloc() {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "delete from "+TABLE_MQTT;
        db.execSQL(deleteQuery);
        db.close();
    }


    // Updating single contact
    public int updateLocation(LocationModel contact) {
        SQLiteDatabase db = this.getWritableDatabase();

      //  Log.e("VF","uPDATE KEY: "+contact.getID()+" "+contact.getMqttRes());

        ContentValues values = new ContentValues();
  /*      values.put(KEY_LAT, contact.getLatitude());
        values.put(KEY_LON, contact.getLongitude());
        values.put(KEY_ADD, contact.getAddress());
        values.put(KEY_FROM_NETWORK, contact.getLocTakenFrom());
        values.put(KEY_INTERNET_STATUS, contact.getInternetStatus());
        values.put(KEY_MQTT_REQ, contact.getMqttReq());*/

       // values.put(KEY_MQTT_RES, contact.getMqttRes());


        // updating row
        //return db.update(TABLE_LOCATION, values, KEY_ID + " = ?",
             //   new String[] { String.valueOf(contact.getID()) });


        Log.e("VF","UPDATE RETURN: "+db.update(TABLE_LOCATION, values, KEY_ID + " =?",
                null));

        //return db.update(TABLE_LOCATION, values, KEY_ID + " =?",
           //     new String[] { String.valueOf(contact.getID()) });


        return db.update(TABLE_LOCATION, values, KEY_ID + " =?",
                null);


      /*  "db.update(TABLE_NAME, values, C_ID +"=?"+id, null);"
        the second and third parameters are wrong.
                 "String[] whereArgs = {String.valueOf(id)};

                 db.update(TABLE_NAME, values, C_ID +"=?", whereArgs );*/
    }

    // Deleting single contact
    /*public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }*/


    // Getting contacts Count
    /*public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }*/
}