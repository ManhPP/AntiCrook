package com.android.anticrook.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.android.anticrook.model.Contact;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tranv on 5/23/2019.
 */

public class ContactHandler {
    private Context context;

    //lấy đối tượng context từ hàm khởi tạo
    public ContactHandler(Context context){
        this.context = context;
    }

    // hàm lấy tất cả các liên lạc trong danh bạ
    public List<Contact> getAllContact(){
        List<Contact> listContact = new ArrayList<>();
        //đọc bảng lưu các liên hệ trong danh bạ và lấy ra đối tượng cursor để duyệt
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        listContact.clear();
        while (cursor.moveToNext()){
            //lấy ra số thứ tự của code lưu tên liên lạc
            int idName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            //lấy ra số thứ tự của cột lưu sđt liên lạc
            int idPhone = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            //đọc và khởi tạo đối tượng contact
            String name = cursor.getString(idName);
            String phoneNumber = cursor.getString(idPhone);
            Contact c = new Contact(name, phoneNumber);
            //thêm trường thời gian đọc cho đối tượng contact
            c.setTime((new Date()).toString());
            //add đối tượng contact vào list
            listContact.add(c);
        }
        return listContact;
    }
}
