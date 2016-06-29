package com.jiayusoft.mobile.utils.app.cardview;

/**
 * Created by ASUS on 2014/12/2.
 */
public class CardEvent {
    public static final int cardClickEvent = 1;
    public static final int cardImageEvent = 2;

    int position;
    int eventType;

    public CardEvent(int position, int eventType) {
        this.position = position;
        this.eventType = eventType;
    }

    public int getPosition() {
        return position;
    }

    public int getEventType() {
        return eventType;
    }
}
