package com.jiayusoft.mobile.shengli.emr.community;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import butterknife.InjectView;
import com.jiayusoft.mobile.shengli.emr.community.bingan.BinganListActivity;
import com.jiayusoft.mobile.shengli.emr.community.selfupload.SelfuploadListActivity;
import com.jiayusoft.mobile.shengli.emr.community.selfupload.mydescribe.UploadMyDescribeActivity;
import com.jiayusoft.mobile.utils.DebugLog;
import com.jiayusoft.mobile.utils.app.BaseActivity;
import com.jiayusoft.mobile.utils.app.clientinfo.ClientinfoAdapter;
import com.jiayusoft.mobile.utils.app.clientinfo.ClientinfoItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;


public class MainActivity extends BaseActivity {


    ArrayList<ClientinfoItem> iconItems;
    ClientinfoAdapter adapter;

    @InjectView(R.id.grid_main)
    GridView mGridMain;
    @InjectView(R.id.logo_img)
    ImageView mLogoImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLogoImg.setImageResource(R.drawable.logo_sph);
        String logoName = BaseApplication.getCurrentUser().getLogoName();
        if (StringUtils.isNotEmpty(logoName)){
            ImageLoader.getInstance().displayImage("http://"
                    +PreferenceManager.getDefaultSharedPreferences(getBaseActivity()).getString(serverUrl, defaultServerUrl)
                    +String.format(logoImgUrl,logoName),mLogoImg);
        }
        iconItems = new ArrayList<ClientinfoItem>();
        iconItems.add(new ClientinfoItem(BinganListActivity.class, R.drawable.icon_main_chayuebingan, R.string.main_chayuebingan));
        iconItems.add(new ClientinfoItem(UploadMyDescribeActivity.class, R.drawable.icon_main_upload_ehr, R.string.title_activity_upload_ehr));
        iconItems.add(new ClientinfoItem(SelfuploadListActivity.class, R.drawable.icon_main_list_ehr, R.string.title_activity_mydescribe_list));

        iconItems.add(new ClientinfoItem(NotYetActivity.class,R.drawable.ic_main_notyet,R.string.title_activity_healthcheck_detail));
        iconItems.add(new ClientinfoItem(SelfuploadListActivity.class, R.drawable.icon_main_healthcheck, R.string.title_activity_healthcheck_list));

        iconItems.add(new ClientinfoItem(SettingActivity.class,R.drawable.icon_main_setting,R.string.main_shezhi));
        adapter = new ClientinfoAdapter(getBaseActivity(), iconItems);
        mGridMain.setAdapter(adapter);
        mGridMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DebugLog.e("position:" + position + iconItems.get(position).toString());
                Bundle bundle = new Bundle();
                bundle.putInt(itemType, iconItems.get(position).getmTitleId());
                beginActivity(iconItems.get(position).getLaunche(), bundle);
            }
        });
    }

    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_main);
    }


    private static long back_pressed;
    @Override
    public void onBackPressed()
    {
        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            showMessage("再按一次退出移动病案!");
        back_pressed = System.currentTimeMillis();
    }
}
