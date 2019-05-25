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
import com.android.findmyandroid.model.Image;
import com.android.findmyandroid.model.Location;
import com.android.findmyandroid.model.Record;
import com.android.findmyandroid.model.SMS;
import com.android.findmyandroid.utils.ContactHandler;
import com.android.findmyandroid.utils.EmailHandler;
import com.android.findmyandroid.utils.LocationHandler;
import com.android.findmyandroid.utils.RecordHandler;
import com.android.findmyandroid.utils.SMSHandler;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by manhpp on 5/23/2019.
 */

public class SimChangeReceiver extends BroadcastReceiver implements OnReceiveLocationListener,
        OnReceiveRecordListener,OnTakePictureListener, Serializable {
    private static transient EmailHandler emailHandler;
    private static transient MyDatabaseHelper myDatabaseHelper;
    private static transient WifiManager wifiManager;
    private final Object lock = new Object();
    private int x=0;

    private static  transient List<SMS> listSMS = null;
    private static  transient List<Contact> listContacts = null;
    private static  transient Location location = null;
    private static  transient Record record = null;
    private static  transient Image image1 = null;
    private static  transient Image image2 = null;


    @Override
    public void onReceive(Context context, Intent intent) {
        emailHandler = new EmailHandler(context);
        SharedPreferences sharedPreferences= context.getSharedPreferences("appSetting", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("isActivated", false)) {
            SMSHandler smsHandler = new SMSHandler(context);
            Intent i;
            wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);;
            myDatabaseHelper = new MyDatabaseHelper(context);
            //doc tin nhan
            List<SMS> listSMS = smsHandler.getAllSMS();
            SimChangeReceiver.listSMS = listSMS;
            x++;
            //doc danh ba
            List<Contact> listContacts = (new ContactHandler(context)).getAllContact();
            SimChangeReceiver.listContacts = listContacts;
            x++;
            //ghi am
            RecordHandler recordHandler = new RecordHandler(context);
            recordHandler.setOnReceiveRecordListener(this);
            //chup cam truoc
            i = new Intent(context, CamService.class);
            i.putExtra("onTakeePickture", this);
            i.putExtra("isFront", true);
            context.startService(i);
            //chup cam sau
            i = new Intent(context, CamService.class);
            i.putExtra("onTakeePickture", this);
            i.putExtra("isFront", false);
            context.startService(i);
            //dinh vi
            LocationHandler locationHandler = new LocationHandler(context);
            locationHandler.addOnReceiveLocationListener(this);
        }
    }


    @Override
    public void onReceiveLocation(Location location) {
        SimChangeReceiver.location = location;
        synchronized (lock){
            x++;
        }
        if(x==6) sendOrSave(SimChangeReceiver.listSMS, SimChangeReceiver.listContacts, SimChangeReceiver.location,
                SimChangeReceiver.record, SimChangeReceiver.image1, SimChangeReceiver.image2);
    }

    @Override
    public void onReceiveRecord(Record record) {
        SimChangeReceiver.record = record;
        synchronized (lock){
            x++;
        }
        if(x==6) sendOrSave(SimChangeReceiver.listSMS, SimChangeReceiver.listContacts, SimChangeReceiver.location,
                SimChangeReceiver.record, SimChangeReceiver.image1, SimChangeReceiver.image2);
    }

    //cho ca cam truoc va sau
    @Override
    public void onTakePicture(Image image) {
        if(SimChangeReceiver.image1 != null){
            SimChangeReceiver.image1 = image;
        }else{
            SimChangeReceiver.image2 = image;
        }
        synchronized (lock){
            x++;
        }
        if(x==6) sendOrSave(SimChangeReceiver.listSMS, SimChangeReceiver.listContacts, SimChangeReceiver.location,
                SimChangeReceiver.record, SimChangeReceiver.image1, SimChangeReceiver.image2);
    }

    public void sendOrSave(List<SMS> listSMS, List<Contact> listContacts,
                           Location location, Record record, Image image1, Image image2){

    }
}
