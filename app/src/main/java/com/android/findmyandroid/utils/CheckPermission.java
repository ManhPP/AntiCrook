package com.android.findmyandroid.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manhpp on 5/23/2019.
 */

public class CheckPermission {
    Context context;
    Activity activity;
    final int REQUEST_MULTIPLE_PERMISSIONS = 1;
    public CheckPermission(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }
    public boolean checkAndRequestPermission(String[] needPermissions){

        List<String> listPermissionDenied = new ArrayList<>();
        String[] permissions = null;
        if (needPermissions == null){
            permissions = new String[] {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.INTERNET,
//                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
        }
        else{
            permissions = needPermissions;
        }


        for (String per: permissions) {
            if (ContextCompat.checkSelfPermission(this.context, per) != PackageManager.PERMISSION_GRANTED){
                listPermissionDenied.add(per);
            }
        }

        if(!listPermissionDenied.isEmpty()){
            ActivityCompat.requestPermissions(activity,listPermissionDenied.toArray(new String[listPermissionDenied.size()]), REQUEST_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
