package com.android.findmyandroid.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.android.findmyandroid.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tranv on 5/23/2019.
 */

public class ContactHandler {
    private Context context;

    public ContactHandler(Context context){
        this.context = context;
    }

    public List<Contact> getAllContact(){
        List<Contact> listContact = new ArrayList<>();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        listContact.clear();
        while (cursor.moveToNext()){
            int idName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int idPhone = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            String name = cursor.getString(idName);
            String phoneNumber = cursor.getString(idPhone);
            Contact c = new Contact(name, phoneNumber);
            listContact.add(c);
        }
        return listContact;
    }
}
