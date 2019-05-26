package com.android.anticrook.utils;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.anticrook.OnReceiveLocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

/**
 * Created by tranv on 5/21/2019.
 */

public class LocationHandler implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private Location location;
    private static GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private OnReceiveLocationListener onReceiveLocationListener;

    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private Context context;
    // lists for permissions

    public LocationHandler(){

    }

    public LocationHandler(Context context){

        this.context = context;
        //khởi tạo đối tượng GoogleApiClient để lấy tọa độ
        googleApiClient = new GoogleApiClient.Builder(context).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            // Permissions ok, we get last location
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            com.android.anticrook.model.Location myLocation
                    = new com.android.anticrook.model.Location(location.getLatitude()+"", location.getLongitude()+"", (new Date()).toString());
            //gọ hàm call back sau khi lấy được tọa độ
            onReceiveLocationListener.onReceiveLocation(myLocation);

        }catch (SecurityException e){
            Log.i("location", "onConnected: noo");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //call back khi không kết nối được
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("location", "onConnectionFailed: noo");
    }

    @Override
    public void onLocationChanged(Location location) {
        //callback khi location thay doi
    }


    public void addOnReceiveLocationListener(OnReceiveLocationListener onReceiveLocationListener){
        this.onReceiveLocationListener = onReceiveLocationListener;
        googleApiClient.connect();
    }
}
