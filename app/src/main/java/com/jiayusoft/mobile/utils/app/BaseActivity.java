package com.jiayusoft.mobile.utils.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import butterknife.ButterKnife;
import com.devspark.appmsg.AppMsg;
import com.jiayusoft.mobile.shengli.emr.community.BaseApplication;
import com.jiayusoft.mobile.shengli.emr.community.R;
import com.jiayusoft.mobile.utils.GlobalData;
import com.jiayusoft.mobile.utils.eventbus.BusProvider;
import com.jiayusoft.mobile.utils.eventbus.event.MessageEvent;
import com.squareup.otto.Subscribe;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by ASUS on 2014/11/14.
 */
public abstract class BaseActivity extends ActionBarActivity implements GlobalData {
    public Activity getBaseActivity(){
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView();
//        getSupportActionBar().setIcon(R.drawable.ic_launcher);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_actionbar_sph));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String logoColor = BaseApplication.getCurrentUser().getLogoColor();
        if (StringUtils.isNotEmpty(logoColor)){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(logoColor)));
        }
        ButterKnife.inject(this);
    }

    protected abstract void initContentView();

    protected Object showMessageListener;
    @Override
    public void onResume() {
        super.onResume();

        // Register ourselves so that we can provide the initial value.
        BusProvider.getInstance().register(this);
        showMessageListener = new Object() {
            @Subscribe
            public void showMessage(final MessageEvent event) {
                BaseActivity.this.showMessage(event);
            }
        };
        BusProvider.getInstance().register(showMessageListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelMessage();
        cancelProgress();
        // Always unregister when an object no longer should be on the bus.
        BusProvider.getInstance().unregister(this);
        BusProvider.getInstance().unregister(showMessageListener);
    }


    private AppMsg appMsg;
    public void showMessage(MessageEvent event){
        switch (event.getMessageType()){
            case MessageEvent.STYLE_ALERT:
                showMessage(event.getMessage(), AppMsg.STYLE_ALERT);
                break;
            case MessageEvent.STYLE_CONFIRM:
                showMessage(event.getMessage(), AppMsg.STYLE_CONFIRM);
                break;
            case MessageEvent.STYLE_INFO:
                showMessage(event.getMessage(), AppMsg.STYLE_INFO);
                break;
        }
    }

    public void showMessage(String message){
        showMessage(message, AppMsg.STYLE_INFO);
    }
    public void showMessage(String message, AppMsg.Style style){
        cancelMessage();
        appMsg = AppMsg.makeText(getBaseActivity(), message, style);
        appMsg.setAnimation(android.R.anim.fade_in, android.R.anim.fade_out);
        appMsg.show();

    }
    public void cancelMessage(){
        if (appMsg!=null && appMsg.isShowing()){
            appMsg.cancel();
        }
    }

    public ProgressDialog mProgressDialog;
    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(String message) {
        cancelMessage();
        if (mProgressDialog == null){
            mProgressDialog = new ProgressDialog(getBaseActivity(), ProgressDialog.THEME_HOLO_LIGHT);
        }
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }
    public void cancelProgress(){
        if (mProgressDialog!=null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }


    public void beginActivity(Class<?> launche){
        beginActivity(launche,null);
    }
    public void beginActivity(Class<?> launche, Bundle bundle){
        Intent intent = new Intent( getBaseActivity(), launche);
        if (bundle!=null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
    public void beginActivityForResult(Class<?> launche, int requestCode){
        beginActivityForResult(launche,requestCode,null);
    }
    public void beginActivityForResult(Class<?> launche, int requestCode, Bundle bundle){
        Intent intent = new Intent( getBaseActivity(), launche);
        if (bundle!=null){
            intent.putExtras(bundle);
        }
        startActivityForResult(intent,requestCode);
    }
}
