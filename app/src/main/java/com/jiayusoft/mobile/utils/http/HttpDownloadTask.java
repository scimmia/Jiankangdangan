package com.jiayusoft.mobile.utils.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import com.jiayusoft.mobile.utils.DebugLog;
import com.jiayusoft.mobile.utils.FileUtils;
import com.jiayusoft.mobile.utils.GlobalData;
import com.jiayusoft.mobile.utils.eventbus.BusProvider;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.*;

/**
 * Created by ASUS on 2014/12/29.
 */
public class HttpDownloadTask extends AsyncTask<Void,Integer,String> implements GlobalData {

    ProgressDialog mpDialog;
    Context mContent;
    String msgToShow;

    int mTag;
    String mUrl;
    String mFilePath;

    public HttpDownloadTask(Context mContent, String msgToShow, int mTag, String mUrl, String filePath) {
        this.mContent = mContent;
        this.msgToShow = msgToShow;
        this.mFilePath = filePath;
        this.mTag = mTag;
        this.mUrl = "http://"+
                PreferenceManager.getDefaultSharedPreferences(mContent).getString(serverUrl, defaultServerUrl)
//                defaultServerUrl
                + mUrl;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        mpDialog = new ProgressDialog(mContent, ProgressDialog.THEME_HOLO_LIGHT);
        mpDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置风格为圆形进度条
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
        DebugLog.e("begin download"+mUrl);
        Request request = new Request.Builder().tag(mTag)
                .url(mUrl)
                .build();
        if (request != null){
            OkHttpClient okHttpClient = new OkHttpClient();
            mCall = okHttpClient.newCall(request);
            InputStream input = null;
            OutputStream output = null;
            try {
                Response response = mCall.execute();
                if(response.isSuccessful()){
                    long fileLength = response.body().contentLength();
                    input = response.body().byteStream();
                    File file = new File(mFilePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    FileUtils.createFile(mFilePath);

                    output = new FileOutputStream(mFilePath);

                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            return null;
                        }
                        total += count;
                        // publishing the progress....
                        if (fileLength > 0) // only if total length is known
                            publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }
                    return mFilePath;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
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

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mpDialog.setIndeterminate(false);
        mpDialog.setMax(100);
        mpDialog.setProgress(progress[0]);
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
