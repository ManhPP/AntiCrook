package com.android.findmyandroid.model;

/**
 * Created by tranv on 5/22/2019.
 */

public class EmailReceive {
    private int _id;
    private String email;

    public EmailReceive() {
    }

    public EmailReceive(String email) {
        this.email = email;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
