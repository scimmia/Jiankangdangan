package com.jiayusoft.mobile.utils.eventbus.event;


/**
 * Created by ASUS on 2014/7/22.
 */
public class MessageEvent {
    String message;
    int messageType;

    public static final int STYLE_INFO = 1;
    public static final int STYLE_CONFIRM = 2;
    public static final int STYLE_ALERT = 3;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public MessageEvent(String message, int messageType) {

        this.message = message;
        this.messageType = messageType;
    }

    public MessageEvent(String message) {

        this.message = message;
        this.messageType = STYLE_INFO;
    }
}
