package com.jiayusoft.mobile.utils.app.listener;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created with IntelliJ IDEA.
 * User: ASUS
 * Date: 13-11-11
 * Time: 下午1:42
 * To change this template use File | Settings | File Templates.
 */
public class HideKeyboardListener implements View.OnClickListener {
    Activity activity;

    public HideKeyboardListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null) {
            manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
