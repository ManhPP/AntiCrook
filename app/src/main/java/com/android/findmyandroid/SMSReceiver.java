package com.android.findmyandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.android.findmyandroid.model.Location;
import com.android.findmyandroid.model.Record;
import com.android.findmyandroid.model.SMS;
import com.android.findmyandroid.utils.LocationHandler;
import com.android.findmyandroid.utils.SMSHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tranv on 4/14/2019.
 */

public class SMSReceiver extends BroadcastReceiver implements OnReceiveLocationListener, OnReceiveRecordListener{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("smsreceive", "onReceive: aaaaaaa");
        SharedPreferences sharedPreferences= context.getSharedPreferences("appSetting", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("isActivated", false)) {
            SMSHandler smsHandler = new SMSHandler(context);
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);;
            MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
            switch (smsHandler.getCommand()) {
                case -11:
                    break;
                case 0: //bat wifi
//                    wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                    break;
                case -1: //doc tin nhan
//                    wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                    List<SMS> list = smsHandler.getAllSMS();
                    if(wifiManager.isWifiEnabled()){
                        //gui qua email
                    }else{
                        //luu vao db doi khi co internet se gui lai
                        myDatabaseHelper.deleteSMS();
                        Log.i("asd", "onReceive: "+list.size()+"-------");
                        for(SMS sms : list){
                            myDatabaseHelper.addSMS(sms);
                            Log.i("asd", "onReceive: "+sms.getBody());
                        }
                    }
                    break;
                case 2: //doc danh ba
                    break;
                case 3: //ghi am
                    break;
                case 4: //chup cam truoc
                    break;
                case 5: //chup cam sau
                    break;
                case 6: //dinh vi
                    LocationHandler locationHandler = new LocationHandler(context);
                    locationHandler.addOnReceiveLocationListener(this);
                    break;
                case 7: //bao dong
                    break;

            }
        }
    }


    @Override
    public void onReceiveLocation(Location location) {
        Log.i("onReceived", "Location: "+location.getLatitude()+"-"+location.getLongitude());
    }

    @Override
    public void onReceive(Record record) {
        Log.i("onReceived","Record: "+record.getUrl());
    }
}
