package com.jiayusoft.mobile.utils.app;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ASUS on 2015/2/5.
 */
public class LockableDrawerLayout extends DrawerLayout {
    private boolean isLocked;

    public LockableDrawerLayout(Context context) {
        super(context);
        isLocked = false;
    }

    public LockableDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        isLocked = false;
    }

    public LockableDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        isLocked = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isLocked) {
            try {
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (!isLocked) {
//            return super.onTouchEvent(event);
//        }
//        return false;
//    }

    public void toggleLock() {
        isLocked = !isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public boolean isLocked() {
        return isLocked;
    }

}
