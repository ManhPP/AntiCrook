package com.android.findmyandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tranv on 4/14/2019.
 */

public class SMSReceiver extends BroadcastReceiver implements OnReceiveLocationListener,
        OnReceiveRecordListener,OnTakePictureListener, Serializable{
    private static transient EmailHandler emailHandler;
    private static transient MyDatabaseHelper myDatabaseHelper;
    private static transient WifiManager wifiManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        emailHandler = new EmailHandler(context);
        SharedPreferences sharedPreferences= context.getSharedPreferences("appSetting", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("isActivated", false)) {
            SMSHandler smsHandler = new SMSHandler(context);
            Intent i;
            wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);;
            myDatabaseHelper = new MyDatabaseHelper(context);
            switch (smsHandler.getCommand()) {
                case 0: //bat wifi
                    wifiManager.setWifiEnabled(true);
                    break;
                case 1: //doc tin nhan
                    List<SMS> listSMS = smsHandler.getAllSMS();
                    String content = "";
                    if(wifiManager.isWifiEnabled()){
                        //gui qua email
                        for(SMS sms : listSMS){
                            content += "Đọc lúc:"+sms.getTime()+"\n\t\tSDT:"+sms.getPhoneNumber()+" nhận lúc "+sms.getTimeReceive()+"\n\t\tNội dung: "+sms.getBody()+"\n";
                        }
                        List<String> listContent = new ArrayList<>();
                        listContent.add(content);
                        emailHandler.send(listContent,false, null);
                    }else{
                        //luu vao db doi khi co internet se gui lai
                        myDatabaseHelper.deleteSMS();
                        for(SMS sms : listSMS){
                            myDatabaseHelper.addSMS(sms);
                        }
                    }
                    break;
                case 2: //doc danh ba
                    List<Contact> listContacts = (new ContactHandler(context)).getAllContact();
                    String contentContact = "";
                    if(wifiManager.isWifiEnabled()) {
                        for (Contact contact : listContacts) {
                            contentContact += "Đọc lúc: " + contact.getTime() + "-Tên: " + contact.getName() + ": SĐT: " + contact.getPhone() + "\n";
                        }
                        List<String> listContent = new ArrayList<>();
                        listContent.add(contentContact);
                        emailHandler.send(listContent, false, null);
                    }else{
                        myDatabaseHelper.deleteContact();
                        for(Contact contact : listContacts){
                            myDatabaseHelper.addContact(contact);
                        }
                        Log.i("sms", "onReceive read contact: save db");
                    }
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
                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                        final Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
                        new CountDownTimer(30000, 1000) {
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
                    break;

            }
        }
    }


    @Override
    public void onReceiveLocation(Location location) {
        if(wifiManager.isWifiEnabled()) {
            List<String> contentAndPath = new ArrayList<>();
            contentAndPath.add("(Cập nhập: " + location.getTime() + "):Vị trí của điện thoại của bạn là: https://www.google.com/maps/place/" + location.getLatitude() + "," + location.getLongitude());
            emailHandler.send(contentAndPath, false, null);
        }else{
            myDatabaseHelper.addLocate(location);
        }
    }

    @Override
    public void onReceiveRecord(Record record) {
        if(wifiManager.isWifiEnabled()) {
            Log.i("onReceived", "Record: " + record.getUrl());
            List<String> contentAndPath = new ArrayList<>();
            contentAndPath.add("(Cập nhập: " + record.getTime() + "): Đã thư một bản ghi âm");
            contentAndPath.add(record.getUrl());
            emailHandler.send(contentAndPath, false, null);
        }else{
            myDatabaseHelper.addRecord(record);
        }
    }

    //cho ca cam truoc va sau
    @Override
    public void onTakePicture(Image image) {
        if(wifiManager.isWifiEnabled()) {
            List<String> contentAndPath = new ArrayList<>();
            contentAndPath.add("(Cập nhập: " + image.getTime() + "): Đã chụp ảnh môi trường xung quanh");
            contentAndPath.add(image.getUrl());
            emailHandler.send(contentAndPath, false, null);
        }else{
            myDatabaseHelper.addImage(image);
        }
    }

}
