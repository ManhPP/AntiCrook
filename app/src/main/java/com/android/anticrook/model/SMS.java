package com.android.anticrook.model;

/**
 * Created by tranv on 5/23/2019.
 */

public class SMS {
    private int id;
    private String body;
    private String timeReceive;
    private String phoneNumber;
    private String time;

    public SMS() {
    }

    public SMS(String body, String timeReceive, String phoneNumber) {
        this.body = body;
        this.timeReceive = timeReceive;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTimeReceive() {
        return timeReceive;
    }

    public void setTimeReceive(String timeReceive) {
        this.timeReceive = timeReceive;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
