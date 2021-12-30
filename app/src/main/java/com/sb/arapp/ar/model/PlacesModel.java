package com.sb.arapp.ar.model;

import java.util.HashMap;

/**
 * Created by Bezet on 06/04/2017.
 */

public class PlacesModel {
    String placeId;
    String address;
    HashMap<String, Boolean> category;
    String description;
    String facilities;
    String imageThumbnail;
    String info;
    HashMap<String, Double> location;
    String phoneNumber;
    String placeName;
    boolean useUrl;
    boolean isFeatured;
    HashMap<String, Long> rates;
    Integer viewType;
    float distance;
    
    public void setIsFeatured(boolean isFeatured){ this.isFeatured = isFeatured;}
    
    public boolean getIsFeatured(){ return isFeatured;}
    
    public void setUseUrl(boolean useUrl) {
        this.useUrl = useUrl;
    }

    public boolean getUseUrl(){
        return useUrl;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Integer getViewType() {
        return viewType;
    }

    public void setViewType(Integer viewType) {
        this.viewType = viewType;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageThumbnail() {
        return imageThumbnail;
    }

    public void setImageThumbnail(String imageThumbnail) {
        this.imageThumbnail = imageThumbnail;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public HashMap<String, Boolean> getCategory() {
        return category;
    }

    public void setCategory(HashMap<String, Boolean> category) {
        this.category = category;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public HashMap<String, Long> getRates() {
        return rates;
    }

    public void setRates(HashMap<String, Long> rates) {
        this.rates = rates;
    }

    public HashMap<String, Double> getLocation() {
        return location;
    }

    public void setLocation(HashMap<String, Double> location) {
        this.location = location;
    }
}
