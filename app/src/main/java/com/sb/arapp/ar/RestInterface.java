package com.sb.arapp.ar;


import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by ihsan_bz on 30/06/2016.
 */

public interface RestInterface {
    
    @GET("/json")
    void getAddress(@Query("address") String key,
                    @Query("sensor") String part,
                    Callback<AddressLoc> cb);

    @GET("/json")
    void getElevation(@Query("locations") String location,
                      @Query("sensor") String sensor,
                      @Query("key") String key,
                      Callback<Elevation> el);

}
