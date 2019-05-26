package com.android.anticrook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.android.anticrook.model.Contact;
import com.android.anticrook.model.Image;
import com.android.anticrook.model.Location;
import com.android.anticrook.model.Record;
import com.android.anticrook.model.SMS;
import com.android.anticrook.utils.EmailHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by manhpp on 5/25/2019.
 */

public class NetworkReceiver extends BroadcastReceiver {
    List<SMS> listSMS = null;
    List<Contact> listContacts = null;
    List<Location> listLocation = null;
    List<Record> listRecord = null;
    List<Image> listImage = null;
    MyDatabaseHelper helper = null;
    EmailHandler emailHandler = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        helper = new MyDatabaseHelper(context.getApplicationContext());
        emailHandler = new EmailHandler(context);

        final ConnectivityManager conn = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifi.isAvailable() && wifi.isConnected())) {
            if(readDB(helper)) {
                send();
                helper.deleteLocate();
                helper.deleteContact();
                helper.deleteImage();
                helper.deleteSMS();
                helper.deleteRecord();
            }
        }
        else if (mobile.isAvailable() && mobile.isConnected()){
            if(readDB(helper)) {
                send();
                helper.deleteLocate();
                helper.deleteContact();
                helper.deleteImage();
                helper.deleteSMS();
                helper.deleteRecord();
            }
        }
    }
    public boolean readDB(MyDatabaseHelper helper){
        listLocation =helper.getLocate();
        listSMS = helper.getListSMS();
        listContacts = helper.getListContacts();
        listRecord = helper.getRecord();
        listImage = helper.getImage();
        if(listLocation.size()==0 && listSMS.size()==0 && listContacts.size()==0 && listRecord.size()==0 && listImage.size()==0){
            return false;
        }
        return true;
    }
    public void send(){
        String content = "";
        List<String> contentAndPath = new ArrayList<>();

        if(listContacts != null && listContacts.size()>0){
            content += "Contact:\n";
            for (Contact contact : listContacts) {
                content += "Đọc lúc: " + contact.getTime() + "-Tên: " + contact.getName() + ": SĐT: " + contact.getPhone() + "\n";
            }
        }
        if(listRecord != null && listRecord.size()>0){
            content += "Record: Đã ghi âm vào các thời điểm\n";
            for (Record record : listRecord) {
                content += "(Cập nhập: " + record.getTime() + ")\n";
            }
        }
        if (listSMS != null && listSMS.size()>0){
            content += "SMS:\n";
            for (SMS sms : listSMS) {
                content += "Đọc lúc:" + sms.getTime() + "\n\t\tSDT:" + sms.getPhoneNumber() + " nhận lúc " + (new Date(Long.parseLong(sms.getTimeReceive()))).toString() + "\n\t\tNội dung: " + sms.getBody() + "\n";
            }
        }
        if (listImage != null && listImage.size()>0){
            content += "Images: Đã chụp ảnh môi trường xung quanh vào các thời điểm:\n";
            for(Image image: listImage){
                content += "(Cập nhập: " + image.getTime() + ") \n";
            }
        }
        if (listLocation != null && listLocation.size()>0){
            content += "Location:\n";
            for(Location location: listLocation){
                content += "(Cập nhập: " + location.getTime() + "):Vị trí của điện thoại của bạn là: " + location.getLatitude() + ", " + location.getLongitude() + "\n";
            }
        }
        contentAndPath.add(content);
        if(listRecord != null && listRecord.size()>0){
            for (Record record : listRecord) {
                contentAndPath.add(record.getUrl());
            }
        }
        if(listImage != null && listImage.size()>0){
            for(Image image: listImage){
                contentAndPath.add(image.getUrl());
            }
        }
        emailHandler.send(contentAndPath, false, "");
    }
}
