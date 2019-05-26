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

    public int getCommand(){
        int ret = getListCommndSetting().indexOf(getCommandReceive());
        for(String s: getListCommndSetting())
            Log.i("aaaaa", s.trim().length()+"---"+getCommandReceive().trim().length());
        Log.i("aaaaa", "getCommand: ========="+ret);
        return ret;
    }

    public List<String> getListCommndSetting(){
        List<String> list = new ArrayList<>();

        SharedPreferences sharedPreferences = context.getSharedPreferences("appSetting", context.MODE_PRIVATE);
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

    public String getCommandReceive(){
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(uri, null, "read=0", null, null);
        cursor.moveToLast();
        int idBody = cursor.getColumnIndex("body");
        String body = cursor.getString(idBody);
        cursor.close();
        return body;
    }

    public List<SMS> getAllSMS(){
        List<SMS> listSMS = new ArrayList<>();
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        listSMS.clear();
        cursor.moveToFirst();
        if(cursor!=null) {
            while (!cursor.isAfterLast()) {
                int idPhoneNumber = cursor.getColumnIndex("address");
                int idTimeStamp = cursor.getColumnIndex("date");
                int idBody = cursor.getColumnIndex("body");

                String phoneNumber = cursor.getString(idPhoneNumber);
                String timeStamp = cursor.getString(idTimeStamp);
                String body = cursor.getString(idBody);

                SMS sms = new SMS(body, timeStamp, phoneNumber);
                sms.setTime((new Date()).toString());
                listSMS.add(sms);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return listSMS;
    }



}
