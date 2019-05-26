package com.android.anticrook.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.android.anticrook.model.SMS;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tranv on 5/23/2019.
 */

public class SMSHandler {
    private Context context;
    public SMSHandler(Context context) {
        this.context = context;
    }

    //hàm trả về lệnh mà người dùng muốn thực hiện
    public int getCommand(){
        //lấy index của lệnh mà người dùng yêu cầu trong màng lệnh
        int ret = getListCommndSetting().indexOf(getCommandReceive());
        for (String s: getListCommndSetting()){
            Log.i("aaaa", "getCommand: "+(s.equals(getCommandReceive()))+"-"+s+"-"+getCommandReceive());
        }
        Log.d("ret", "getCommand: rettttttttt"+ret);
        return ret;
    }

    //hàm lấy mảng lệnh mà người dùng cái đặt
    public List<String> getListCommndSetting(){
        List<String> list = new ArrayList<>();

        SharedPreferences sharedPreferences = context.getSharedPreferences("appSetting", context.MODE_PRIVATE);
        //đọc các lệnh mà người dùng cài đặt được lưu trong sharepreferences
        if(sharedPreferences!=null) {
            list.add(sharedPreferences.getString("WIFI", "WIFI_ON"));
            list.add(sharedPreferences.getString("SMS", "READ_SMS"));
            list.add(sharedPreferences.getString("CONTACT", "READ_CONTACT"));
            list.add(sharedPreferences.getString("RECORD", "RECORD"));
            list.add(sharedPreferences.getString("FRONT", "FRONT_CAM"));
            list.add(sharedPreferences.getString("BACK", "BACK_CAM"));
            list.add(sharedPreferences.getString("LOCATE", "LOCATE"));
            list.add(sharedPreferences.getString("ALARM", "ALARM"));
            Log.d("smsreceive", "getListCommndSetting: not null");
        }else{
            Log.d("smsreceive", "getListCommndSetting: null");
        }

        return list;
    }

    //đọc lệnh từ sms khi có tin nhắn mới gửi đến
    public String getCommandReceive(){
        //khai báo đường dẫn uri cho đọc tin nhắn
        Uri uri = Uri.parse("content://sms/inbox");
        //lấy đối tượng cursor để đọc tất cả tin nhắn chưa được đọc
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        //đọc tin nhắn mới nhất
        cursor.moveToFirst();
        int idBody = cursor.getColumnIndex("body");
        //lấy ra tin nhắn
        String body = cursor.getString(idBody);
        cursor.close();
        return body;
    }

    //hàm lấy ra tất cả tin nhắn trong điện thoại
    public List<SMS> getAllSMS(){
        //khởi tạo mảng lưu đối tượng tin nhắn
        List<SMS> listSMS = new ArrayList<>();
        //khai báo uri để đọc tin nhắn
        Uri uri = Uri.parse("content://sms/inbox");
        //lấy ra đối tượng cursor để bắt đầu đọc
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        listSMS.clear();
        cursor.moveToFirst();
        if(cursor!=null) {
            while (!cursor.isAfterLast()) {
                //đọc só điện thoại, thời gian nhận, nội dung
                int idPhoneNumber = cursor.getColumnIndex("address");
                int idTimeStamp = cursor.getColumnIndex("date");
                int idBody = cursor.getColumnIndex("body");

                String phoneNumber = cursor.getString(idPhoneNumber);
                String timeStamp = cursor.getString(idTimeStamp);
                String body = cursor.getString(idBody);

                //khởi tạo đối tuonjg sms
                SMS sms = new SMS(body, timeStamp, phoneNumber);
                //khởi tạo thời gina ứng dụng đọc tin nhắn
                sms.setTime((new Date()).toString());
                listSMS.add(sms);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return listSMS;
    }
}
