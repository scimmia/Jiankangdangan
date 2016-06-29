package com.jiayusoft.mobile.shengli.emr.community.selfupload.mydescribe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import butterknife.OnPageChange;
import com.devspark.appmsg.AppMsg;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayusoft.mobile.shengli.emr.community.R;
import com.jiayusoft.mobile.shengli.emr.community.selfupload.SelfUpload;
import com.jiayusoft.mobile.utils.DebugLog;
import com.jiayusoft.mobile.utils.app.BaseActivity;
import com.jiayusoft.mobile.utils.http.BaseResponse;
import com.jiayusoft.mobile.utils.http.HttpEvent;
import com.jiayusoft.mobile.utils.http.HttpTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.otto.Subscribe;
import org.apache.commons.lang3.StringUtils;
import uk.co.senab.photoview.PhotoView;

import java.util.ArrayList;
import java.util.HashMap;

public class MyDescribeDetailActivity extends BaseActivity {

    @InjectView(R.id.ehr_pager)
    com.jiayusoft.mobile.utils.app.HackyViewPager pager;

    ArrayList<String> mItemsUrlList;
    int mCurrentPage;

    ArrayList<String> mDeleteFileList;

    String mURLHeader;
    SelfUpload mMyDescribe;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mURLHeader = "http://" +
                PreferenceManager.getDefaultSharedPreferences(getBaseActivity()).getString(serverUrl, defaultServerUrl);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_detail_loading)
                .showImageForEmptyUri(R.drawable.ic_ehr_detail_empty)
                .showImageOnFail(R.drawable.ic_ehr_detail_empty)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        mCurrentPage = 0;
        mDeleteFileList = new ArrayList<String>();
        mItemsUrlList = new ArrayList<String>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String temp = bundle.getString("mydescribe");
            if (temp != null) {
                mMyDescribe = new Gson().fromJson(temp,SelfUpload.class);
                mItemsUrlList.addAll(mMyDescribe.getFileNames());
            }
        }
        mPagerAdapter = new SamplePagerAdapter();
        pager.setAdapter(mPagerAdapter);
        if(mItemsUrlList.size()<=0){
            showMessage("没有附加图片！", AppMsg.STYLE_ALERT);
        }else{
            onPageSelected(mCurrentPage);
        }
    }


    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_mydescribe_detail);
    }

    @OnPageChange(R.id.ehr_pager)
    void onPageSelected(int position) {
        DebugLog.e("onPageSelected:"+position);
        if (mItemsUrlList.size() > 0) {
            mCurrentPage = position;
            getSupportActionBar().setSubtitle(String.format("第 %d/%d 张", mCurrentPage + 1, mItemsUrlList.size()));
        }else{
            getSupportActionBar().setSubtitle(null);
        }
    }

    SamplePagerAdapter mPagerAdapter;
    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mItemsUrlList.size();
        }

        private int mChildCount = 0;

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object)   {
            if ( mChildCount > 0) {
                mChildCount --;
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            ImageLoader.getInstance().displayImage(
                    mURLHeader
                            +String.format(mydescribeDetailPhotoUrl,
                                StringUtils.substring(mMyDescribe.getIdCard(),0,6),
                            mMyDescribe.getIdCard(),
                            StringUtils.substring(mMyDescribe.getUploadTime(),0,10),
                            mItemsUrlList.get(position)),
//                            "http://11.0.0.55:8880/mobile"
//                            + "Doc/ehr/"
//                            +StringUtils.substring(mMyDescribe.getIdCard(),0,6)+'/'
//                            +mMyDescribe.getIdCard()+'/'
//                            +mItemsUrlList.get(position),
                    photoView, options);
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Subscribe
    public void onHttpEvent(HttpEvent event) {
        if (event == null || StringUtils.isEmpty(event.getResponse())) {
            showMessage("网络连接错误，请稍后重试。");
        } else {
            int tag = event.getTag();
            switch (tag) {
                case tagMyDescribeDetailDelete:
                    BaseResponse<String> responseDelete = new Gson().fromJson(event.getResponse(), new TypeToken<BaseResponse<String>>() {
                    }.getType());
                    switch (responseDelete.getErrorCode()){
                        case 0:
                            showMessage(responseDelete.getData());
                            mDeleteFileList.add(mItemsUrlList.remove(mCurrentPage));
                            mPagerAdapter.notifyDataSetChanged();
                            if (mItemsUrlList.size()>0){
                                if (mCurrentPage <= mItemsUrlList.size()){
                                    getSupportActionBar().setSubtitle(String.format("第 %d/%d 张", mCurrentPage + 1, mItemsUrlList.size()));
                                }
                            }else{
                                new AlertDialog.Builder(getBaseActivity())
                                        .setTitle("提示")
                                        .setMessage("已无附加图片，将返回到自述列表。")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                onBackPressed();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                            break;
                        default:
                            String msg = responseDelete.getErrorMsg();
                            if (StringUtils.isEmpty(msg)) {
                                msg = "网络连接错误，请稍后重试。";
                            }
                            showMessage(msg);
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ehr_detail, menu);
        MenuItem menuItemLock = menu.findItem(R.id.action_lock);
        if (menuItemLock != null && pager.isLocked()) {
            menuItemLock.setTitle(R.string.menu_unlock);
            menuItemLock.setIcon(R.drawable.ic_lock_on);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_lock:
                DebugLog.e("action_lock");
                pager.toggleLock();
                boolean isLocked = pager.isLocked();
                String title = (isLocked) ? getString(R.string.menu_unlock) : getString(R.string.menu_lock);
                item.setTitle(title);
                item.setIcon((isLocked) ? R.drawable.ic_lock_on:R.drawable.ic_lock_off);
                break;
            case R.id.action_delete:
                mCurrentPage = pager.getCurrentItem();
                if (mCurrentPage>=0) {
                    new AlertDialog.Builder(getBaseActivity())
                            .setTitle("提示")
                            .setMessage("将从服务器上删除该图片，是否确定？")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deletePhoto(mCurrentPage);
                                }
                            })
                            .show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void deletePhoto(int currentPage){
        HashMap<String, String> mFormBody = new HashMap<String, String>();
        mFormBody.put("idcard", mMyDescribe.getIdCard());
        mFormBody.put("serialnum", mMyDescribe.getSerialNum());
        mFormBody.put("uploaddate", StringUtils.substring(mMyDescribe.getUploadTime(), 0, 10));
        mFormBody.put("uploadtype", ""+mMyDescribe.getUploadType());
        mFormBody.put("filename", mItemsUrlList.get(currentPage));
        new HttpTask(getBaseActivity(), "正在删除...", httpPost,
                tagMyDescribeDetailDelete, selfDetailDeleteUrl, mFormBody)
                .execute();
    }

    @Override
    public void onBackPressed()
    {
        if (mDeleteFileList!=null && mDeleteFileList.size()>0){
            Bundle bundle = new Bundle();
            bundle.putString("serialNum", mMyDescribe.getSerialNum());
            bundle.putStringArrayList("deleteFiles",mDeleteFileList);
            Intent it = new Intent();
            it.putExtras(bundle);
            setResult(RESULT_OK, it);
        }
        super.onBackPressed();
    }
}
