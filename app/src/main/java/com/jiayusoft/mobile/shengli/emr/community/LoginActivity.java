package com.jiayusoft.mobile.shengli.emr.community;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayusoft.mobile.shengli.emr.community.beans.UpdateInfo;
import com.jiayusoft.mobile.shengli.emr.community.beans.UserCommunity;
import com.jiayusoft.mobile.utils.DebugLog;
import com.jiayusoft.mobile.utils.app.BaseActivity;
import com.jiayusoft.mobile.utils.app.listener.HideKeyboardListener;
import com.jiayusoft.mobile.utils.http.BaseResponse;
import com.jiayusoft.mobile.utils.http.HttpDownloadTask;
import com.jiayusoft.mobile.utils.http.HttpEvent;
import com.jiayusoft.mobile.utils.http.HttpTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;


public class LoginActivity extends BaseActivity {

    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_login);
    }

    @InjectView(R.id.login_et_username)
    MaterialEditText mLoginEtUsername;
    @InjectView(R.id.login_et_password)
    MaterialEditText mLoginEtPassword;
    @InjectView(R.id.login_btn_sign)
    Button mLoginBtnSignOnline;
    @InjectView(R.id.login_layout)
    LinearLayout mLoginLayout;
    @InjectView(R.id.login_cb_save_password)
    CheckBox mLoginCbSavePassword;
    @InjectView(R.id.login_cb_auto_login)
    CheckBox mLoginCbAutoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginLayout.setOnClickListener(new HideKeyboardListener(getBaseActivity()));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseActivity());
        mLoginEtUsername.setText(sharedPreferences.getString(LOGIN_USER_NAME, null));
        mLoginEtPassword.setText(sharedPreferences.getString(LOGIN_PASSWORD, null));
        mLoginCbSavePassword.setChecked(sharedPreferences.getBoolean(LOGIN_SAVE_PASSWORD, false));
        mLoginCbAutoLogin.setChecked(sharedPreferences.getBoolean(loginAutoLogin, false));

        if (sharedPreferences.getString(serverUrl,null)==null){
            SharedPreferences.Editor spEd = sharedPreferences.edit();
            spEd.putString(serverUrl, defaultServerUrl);
            spEd.apply();
        }
        if (sharedPreferences.getBoolean(loginAutoLogin, false)){
            attemptLogin();
        }
    }

    @OnClick(R.id.login_btn_sign)
    public void attemptLogin() {
        if (TextUtils.isEmpty(mLoginEtUsername.getText())) {
            mLoginEtUsername.setError(getString(R.string.error_field_required));
            mLoginEtUsername.requestFocus();
        } else {
            mLoginEtUsername.setError(null);
            mLoginEtPassword.setError(null);

            checkUpdate();
        }
    }

    @OnCheckedChanged(R.id.login_cb_auto_login)
    public void onAutoLogin(boolean checked){
        if (checked && !mLoginCbSavePassword.isChecked()){
            mLoginCbSavePassword.toggle();
        }
    }

    @Subscribe
    public void onHttpEvent(HttpEvent event) {
        if (event == null || StringUtils.isEmpty(event.getResponse())) {
            showMessage(defaultNetErrorMsg);
        } else {
            int tag = event.getTag();
            switch (tag) {
                case tagLogin:
                    DebugLog.e(event.getResponse());
                    BaseResponse<UserCommunity> response = new Gson().fromJson(event.getResponse(), new TypeToken<BaseResponse<UserCommunity>>() {
                    }.getType());
                    switch (response.getErrorCode()) {
                        case 0:
                            UserCommunity user = response.getData();
                            user.setPassword(mLoginEtPassword.getText().toString());
                            BaseApplication.setCurrentUser(user);
                            startMainActivity();
                            break;
                        default:
                            String msg = response.getErrorMsg();
                            if (StringUtils.isEmpty(msg)) {
                                msg = "网络连接错误，请稍后重试。";
                            }
                            showMessage(msg);
                            break;
                    }
                    break;
                case tagCheckUpdate:
                    DebugLog.e(event.getResponse());
                    final BaseResponse<UpdateInfo> responseCheckUpdate = new Gson().fromJson(event.getResponse(), new TypeToken<BaseResponse<UpdateInfo>>() {
                    }.getType());
                    switch (responseCheckUpdate.getErrorCode()) {
                        case 0:
                            new AlertDialog.Builder(getBaseActivity())
                                    .setTitle("升级信息提示")
                                    .setMessage("发现新版本："+responseCheckUpdate.getData().getVersionName()
                                            +"\n更新内容："+responseCheckUpdate.getData().getUpdateLog())
                                    .setNegativeButton("现在升级", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            new HttpDownloadTask(
                                                    getBaseActivity(),"下载中...",tagDownloadNewFile,
                                                    responseCheckUpdate.getData().getSoftUrl(),
                                                    updateFolder+"EHR.apk").execute();
                                        }
                                    })
                                    .setPositiveButton("以后再说",null)
                                    .setCancelable(false)
                                    .show();
                            break;
                        default:
                            login();
                            break;
                    }
                    break;
                case tagDownloadNewFile:
                    if (StringUtils.isNotEmpty(event.getResponse())) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(event.getResponse())), "application/vnd.android.package-archive");
                        startActivity(intent);
                    }
                    break;
            }
        }
    }

    void startMainActivity() {
        DebugLog.e("startMainActivity");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseActivity());
        SharedPreferences.Editor spEd = sp.edit();
        spEd.putString(LOGIN_USER_NAME, mLoginEtUsername.getText().toString());
        if (mLoginCbSavePassword.isChecked()) {
            spEd.putBoolean(LOGIN_SAVE_PASSWORD, true);
            spEd.putString(LOGIN_PASSWORD, mLoginEtPassword.getText().toString());
        } else {
            spEd.remove(LOGIN_SAVE_PASSWORD);
            spEd.remove(LOGIN_PASSWORD);
        }
        spEd.putBoolean(loginAutoLogin, mLoginCbAutoLogin.isChecked());
        spEd.apply();
//            BaseApplication.setCurrentCustomer(new Customer("王五","123","sdf","","",""));
//            Intent intent = new Intent(getActivity(), MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//            startActivity(intent);
        beginActivity(MainActivity.class);
        finish();
    }

    void checkUpdate(){
        HashMap<String, String> formBody = new HashMap<String, String>();
        String versionTemp = "0";
        try {
            PackageManager pm = getBaseActivity().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getBaseActivity().getPackageName(), 0);
            versionTemp = pi.versionCode+"";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        formBody.put(versionCode, versionTemp);
        formBody.put(softName, defaultSoftName);
        new HttpTask(getBaseActivity(), "检查更新...", httpPost, tagCheckUpdate, checkUpdateUrl, formBody).execute();
    }

    void login(){
        String userName = mLoginEtUsername.getText().toString();
        String password = mLoginEtPassword.getText().toString();

        HashMap<String, String> formBody = new HashMap<String, String>();
        formBody.put(loginUserID, userName);
        formBody.put(loginPassword, password);
        new HttpTask(getBaseActivity(), "登陆中...", httpPost, tagLogin, loginCommunity, formBody).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            beginActivity(SettingActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
