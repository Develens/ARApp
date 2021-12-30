package com.sb.arapp.ar.database;

/**
 * Created by gleecus on 1/12/17.
 */

public class LocationModel {

    //private variables
    int _id;
    String locationLat;
    String locationLon;
    String locationAddress;
    String dateTime;
    String locationTakenFrom;//Cell, Wifi, GPS
    String internetStatus;
    //String mqttReq;
   // String mqttRes;

    // Empty constructor
    public LocationModel(){

    }
    // constructor
    public LocationModel(int id, String locationLat, String locationLon, String locationAddress,
                         String dateTime, String locationTakenFrom, String internetStatus)
                        {
        this._id = id;
        this.locationLat = locationLat;
        this.locationLon = locationLon;
        this.locationAddress = locationAddress;
        this.dateTime = dateTime;
        this.locationTakenFrom = locationTakenFrom;
        this.internetStatus = internetStatus;
        //this.mqttReq = mqttReq;
        //this.mqttRes = mqttRes;
    }
    //   this.mqttReq = mqttReq;
      //  this.mqttRes = mqttRes;

    // constructor
    public LocationModel(String locationLat, String locationLon, String locationAddress
            , String dateTime, String locationTakenFrom, String internetStatus){
                      //   String mqttReq, String mqttRes){
        this.locationLat = locationLat;
        this.locationLon = locationLon;
        this.locationAddress = locationAddress;
        this.dateTime = dateTime;
        this.locationTakenFrom = locationTakenFrom;
        this.internetStatus = internetStatus;
        //this.mqttReq = mqttReq;
       // this.mqttRes = mqttRes;
    }
    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting latitude
    public String getLatitude(){
        return this.locationLat;
    }

    // setting latitude
    public void setLatitude(String locationLat){
        this.locationLat = locationLat;
    }

    // getting longitude
    public String getLongitude(){
        return this.locationLon;
    }

    // setting longitude
    public void setLongitude(String locationLon){
        this.locationLon = locationLon;
    }

    // getting address
    public String getAddress(){
        return this.locationAddress;
    }

    // setting date time
    public void setDateTime(String locationAddress){
        this.dateTime = dateTime;
    }

    // getting date time
    public String getDateTime(){
        return this.dateTime;
    }

    // setting address
    public void setAddress(String locationAddress){
        this.locationAddress = locationAddress;
    }


    // getting loc from
    public String getLocTakenFrom(){
        return this.locationTakenFrom;
    }

    // setting loc from
    public void setLocTakenFrom(String locationTakenFrom){
        this.locationTakenFrom = locationTakenFrom;
    }

    // getting internet status
    public String getInternetStatus(){
        return this.internetStatus;
    }

    // setting internet status
    public void setInternetStatus(String internetStatus){
        this.internetStatus = internetStatus;
    }

  /*  public String getMqttReq() {
        return mqttReq;
    }

    public void setMqttReq(String mqttReq) {
        this.mqttReq = mqttReq;
    }

    public String getMqttRes() {
        return mqttRes;
    }

    public void setMqttRes(String mqttRes) {
        this.mqttRes = mqttRes;
    }*/
}
