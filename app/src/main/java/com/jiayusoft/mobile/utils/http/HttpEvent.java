package com.jiayusoft.mobile.utils.http;

/**
 * Created by ASUS on 2014/12/4.
 */
public class HttpEvent {
    int tag;
    String response;

    public HttpEvent(int tag, String response) {
        this.tag = tag;
        this.response = response;
    }

    public int getTag() {
        return tag;
    }

    public String getResponse() {
        return response;
    }
}
