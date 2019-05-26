package com.android.anticrook.utils;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
            onReceiveLocationListener.onReceiveLocation(myLocation);

//            startLocationUpdates();
        }catch (SecurityException e){

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //callback khi location thay doi

        if (location != null) {
            com.android.anticrook.model.Location myLocation
                    = new com.android.anticrook.model.Location(location.getLatitude()+"", location.getLongitude()+"", (new Date()).toString());
            onReceiveLocationListener.onReceiveLocation(myLocation);
        }
    }

    public void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }catch (SecurityException e){

        }
    }


    public void addOnReceiveLocationListener(OnReceiveLocationListener onReceiveLocationListener){
        this.onReceiveLocationListener = onReceiveLocationListener;
        googleApiClient.connect();
    }
}
