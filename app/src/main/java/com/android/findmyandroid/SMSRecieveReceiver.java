package com.android.findmyandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.android.findmyandroid.model.SMS;
import com.android.findmyandroid.utils.SMSHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tranv on 4/14/2019.
 */

public class SMSRecieveReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences= context.getSharedPreferences("appSetting", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("isActived", false)) {
            SMSHandler smsHandler = new SMSHandler(context);
            WifiManager wifiManager;
            MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
            switch (smsHandler.getCommand()) {
                case -1:
                    break;
                case 0: //bat wifi
                    wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                    break;
                case 1: //doc tin nhan
                    wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                    List<SMS> list = smsHandler.getAllSMS();
                    if(wifiManager.isWifiEnabled()){
                        //gui qua email
                    }else{
                        //luu vao db doi khi co internet se gui lai
                        myDatabaseHelper.deleteSMS();
                        for(SMS sms : list){
                            myDatabaseHelper.addSMS(sms);
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
                    break;
                case 7: //bao dong
                    break;

            }
        }
    }
}
