package com.jiayusoft.mobile.shengli.emr.community;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.Optional;
import com.jiayusoft.mobile.utils.app.BaseActivity;
import com.rengwuxian.materialedittext.MaterialEditText;
import org.apache.commons.lang3.StringUtils;


public class SettingActivity extends BaseActivity {

    @InjectView(R.id.server_address)
    MaterialEditText mServerAddress;
    @InjectView(R.id.sw_autosave)
    Switch mSwAutosave;
    @InjectView(R.id.rb_diy)
    RadioButton rbDiy;
    @InjectView(R.id.rg_server)
    RadioGroup rgServer;
    @InjectView(R.id.rb_default)
    RadioButton rbDefault;
    @InjectView(R.id.rb_inner)
    RadioButton rbInner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String address = PreferenceManager.getDefaultSharedPreferences(getBaseActivity())
                .getString(serverUrl, null);
        if (StringUtils.equals(address, defaultServerUrl)) {
            rgServer.check(R.id.rb_default);
        } else if (StringUtils.equals(address, innerServerUrl)) {
            rgServer.check(R.id.rb_inner);
        } else {
            rgServer.check(R.id.rb_diy);
            mServerAddress.setVisibility(View.VISIBLE);
            mServerAddress.setText(address);
        }
        mSwAutosave.setChecked(PreferenceManager.getDefaultSharedPreferences(getBaseActivity()).getBoolean(loginAutoLogin, false));
    }

    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_setting);
    }

    @Optional
    @OnCheckedChanged({R.id.rb_diy})
    public void diyServer(boolean checked) {
        if (checked) {
            mServerAddress.setVisibility(View.VISIBLE);
        } else {
            mServerAddress.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseActivity());
            SharedPreferences.Editor spEd = sp.edit();
            switch (rgServer.getCheckedRadioButtonId()){
                case R.id.rb_default:
                    spEd.putString(serverUrl, defaultServerUrl);
                    break;
                case R.id.rb_inner:
                    spEd.putString(serverUrl, innerServerUrl);
                    break;
                case R.id.rb_diy:
                    spEd.putString(serverUrl, mServerAddress.getText().toString());
                    break;
            }
            spEd.putBoolean(loginAutoLogin, mSwAutosave.isChecked());

            spEd.apply();
            showMessage("保存成功");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
