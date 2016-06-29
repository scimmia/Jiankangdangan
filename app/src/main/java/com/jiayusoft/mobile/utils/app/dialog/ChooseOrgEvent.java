package com.jiayusoft.mobile.utils.app.dialog;

/**
 * Created by ASUS on 2014/12/16.
 */
public class ChooseOrgEvent {
    String title;
    String id;

    public ChooseOrgEvent(String title, String id) {
        this.title = title;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }
}
