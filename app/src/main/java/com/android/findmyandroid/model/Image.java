package com.android.findmyandroid.model;

/**
 * Created by tranv on 5/22/2019.
 */

public class Image {
    private int id;
    private String url;
    private String time;

    public Image() {
    }

    public Image(String url, String time) {
        this.url = url;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
