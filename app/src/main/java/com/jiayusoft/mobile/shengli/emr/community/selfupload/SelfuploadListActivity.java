package com.jiayusoft.mobile.shengli.emr.community.selfupload;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayusoft.mobile.shengli.emr.community.BaseApplication;
import com.jiayusoft.mobile.shengli.emr.community.R;
import com.jiayusoft.mobile.shengli.emr.community.selfupload.healthcheck.HealthCheckDetailActivity;
import com.jiayusoft.mobile.shengli.emr.community.selfupload.mydescribe.MyDescribeDetailActivity;
import com.jiayusoft.mobile.utils.DebugLog;
import com.jiayusoft.mobile.utils.FileUtils;
import com.jiayusoft.mobile.utils.app.BaseActivity;
import com.jiayusoft.mobile.utils.app.cardview.CardAdapter;
import com.jiayusoft.mobile.utils.app.cardview.CardEvent;
import com.jiayusoft.mobile.utils.app.cardview.CardItem;
import com.jiayusoft.mobile.utils.http.BaseResponse;
import com.jiayusoft.mobile.utils.http.HttpDownloadTask;
import com.jiayusoft.mobile.utils.http.HttpEvent;
import com.jiayusoft.mobile.utils.http.HttpTask;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.squareup.otto.Subscribe;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelfuploadListActivity extends BaseActivity {

    @InjectView(R.id.list_healthcheck)
    ListView listHealthcheck;
    @InjectView(android.R.id.empty)
    TextView empty;

    Button mLoadmore;

    ArrayList<CardItem> mCardItems;
    CardAdapter mCardAdapter;

    String mFileNeedDelete;

    int mUploadType;
    int mSelectedPosition;
    SelfUpload mSelectedItem;
    void initSelect(int position){
        if (position>=0 && position<mCardItems.size()){
            mSelectedPosition = position;
            mSelectedItem = (SelfUpload)mCardItems.get(mSelectedPosition);
        }else {
            mSelectedPosition = -1;
            mSelectedItem = null;
        }
    }
    boolean isSelected(){
        return mSelectedItem!=null;
    }

    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_healthcheck_list);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int tempType = bundle.getInt(itemType, 0);
            getSupportActionBar().setTitle(tempType);
            switch (tempType){
                case R.string.title_activity_mydescribe_list:
                    mUploadType = CardItem.typeMyDescribe;
                    break;
                case R.string.title_activity_healthcheck_list:
                    mUploadType = CardItem.typeHealthCheck;
                    break;
                default:
                    break;
            }
        }

        mCardItems = new ArrayList<CardItem>();
        mCardAdapter = new CardAdapter(getBaseActivity(), mCardItems);
        mLoadmore = new Button(getBaseActivity());
        mLoadmore.setText(R.string.loadmore);
        mLoadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore();
            }
        });
        listHealthcheck.addFooterView(mLoadmore);
        listHealthcheck.setEmptyView(empty);
        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(mCardAdapter);
        animationAdapter.setAbsListView(listHealthcheck);
        listHealthcheck.setAdapter(animationAdapter);

        initSelect(-1);
        loadMore();
    }

    void loadMore() {
        String url = String.format(healthcheckLoadMoreUrl,
                BaseApplication.getCurrentUser().getOrgCode(),
                BaseApplication.getCurrentUser().getIdcard(),
                mUploadType,mCardItems.size());
        new HttpTask(getBaseActivity(), "查询中...", httpGet, tagHealthcheckLoadMore, url, null)
                .execute();
    }

    void openHealthCheck(String fileName){
        Bundle bundle = new Bundle();
        bundle.putString("serialNum", mSelectedItem.getSerialNum());
        bundle.putString("fileName", fileName);
        beginActivityForResult(HealthCheckDetailActivity.class, deleteHealthcheckDetail, bundle);
    }

    @Subscribe
    public void onCardEvent(CardEvent event) {
        initSelect(event.getPosition());
        switch (event.getEventType()) {
            case CardEvent.cardClickEvent:
                switch (mUploadType){
                    case CardItem.typeMyDescribe:
                        if(isSelected() && mSelectedItem.getFileCount()>0) {
                            Bundle bundle = new Bundle();
                            bundle.putString("mydescribe", new Gson().toJson(mSelectedItem));
                            beginActivityForResult(MyDescribeDetailActivity.class, deleteMyDescribeDetail, bundle);
                        }else{
                            showMessage("该自述无附加图片。");
                        };
                        break;
                    case CardItem.typeHealthCheck:
                        if(isSelected() && mSelectedItem.getFileCount()>0) {
                            String[] filesTemp = new String[mSelectedItem.getFileCount()];
                            for (int i=1;i<=mSelectedItem.getFileCount();i++){
                                filesTemp[i-1] = "报告文件-"+i;
                            }
                            new AlertDialog.Builder(getBaseActivity())
                                    .setTitle("选择报告文件")
                                    .setSingleChoiceItems(filesTemp, -1, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String fileName = mSelectedItem.getFileName(which);
                                            File targeFile = new File(healthCheckFolder+mSelectedItem.getSerialNum()+ File.separator+fileName);
                                            if (targeFile.exists()){
                                                openHealthCheck(fileName);
                                            }else{
                                                new HttpDownloadTask(
                                                        getBaseActivity(),"正在加载...",tagHealthcheckDetailDownload,
                                                        String.format(healthcheckDetailDownloadUrl,
                                                                StringUtils.substring(mSelectedItem.getIdCard(),0,6),
                                                                mSelectedItem.getIdCard(),
                                                                StringUtils.substring(mSelectedItem.getUploadTime(),0,10),
                                                                fileName),
                                                        healthCheckFolder+mSelectedItem.getSerialNum()+ File.separator+fileName)
                                                        .execute();
                                            }
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }else{
                            showMessage("该检查无附加文件。");
                        }
                        break;
                    default:
                        break;
                }
                break;
            case CardEvent.cardImageEvent:
                new AlertDialog.Builder(getBaseActivity())
                        .setTitle("提示")
                        .setMessage("将从服务器上删除该记录及文件，是否确定？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (isSelected()) {
                                    HashMap<String, String> mFormBody = new HashMap<String, String>();
                                    mFormBody.put("idcard", mSelectedItem.getIdCard());
                                    mFormBody.put("serialnum", mSelectedItem.getSerialNum());
                                    mFormBody.put("uploaddate", StringUtils.substring(mSelectedItem.getUploadTime(), 0, 10));
                                    mFormBody.put("uploadtype", ""+mSelectedItem.getUploadType());
                                    new HttpTask(getBaseActivity(), "正在删除...", httpPost,
                                            tagselfDelete, selfDeleteUrl, mFormBody)
                                            .execute();
                                }
                            }
                        })
                        .show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case deleteHealthcheckDetail:
                    if (isSelected()){
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            mFileNeedDelete = bundle.getString("filename");
                            if (mSelectedItem.getFileNames().contains(mFileNeedDelete)){
                                HashMap<String, String> mFormBody = new HashMap<String, String>();
                                mFormBody.put("idcard", mSelectedItem.getIdCard());
                                mFormBody.put("serialnum", mSelectedItem.getSerialNum());
                                mFormBody.put("uploaddate", StringUtils.substring(mSelectedItem.getUploadTime(), 0, 10));
                                mFormBody.put("uploadtype", ""+mSelectedItem.getUploadType());
                                mFormBody.put("filename", mFileNeedDelete);
                                new HttpTask(getBaseActivity(), "正在删除...", httpPost,
                                        tagHealthcheckDetailDelete, selfDetailDeleteUrl, mFormBody)
                                        .execute();
                            }
                        }
                    }
                    break;
                case deleteMyDescribeDetail:
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        ArrayList<String> deleteFiles = bundle.getStringArrayList("deleteFiles");
                        if (deleteFiles!=null){
                            for (String deleteFile:deleteFiles){
                                mSelectedItem.getFileNames().remove(deleteFile);
                            }
                            mCardAdapter.notifyDataSetChanged();
                            initSelect(-1);
                            break;
                        }
                    }
                    break;
            }
        }
    }

    @Subscribe
    public void onHttpEvent(HttpEvent event) {
        if (event == null || StringUtils.isEmpty(event.getResponse())) {
            showMessage("网络连接错误，请稍后重试。");
        } else {
            int tag = event.getTag();
            DebugLog.e(event.getResponse());
            switch (tag) {
                case tagHealthcheckLoadMore:
                    BaseResponse<List<SelfUpload>> response = new Gson().fromJson(event.getResponse(), new TypeToken<BaseResponse<List<SelfUpload>>>() {
                    }.getType());
                    switch (response.getErrorCode()) {
                        case 0:
                            mCardItems.addAll(response.getData());
                            mCardAdapter.notifyDataSetChanged();
                            initSelect(-1);
                            if (response.getData().size() < 30) {
                                listHealthcheck.removeFooterView(mLoadmore);
                            }
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
                case tagselfDelete:
                    BaseResponse<String> responseDelete = new Gson().fromJson(event.getResponse(), new TypeToken<BaseResponse<String>>() {
                    }.getType());
                    switch (responseDelete.getErrorCode()){
                        case 0:
                            showMessage(responseDelete.getData());
//                            File tempFile = new File(healthCheckFolder+mSelectedItem.getSerialNum());
//                            if (tempFile.exists() && tempFile.isDirectory()){
//                                File[] tempFiles = tempFile.listFiles();
//                                for (File temp:tempFiles){
//                                    temp.delete();
//                                }
//                                tempFile.delete();
//                            }
                            FileUtils.deleteFile(healthCheckFolder+mSelectedItem.getSerialNum());
                            mCardItems.remove(mSelectedPosition);
                            mCardAdapter.notifyDataSetChanged();
                            initSelect(-1);
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
                case tagHealthcheckDetailDelete:
                    BaseResponse<String> responseDeleteDetail = new Gson().fromJson(event.getResponse(), new TypeToken<BaseResponse<String>>() {
                    }.getType());
                    switch (responseDeleteDetail.getErrorCode()){
                        case 0:
                            showMessage(responseDeleteDetail.getData());
                            if (isSelected()){
//                                File tempFile = new File(healthCheckFolder+mSelectedItem.getSerialNum()+ File.separator+mFileNeedDelete);
//                                if (tempFile.exists() && tempFile.isFile()){
//                                    tempFile.delete();
//                                }
                                FileUtils.deleteFile(healthCheckFolder+mSelectedItem.getSerialNum()+ File.separator+mFileNeedDelete);
                                mSelectedItem.getFileNames().remove(mFileNeedDelete);
                                mCardAdapter.notifyDataSetChanged();
                                initSelect(-1);
                            }
                            break;
                        default:
                            String msg = responseDeleteDetail.getErrorMsg();
                            if (StringUtils.isEmpty(msg)) {
                                msg = "网络连接错误，请稍后重试。";
                            }
                            showMessage(msg);
                            break;
                    }
                    break;
                case tagHealthcheckDetailDownload:
                    if (StringUtils.isNotEmpty(event.getResponse())){
                        File tempFile = new File(event.getResponse());
                        if (tempFile.exists()){
                            DebugLog.e(tempFile.getName());
                            openHealthCheck(tempFile.getName());
                        }
                    }else {
                        showMessage("加载失败。");
                    }
                    break;
            }
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_healthcheck_list, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
