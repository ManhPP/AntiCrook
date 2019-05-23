package com.android.findmyandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.android.findmyandroid.model.Location;
import com.android.findmyandroid.model.Record;
import com.android.findmyandroid.utils.LocationHandler;
import com.android.findmyandroid.utils.RecordHandler;

/**
 * Created by manhpp on 5/23/2019.
 */

public class SimChangeReceiver extends BroadcastReceiver implements OnReceiveRecordListener, OnReceiveLocationListener{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("onReceived", "changed sim");

        SharedPreferences sharedPreferences = context.getSharedPreferences("isActivated",Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("isActivated",false)){
            //bat wifi
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            if(wifiManager != null){
                wifiManager.setWifiEnabled(true);
            }
            //ghi am
            RecordHandler recordHandler = new RecordHandler();
            recordHandler.setOnReceiveRecordListener(this);
            //dinh vi
            LocationHandler locationHandler = new LocationHandler(context);
            locationHandler.addOnReceiveLocationListener(this);
        }
    }

    @Override
    public void onReceiveRecord(Record record) {
        Log.i("onReceived", "Record: " + record.getUrl());
    }

    @Override
    public void onReceiveLocation(Location location) {
        Log.i("onReceived", "Location: "+location.getLatitude()+"-"+location.getLongitude());
    }
}
