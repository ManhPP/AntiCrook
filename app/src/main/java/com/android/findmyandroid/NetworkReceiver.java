package com.android.findmyandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by manhpp on 5/25/2019.
 */

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final ConnectivityManager conn = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifi.isAvailable() && wifi.isConnected())) {
            // Do something
                Log.i("Network Available ", "Flag No 1");
        }
        else if (mobile.isAvailable() && mobile.isConnected()){
            Log.i("Network Available ", "Flag No 2");
        }
    }
}
