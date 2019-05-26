package com.android.anticrook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
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
import java.util.List;

/**
 * Created by tranv on 4/14/2019.
 */

public class SMSReceiver extends BroadcastReceiver implements OnReceiveLocationListener,
        OnReceiveRecordListener,OnTakePictureListener, Serializable{
    //Lớp xử lí cho việc gửi email
    private static transient EmailHandler emailHandler;
    //cung cấp các hàm thao tác với db
    private static transient MyDatabaseHelper myDatabaseHelper;
    //đối tượng quản lí wifi
    private static transient WifiManager wifiManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        emailHandler = new EmailHandler(context);
        //kiểm tra xem ứng dụng đã được activate hay chưa
        SharedPreferences sharedPreferences= context.getSharedPreferences("appSetting", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("isActivated", false)) {
            SMSHandler smsHandler = new SMSHandler(context);
            Intent i;
            wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);;
            myDatabaseHelper = new MyDatabaseHelper(context);
            //đọc sms người dùng gửi đến và kiểm tra xem người dùng muốn thực hiện chức năng nào
            switch (smsHandler.getCommand()) {
                case 0: //bật wifi
                    wifiManager.setWifiEnabled(true);
                    break;
                case 1: //đọc tin nhan
                    List<SMS> listSMS = smsHandler.getAllSMS();
                    String content = "";
                    //kiểm tra xem mạng bật hay tắt để có hành động tương ứng
                    if(wifiManager.isWifiEnabled()){
                        //khởi tạo nội dung để gửi qua email
                        for(SMS sms : listSMS){
                            content += "Đọc lúc:"+sms.getTime()+"\n\t\tSDT:"+sms.getPhoneNumber()+" nhận lúc "+sms.getTimeReceive()+"\n\t\tNội dung: "+sms.getBody()+"\n";
                        }
                        List<String> listContent = new ArrayList<>();
                        listContent.add(content);
                        //gửi qua email
                        emailHandler.send(listContent,false, null);
                    }else{
                        //lưu vào db để chờ khi có kết nối mạng
                        myDatabaseHelper.deleteSMS();
                        for(SMS sms : listSMS){
                            myDatabaseHelper.addSMS(sms);
                        }
                    }
                    break;
                case 2: //đọc danh bạ
                    List<Contact> listContacts = (new ContactHandler(context)).getAllContact();
                    String contentContact = "";
                    //kiểm tra xem có kết nối mạng hay không để có hành động tương ứng
                    if(wifiManager.isWifiEnabled()) {
                        //khởi tạo nội dung và gửi
                        for (Contact contact : listContacts) {
                            contentContact += "Đọc lúc: " + contact.getTime() + "-Tên: " + contact.getName() + ": SĐT: " + contact.getPhone() + "\n";
                        }
                        List<String> listContent = new ArrayList<>();
                        listContent.add(contentContact);
                        //bắt đàu gửi
                        emailHandler.send(listContent, false, null);
                    }else{
                        //lưu vào db
                        myDatabaseHelper.deleteContact();
                        for(Contact contact : listContacts){
                            myDatabaseHelper.addContact(contact);
                        }
                        Log.i("sms", "onReceive read contact: save db");
                    }
                    break;
                case 3: //ghi âm
                    RecordHandler recordHandler = new RecordHandler(context);
                    //set call back cho việc ghi âm xong
                    recordHandler.setOnReceiveRecordListener(this);
                    break;
                case 4: //chup cam trước
                    i = new Intent(context, CamService.class);
                    i.putExtra("onTakeePickture", this);
                    i.putExtra("isFront", true);
                    //bắt đầu service cho việc chụp ảnh
                    context.startService(i);
                    break;
                case 5: //chup cam sau
                    i = new Intent(context, CamService.class);
                    i.putExtra("onTakeePickture", this);
                    i.putExtra("isFront", false);
                    //bắt đầu service cho việc chụp ảnh
                    context.startService(i);
                    break;
                case 6: //dinh vi
                    LocationHandler locationHandler = new LocationHandler(context);
                    //set call back cho việc đọc xong tọa độ
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

    //hàm xử lí call back cho việc đọc xong tọa độ
    @Override
    public void onReceiveLocation(Location location) {
        //nếu có wifi thì sẽ gửi qua email
        if(wifiManager.isWifiEnabled()) {
            List<String> contentAndPath = new ArrayList<>();
            contentAndPath.add("(Cập nhập: " + location.getTime() + "):Vị trí của điện thoại của bạn là: https://www.google.com/maps/place/" + location.getLatitude() + "," + location.getLongitude());
            //bắt đầu gửi
            emailHandler.send(contentAndPath, false, null);
        }else{
            //nếu không thì lưu vào db
            myDatabaseHelper.addLocate(location);
        }
    }

    //hàm xử lí call back cho việc ghi âm
    @Override
    public void onReceiveRecord(Record record) {
        //nếu có wifi thì gửi qua email
        if(wifiManager.isWifiEnabled()) {
            Log.i("onReceived", "Record: " + record.getUrl());
            List<String> contentAndPath = new ArrayList<>();
            contentAndPath.add("(Cập nhập: " + record.getTime() + "): Đã thư một bản ghi âm");
            //bắt đầu gửi
            contentAndPath.add(record.getUrl());
            emailHandler.send(contentAndPath, false, null);
        }else{
            //nếu khôn lưu vào db
            myDatabaseHelper.addRecord(record);
        }
    }

    //cho ca cam truoc va sau
    //hàm call back cho việc chụp ảnh xong
    @Override
    public void onTakePicture(Image image) {
        //có wifi thì gửi mail
        if(wifiManager.isWifiEnabled()) {
            List<String> contentAndPath = new ArrayList<>();
            contentAndPath.add("(Cập nhập: " + image.getTime() + "): Đã chụp ảnh môi trường xung quanh");
            contentAndPath.add(image.getUrl());
            //băt đầu gửi mail
            emailHandler.send(contentAndPath, false, null);
        }else{
            //nếu không thì lưu vào db
            myDatabaseHelper.addImage(image);
        }
    }
}
