package com.android.findmyandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.util.Log;

import com.android.findmyandroid.model.Contact;
import com.android.findmyandroid.model.Location;
import com.android.findmyandroid.model.Record;
import com.android.findmyandroid.utils.ContactHandler;
import com.android.findmyandroid.utils.LocationHandler;
import com.android.findmyandroid.utils.RecordHandler;

import java.io.IOException;
import java.util.List;

/**
 * Created by manhpp on 5/23/2019.
 */

public class SimChangeReceiver extends SMSReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("onReceived", "changed sim");
        Intent intent1;
        SharedPreferences sharedPreferences = context.getSharedPreferences("appSetting",Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("isActivated",false)){
            Log.i("onReceived","activated");
            //bat wifi
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if(wifiManager != null){
                wifiManager.setWifiEnabled(true);
                Log.i("onReceived","wifi on");
            }
            //ghi am
            RecordHandler recordHandler = new RecordHandler(context);
            recordHandler.setOnReceiveRecordListener(this);
            Log.i("onReceived","recorded");
            //dinh vi
            LocationHandler locationHandler = new LocationHandler(context);
            locationHandler.addOnReceiveLocationListener(this);
            //doc danh ba
            List<Contact> listContacts = (new ContactHandler(context)).getAllContact();
            Log.i("contact", "onReceive: num contact"+listContacts.size());
            //chup anh cam truoc
            intent1 = new Intent(context, CamService.class);
            intent1.putExtra("onTakeePickture", this);
            intent1.putExtra("isFront", true);
            context.startService(intent1);

            //bao dong
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                final Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
                new CountDownTimer(10000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        r.play();
                    }

                    public void onFinish() {
                    }
                }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
