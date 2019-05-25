package com.android.findmyandroid;

import android.app.Activity;
import android.app.Application;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;

import com.android.findmyandroid.model.Contact;
import com.android.findmyandroid.model.Location;
import com.android.findmyandroid.model.Record;
import com.android.findmyandroid.model.SMS;
import com.android.findmyandroid.utils.ContactHandler;
import com.android.findmyandroid.utils.LocationHandler;
import com.android.findmyandroid.utils.RecordHandler;
import com.android.findmyandroid.utils.SMSHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.android.findmyandroid.R.id.parent;

/**
 * Created by manhpp on 5/23/2019.
 */

public class SimChangeReceiver extends SMSReceiver {
    SharedPreferences sharedPreferences = null;
    private static transient WifiManager wifiManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences("appSetting", Context.MODE_PRIVATE);
        Log.i("onReceived", "changed sim");
        Intent intent1;
        SharedPreferences sharedPreferences = context.getSharedPreferences("appSetting", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isActivated", false)) {
            Log.i("onReceived", "activated");
            wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //ghi am
            if (sharedPreferences.getBoolean("record", false)) {
                RecordHandler recordHandler = new RecordHandler(context);
                recordHandler.setOnReceiveRecordListener(this);
                Log.i("onReceived", "recorded");
            }
            //dinh vi
            if (sharedPreferences.getBoolean("locate", false)) {
                LocationHandler locationHandler = new LocationHandler(context);
                locationHandler.addOnReceiveLocationListener(this);
            }

            //doc danh ba
            if (sharedPreferences.getBoolean("readContact", false)) {
                List<Contact> listContacts = (new ContactHandler(context)).getAllContact();
                Log.i("contact", "onReceive: num contact" + listContacts.size());
            }

            //chup anh cam truoc
            if (sharedPreferences.getBoolean("fontCam", false)) {
                intent1 = new Intent(context, CamService.class);
                intent1.putExtra("onTakeePickture", this);
                intent1.putExtra("isFront", true);
                context.startService(intent1);
            }


            //chup anh cam sau
            if (sharedPreferences.getBoolean("behindCam", false)) {
                intent1 = new Intent(context, CamService.class);
                intent1.putExtra("onTakeePickture", this);
                intent1.putExtra("isFront", false);
                context.startService(intent1);
            }
            if (sharedPreferences.getBoolean("readSMS", false)) {
                List<SMS> listSMS = new SMSHandler(context).getAllSMS();
                String content = "";
                if (wifiManager.isWifiEnabled()) {
                    //gui qua email
                    for (SMS sms : listSMS) {
                        content += "Đọc lúc:" + sms.getTime() + "\n\t\tSDT:" + sms.getPhoneNumber() + " nhận lúc " + sms.getTimeReceive() + "\n\t\tNội dung: " + sms.getBody() + "\n";
                    }
                    List<String> listContent = new ArrayList<>();
                    listContent.add(content);
                }
            }
        }
    }
}
