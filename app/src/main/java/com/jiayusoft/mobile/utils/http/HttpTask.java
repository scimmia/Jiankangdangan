package com.jiayusoft.mobile.utils.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import com.jiayusoft.mobile.utils.DebugLog;
import com.jiayusoft.mobile.utils.GlobalData;
import com.jiayusoft.mobile.utils.eventbus.BusProvider;
import com.squareup.okhttp.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by ASUS on 2014/12/4.
 */
public class HttpTask extends AsyncTask<Void,Void,String> implements GlobalData {

    ProgressDialog mpDialog;
    Context mContent;
    String msgToShow;

    int httpTpye;
    int mTag;
    String mUrl;
    HashMap<String,String> mBody;

    public HttpTask(Context mContent, String msgToShow, int httpTpye, int mTag, String mUrl, HashMap<String,String> mBody) {
        this.mContent = mContent;
        this.msgToShow = msgToShow;
        this.httpTpye = httpTpye;
        this.mTag = mTag;
        this.mBody = mBody;
        this.mUrl = "http://"+
                PreferenceManager.getDefaultSharedPreferences(mContent).getString(serverUrl, defaultServerUrl)
                + mUrl;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        mpDialog = new ProgressDialog(mContent, ProgressDialog.THEME_HOLO_LIGHT);
//        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条
        mpDialog.setMessage(msgToShow);
        mpDialog.setCancelable(true);

        mpDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                cancel(true);
            }
        });
        mpDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        Request request = null;
        switch (httpTpye){
            case httpGet:
                request = new Request.Builder().tag(mTag)
                        .url(mUrl)
                        .build();
                break;
            case  httpPost:
                StringBuilder stringBuilder = new StringBuilder("");
                if (mBody!=null){
                    Set<String> keys = mBody.keySet();
                    for (String key : keys){
                        String value = mBody.get(key);
                        if (StringUtils.isNotEmpty(value)){
                            stringBuilder.append(key).append('=').append(value).append('&');
                        }
                    }
                }
                String content = StringUtils.removeEnd(stringBuilder.toString(),"&");
                DebugLog.e(content);
                RequestBody formBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),content);
//                FormEncodingBuilder f = new FormEncodingBuilder();
//                if (mBody!=null){
//                    Set<String> keys = mBody.keySet();
//                    for (String key : keys){
//                        String value = mBody.get(key);
//                        if (StringUtils.isNotEmpty(value)){
//                            f.add(key,value);
//                        }
//                    }
//                }
//
//                RequestBody formBody = f.build();
                request = new Request.Builder().tag(mTag)
                        .url(mUrl)
                        .post(formBody)
                        .build();
                break;
        }
        DebugLog.e(mUrl);
        if (request != null){
            OkHttpClient okHttpClient = new OkHttpClient();
            try {
                mCall = okHttpClient.newCall(request);
                Response response = mCall.execute();
                if(response.isSuccessful()){
                    String temp = response.body().string();
                    DebugLog.e(temp);
                    return temp;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mpDialog.dismiss();
        BusProvider.getInstance().post(new HttpEvent(mTag,result));
    }

    Call mCall;
    @Override
    protected void onCancelled() {
        super.onCancelled();
        DebugLog.e("onCancelled");
        if (mCall!=null){
            mCall.cancel();
        }
    }
}
