package com.android.findmyandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.findmyandroid.model.EmailReceive;
import com.android.findmyandroid.model.Image;
import com.android.findmyandroid.model.Location;
import com.android.findmyandroid.model.Record;
import com.android.findmyandroid.model.SMS;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tranv on 5/22/2019.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FindMyPhone";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_LOCATE = "Location";
    private static final String TABLE_IMAGE = "Image";
    private static final String TABLE_RECORD = "Record";
    private static final String TABLE_EMAIL_RECEIVE = "EmailReceive";
    private static final String TABLE_SMS = "SMS";

    public MyDatabaseHelper(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql=" CREATE TABLE "+TABLE_LOCATE +"( id  INTEGER PRIMARY KEY AUTOINCREMENT, latitude TEXT, longitude TEXT, time TEXT);";
        db.execSQL(sql);
        sql=" CREATE TABLE "+TABLE_IMAGE +"( id  INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, time TEXT);";
        db.execSQL(sql);
        sql=" CREATE TABLE "+TABLE_RECORD +"( id  INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, time TEXT);";
        db.execSQL(sql);
        sql=" CREATE TABLE "+TABLE_EMAIL_RECEIVE +"( _id  INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT);";
        db.execSQL(sql);
        sql=" CREATE TABLE "+TABLE_SMS +"( id  INTEGER PRIMARY KEY AUTOINCREMENT, phone TEXT, body TEXT, time TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMAIL_RECEIVE);

        // Và tạo lại.
        onCreate(db);
    }


    //location
    public void addLocate(Location location){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("latitude", location.getLatitude());
        values.put("longitude", location.getLongitude());
        values.put("time", (new Date()).toString());
        db.insert(TABLE_LOCATE, null, values);
        db.close();
    }

    public List<Location> getLocate(){
        List<Location> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_LOCATE;
        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();
        while(c.moveToNext()){
            Location lc = new Location(c.getString(1), c.getString(2), c.getString(3));
            lc.setId(c.getInt(0));
            list.add(lc);
        }
        db.close();
        return list;
    }

    public void deleteLocate(int[] id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM "+TABLE_LOCATE;
        db.execSQL(query);
        db.close();
    }
    // image
    public void addImage(Image image){
        String[] args = {image.getUrl()};
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("url", image.getUrl());
        contentValues.put("time", (new Date()).toString());
        db.insert(TABLE_IMAGE, null, contentValues);
        db.close();
    }

    public List<Image> getImage(){
        List<Image> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_IMAGE;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while(c.moveToNext()){
            Image img = new Image(c.getString(1), c.getString(2));
            img.setId(c.getInt(0));
            list.add(img);
        }
        return list;
    }

    public void deleteImage(int[] id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM "+TABLE_IMAGE;
        db.execSQL(query);
        db.close();
    }

    //record
    public void addRecord(Record record){
        String[] args = {record.getUrl()};
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("url", record.getUrl());
        contentValues.put("time", (new Date()).toString());
        db.insert(TABLE_RECORD, null, contentValues);
        db.close();
    }

    public List<Record> getRecord(){
        List<Record> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_RECORD;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while(c.moveToNext()){
            Record rc = new Record(c.getString(1), c.getString(2));
            rc.setId(c.getInt(0));
            list.add(rc);
        }
        return list;
    }

    public void deleteRecord(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM "+TABLE_RECORD;
        db.execSQL(query);
        db.close();
    }


    //email receive
    public void addEmailReceive(EmailReceive email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email.getEmail());
        db.insert(TABLE_EMAIL_RECEIVE, null, contentValues);
        db.close();
    }

    public Cursor getEmailReceive(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_EMAIL_RECEIVE;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        return c;
    }

    public void deleteEmailReceive(int[] id){
        String[] args = new String[id.length];
        String numPlaceHolder = "";
        for(int i=0; i<id.length; i++){
            if(i==0){
                numPlaceHolder = "?";
            }else{
                numPlaceHolder+= ", ?";
            }
            args[i] = id[i]+"";
        }
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM "+TABLE_EMAIL_RECEIVE+" WHERE _id IN  ("+numPlaceHolder+")";
        db.execSQL(query, args);
        db.close();
    }

    //sms
    public void addSMS(SMS sms){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone", sms.getPhoneNumber());
        contentValues.put("body", sms.getBody());
        contentValues.put("time", sms.getTimeReceive());
        db.insert(TABLE_SMS, null, contentValues);
        db.close();
    }

    public List<SMS> getListSMS(){
        List<SMS> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_SMS;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while(c.moveToNext()){
            SMS sms = new SMS(c.getString(1), c.getString(2),c.getString(3));
            sms.setId(c.getInt(0));
            list.add(sms);
        }
        return list;
    }

    public void deleteSMS(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM "+TABLE_EMAIL_RECEIVE;
        db.execSQL(query);
        db.close();
    }

}
