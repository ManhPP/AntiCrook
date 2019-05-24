package com.android.findmyandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.android.findmyandroid.model.Contact;
import com.android.findmyandroid.model.Location;
import com.android.findmyandroid.model.Record;
import com.android.findmyandroid.model.SMS;
import com.android.findmyandroid.utils.ContactHandler;
import com.android.findmyandroid.utils.LocationHandler;
import com.android.findmyandroid.utils.RecordHandler;
import com.android.findmyandroid.utils.SMSHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class SMSReceiver extends BroadcastReceiver implements OnReceiveLocationListener, OnReceiveRecordListener, Serializable,OnTakePictureListener{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("smsreceive", "onReceive: aaaaaaa");
        SharedPreferences sharedPreferences= context.getSharedPreferences("appSetting", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("isActivated", false)) {
            SMSHandler smsHandler = new SMSHandler(context);
            Intent i;
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);;
            MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
            switch (smsHandler.getCommand()) {
                case 0: //bat wifi
//                    wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                    break;
                case 1: //doc tin nhan
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
                    List<Contact> listContacts = (new ContactHandler(context)).getAllContact();
                    Log.i("contact", "onReceive: num contact"+listContacts.size());
                    break;
                case 3: //ghi am
                    RecordHandler recordHandler = new RecordHandler(context);
                    recordHandler.setOnReceiveRecordListener(this);
                    break;
                case 4: //chup cam truoc
                    i = new Intent(context, CamService.class);
                    i.putExtra("onTakeePickture", this);
                    i.putExtra("isFront", true);
                    context.startService(i);
                    break;
                case 5: //chup cam sau
                    i = new Intent(context, CamService.class);
                    i.putExtra("onTakeePickture", this);
                    i.putExtra("isFront", false);
                    context.startService(i);
                    break;
                case 6: //dinh vi
                    LocationHandler locationHandler = new LocationHandler(context);
                    locationHandler.addOnReceiveLocationListener(this);
                    break;
                case 7: //bao dong
//                    String path = context.getApplicationContext().getExternalFilesDir(null)+"/warning.mp3";
//                    Uri warning = Uri.parse(path);
//                    RingtoneManager.setActualDefaultRingtoneUri(
//                            context.getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION,
//                            warning);
//                    Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), warning);
//                    r.play();
//                    Ringtone ringtone = RingtoneManager.getRingtone(context.getApplicationContext(), warning);
//                    ringtone.play();

                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                        Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
                        r.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    }


    @Override
    public void onReceiveLocation(Location location) {
        Log.i("onReceived", "Location: "+location.getLatitude()+"-"+location.getLongitude());
    }

    @Override
    public void onReceiveRecord(Record record) {
        Log.i("onReceived", "Record: " + record.getUrl());
    }

    @Override
    public void onTakePicture(String path) {
        Log.i("picture", "onTakePicture: "+ path);
    }
}
