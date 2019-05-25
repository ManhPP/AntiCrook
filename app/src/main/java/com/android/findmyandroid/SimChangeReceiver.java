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
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;

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

import static com.android.findmyandroid.R.id.parent;

/**
 * Created by manhpp on 5/23/2019.
 */

    public class SimChangeReceiver extends BroadcastReceiver implements OnReceiveLocationListener,
        OnReceiveRecordListener,OnTakePictureListener, Serializable {
    private static transient EmailHandler emailHandler;
    private static transient MyDatabaseHelper myDatabaseHelper;
    private static transient WifiManager wifiManager;
    private static transient Object lock = new Object();
    private static int x=0;

    private static  transient List<SMS> listSMS = null;
    private static  transient List<Contact> listContacts = null;
    private static  transient Location location = null;
    private static  transient Record record = null;
    private static  transient Image image1 = null;
    private static  transient Image image2 = null;


    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int state = telephonyManager.getSimState();
        String extraState = intent.getStringExtra("ss");
//        Log.i("onReceive",extraState);
        if(state == TelephonyManager.SIM_STATE_ABSENT || (state == TelephonyManager.SIM_STATE_READY && extraState.equals("LOADED"))){
            Log.i("onReceiveSim","thay doi sim");
            emailHandler = new EmailHandler(context);
            SharedPreferences sharedPreferences= context.getSharedPreferences("appSetting", Context.MODE_PRIVATE);
            if(sharedPreferences.getBoolean("isActivated", false)) {
                SMSHandler smsHandler = new SMSHandler(context);
                Intent i;
                wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);;
                myDatabaseHelper = new MyDatabaseHelper(context);
                if (sharedPreferences.getBoolean("readSMS", false)) {
                    //doc tin nhan
                    Log.i("aaaa", "onReceive: Readsms");
                    List<SMS> listSMS = smsHandler.getAllSMS();
                    SimChangeReceiver.listSMS = listSMS;
                }
                x++;

                if (sharedPreferences.getBoolean("readContact", false)) {
                    //doc danh ba
                    Log.i("aaaa", "onReceive: Readsms");
                    List<Contact> listContacts = (new ContactHandler(context)).getAllContact();
                    SimChangeReceiver.listContacts = listContacts;
                }
                x++;

                if (sharedPreferences.getBoolean("record", false)) {
                    //ghi am
                    Log.i("aaaa","ghi am");
                    RecordHandler recordHandler = new RecordHandler(context);
                    recordHandler.setOnReceiveRecordListener(this);
                }else{
                    x++;
                }
                if (sharedPreferences.getBoolean("frontCam", false)) {
                    //chup cam truoc
                    Log.i("aaaa","chup cam truoc");
                    i = new Intent(context, CamService.class);
                    i.putExtra("onTakeePickture", this);
                    i.putExtra("isFront", true);
                    context.startService(i);
                }else{
                    x++;
                }
                if (sharedPreferences.getBoolean("behindCam", false)) {
                    //chup cam sau
                    Log.i("aaaa","chup cam sau");
                    i = new Intent(context, CamService.class);
                    i.putExtra("onTakeePickture", this);
                    i.putExtra("isFront", false);
                    context.startService(i);
                }else{
                    x++;
                }
                if (sharedPreferences.getBoolean("locate", false)) {
                    //dinh vi
                    Log.i("aaaa","lay locate");
                    LocationHandler locationHandler = new LocationHandler(context);
                    locationHandler.addOnReceiveLocationListener(this);
                }else{
                    x++;
                }
            }
        }
    }


    @Override
    public void onReceiveLocation(Location location) {
        SimChangeReceiver.location = location;
        synchronized (lock){
            x++;
        }
        Log.i("xxxxxxxx", "onReceiveLocation: "+x);
        if(x==6) sendOrSave(SimChangeReceiver.listSMS, SimChangeReceiver.listContacts, SimChangeReceiver.location,
                SimChangeReceiver.record, SimChangeReceiver.image1, SimChangeReceiver.image2);
    }

    @Override
    public void onReceiveRecord(Record record) {
        SimChangeReceiver.record = record;
        synchronized (lock){
            x++;
        }
        Log.i("xxxxxxxx", "onReceiveRecord: "+x);
        if(x==6) sendOrSave(SimChangeReceiver.listSMS, SimChangeReceiver.listContacts, SimChangeReceiver.location,
                SimChangeReceiver.record, SimChangeReceiver.image1, SimChangeReceiver.image2);
    }

    //cho ca cam truoc va sau
    @Override
    public void onTakePicture(Image image) {
        if(SimChangeReceiver.image1 == null){
            SimChangeReceiver.image1 = image;
        }else{
            SimChangeReceiver.image2 = image;
        }
        synchronized (lock){
            x++;
        }
        Log.i("xxxxxxxx", "onTakePicture: "+x);
        if(x==6) sendOrSave(SimChangeReceiver.listSMS, SimChangeReceiver.listContacts, SimChangeReceiver.location,
                SimChangeReceiver.record, SimChangeReceiver.image1, SimChangeReceiver.image2);
    }

    public void sendOrSave(List<SMS> listSMS, List<Contact> listContacts,
                           Location location, Record record, Image image1, Image image2){

        Log.i("ooooooo", "sendOrSave: ----------------------------------------");
        x=0;
        if(wifiManager.isWifiEnabled()){
            List<String> contentAndPath = new ArrayList<>();
            String content="";
            if(listSMS!=null) {
                for (SMS sms : listSMS) {
                    content += "Đọc lúc:" + sms.getTime() + "\n\t\tSDT:" + sms.getPhoneNumber() + " nhận lúc " + sms.getTimeReceive() + "\n\t\tNội dung: " + sms.getBody() + "\n";
                }

            }
            if(listContacts!=null) {
                for (Contact contact : listContacts) {
                    content += "Đọc lúc: " + contact.getTime() + "-Tên: " + contact.getName() + ": SĐT: " + contact.getPhone() + "\n";
                }
            }
            if(location!=null) {
                content += "(Cập nhập: " + location.getTime() + "):Vị trí của điện thoại của bạn là: " + location.getLatitude() + ", " + location.getLongitude() + "\n";
                Log.i("abcde", "sendOrSave: "+location.getLongitude());
            }
            if(record!=null) {
                content += "(Cập nhập: " + record.getTime() + "): Đã ghi một bản ghi âm\n";
            }
            if(image1!=null) {
                content += "(Cập nhập: " + image1.getTime() + "): Đã chụp ảnh môi trường xung quanh\n";
            }
            if(image2!=null) {
                content += "(Cập nhập: " + image2.getTime() + "): Đã chụp ảnh môi trường xung quanh\n";
            }
            contentAndPath.add(content);

            if(record!=null) {
                contentAndPath.add(record.getUrl());
            }
            if(image1!=null) {
                contentAndPath.add(image1.getUrl());
            }
            if(image2!=null) {
                contentAndPath.add(image2.getUrl());
            }
            emailHandler.send(contentAndPath, false, "");
        }else{
            if(listSMS!=null){
                myDatabaseHelper.deleteSMS();
                for(SMS sms : listSMS){
                    myDatabaseHelper.addSMS(sms);
                }
            }
            if(listContacts!=null){
                myDatabaseHelper.deleteContact();
                for(Contact contact : listContacts){
                    myDatabaseHelper.addContact(contact);
                }
            }
            if(location!=null){
                myDatabaseHelper.addLocate(location);
            }
            if(record!=null){
                myDatabaseHelper.addRecord(record);
            }
            if(image1!=null){
                myDatabaseHelper.addImage(image1);
            }
            if(image2!=null){
                myDatabaseHelper.addImage(image2);
            }
        }
    }
}
