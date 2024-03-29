package com.android.anticrook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.anticrook.model.Contact;
import com.android.anticrook.model.Image;
import com.android.anticrook.model.Location;
import com.android.anticrook.model.Record;
import com.android.anticrook.model.SMS;
import com.android.anticrook.utils.ContactHandler;
import com.android.anticrook.utils.EmailHandler;
import com.android.anticrook.utils.LocationHandler;
import com.android.anticrook.utils.RecordHandler;
import com.android.anticrook.utils.SMSHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by manhpp on 5/23/2019.
 */

    public class SimChangeReceiver extends BroadcastReceiver implements OnReceiveLocationListener,
        OnReceiveRecordListener,OnTakePictureListener, OnSendEmailListener, Serializable {
    private static transient EmailHandler emailHandler;
    private static transient MyDatabaseHelper myDatabaseHelper;
    private static transient WifiManager wifiManager;
    //khoa cho bien x
    private static transient Object lock = new Object();
    //khoa cho bien isDone
    private static transient Object lockDone = new Object();
    //luu so luong cong viec da thuc hien xong
    private static int x=0;
    //kiem tra xem da chay xong tat ca cong viec o lan thay doi sim truoc chua
    private static boolean isDone = true;

    private static  transient List<SMS> listSMS = null;
    private static  transient List<Contact> listContacts = null;
    private static  transient Location location = null;
    private static  transient Record record = null;
    private static  transient Image image = null;


    @Override
    public void onReceive(Context context, Intent intent) {
        if(isDone){
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int state = telephonyManager.getSimState();
            String extraState = intent.getStringExtra("ss");
//        Log.i("onReceive",extraState);
            if(state == TelephonyManager.SIM_STATE_ABSENT || (state == TelephonyManager.SIM_STATE_READY && extraState.equals("LOADED"))){
                Log.i("onReceiveSim","thay doi sim");
                emailHandler = new EmailHandler(context);
                SharedPreferences sharedPreferences= context.getSharedPreferences("appSetting", Context.MODE_PRIVATE);
                if(sharedPreferences.getBoolean("isActivated", false)) {
                    emailHandler.setOnSendEmailListener(this);
                    synchronized (lockDone){
                        isDone = false;
                    }
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
                    Log.i("xxxxxxxx", "onReceive: read sms"+x);

                    if (sharedPreferences.getBoolean("readContact", false)) {
                        //doc danh ba
                        Log.i("aaaa", "onReceive: Readsms");
                        List<Contact> listContacts = (new ContactHandler(context)).getAllContact();
                        SimChangeReceiver.listContacts = listContacts;
                    }
                    x++;
                    Log.i("xxxxxxxx", "onReceive: read sms"+x);

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
    }


    @Override
    public void onReceiveLocation(Location location) {
        SimChangeReceiver.location = location;
        synchronized (lock){
            x++;
        }
        Log.i("xxxxxxxx", "onReceiveLocation: "+x);
        if(x==5) sendOrSave(SimChangeReceiver.listSMS, SimChangeReceiver.listContacts, SimChangeReceiver.location,
                SimChangeReceiver.record, SimChangeReceiver.image);
    }

    @Override
    public void onReceiveRecord(Record record) {
        SimChangeReceiver.record = record;
        synchronized (lock){
            x++;
        }
        Log.i("xxxxxxxx", "onReceiveRecord: "+x);
        if(x==5) sendOrSave(SimChangeReceiver.listSMS, SimChangeReceiver.listContacts, SimChangeReceiver.location,
                SimChangeReceiver.record, SimChangeReceiver.image);
    }

    //cho ca cam truoc va sau
    @Override
    public void onTakePicture(Image image) {
        if(image!=null) {
            SimChangeReceiver.image = image;
            Log.i("xxxxxxxx", "onTakePicture: " + x);
        }
        synchronized (lock){
            x++;
        }
        if(x==5) sendOrSave(SimChangeReceiver.listSMS, SimChangeReceiver.listContacts, SimChangeReceiver.location,
                SimChangeReceiver.record, SimChangeReceiver.image);
    }

    public void sendOrSave(List<SMS> listSMS, List<Contact> listContacts,
                           Location location, Record record, Image image){

        Log.i("ooooooo", "sendOrSave: ----------------------------------------");
        SimChangeReceiver.x=0;
        if(wifiManager.isWifiEnabled()){
            List<String> contentAndPath = new ArrayList<>();
            String content="";
            if(listSMS!=null) {
                for (SMS sms : listSMS) {
                    content += "Đọc lúc:" + sms.getTime() + "\n\t\tSDT:" + sms.getPhoneNumber() + " nhận lúc " + (new Date(Long.parseLong(sms.getTimeReceive()))).toString() + "\n\t\tNội dung: " + sms.getBody() + "\n";
                }

            }
            if(listContacts!=null) {
                for (Contact contact : listContacts) {
                    content += "Đọc lúc: " + contact.getTime() + "-Tên: " + contact.getName() + ": SĐT: " + contact.getPhone() + "\n";
                }
            }
            if(location!=null) {
                content += "(Cập nhập: " + location.getTime() + "):Vị trí của điện thoại của bạn là: https://www.google.com/maps/place/" + location.getLatitude() + "," + location.getLongitude() + "\n";
                Log.i("abcde", "sendOrSave: "+location.getLongitude());
            }
            if(record!=null) {
                content += "(Cập nhập: " + record.getTime() + "): Đã ghi một bản ghi âm\n";
            }
            if(image!=null) {
                content += "(Cập nhập: " + image.getTime() + "): Đã chụp ảnh môi trường xung quanh\n";
            }
            contentAndPath.add(content);

            if(record!=null) {
                contentAndPath.add(record.getUrl());
            }
            if(image!=null) {
                contentAndPath.add(image.getUrl());
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
            if(image!=null){
                myDatabaseHelper.addImage(image);
            }
            synchronized (lockDone){
                isDone = true;
            }
            Log.i("aaaa", "sendOrSave: save done");
        }
    }

    @Override
    public void onSendEmail() {
        synchronized (lockDone){
            isDone = true;
        }
    }
}
