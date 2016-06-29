package com.jiayusoft.mobile.shengli.emr.community.ehr;

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
import com.jiayusoft.mobile.shengli.emr.community.selfupload.mydescribe.MyDescribeDetailActivity;
import com.jiayusoft.mobile.utils.DebugLog;
import com.jiayusoft.mobile.utils.app.BaseActivity;
import com.jiayusoft.mobile.utils.app.cardview.CardEvent;
import com.jiayusoft.mobile.utils.http.BaseResponse;
import com.jiayusoft.mobile.utils.http.HttpEvent;
import com.jiayusoft.mobile.utils.http.HttpTask;
import com.squareup.otto.Subscribe;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.jiayusoft.mobile.utils.app.cardview.CardEvent.cardClickEvent;

public class MyEhrListActivity extends BaseActivity {

//    @InjectView(R.id.list_ehr)
//    ListView mListResult;
//    @InjectView(android.R.id.empty)
//    TextView mEmpty;
//    ArrayList<Ehr> mCardItems;
//    EhrAdapter mCardAdapter;
//    //    @InjectView(R.id.loadmore)
//    Button mLoadmore;
    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_my_ehr_list);
    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mCardItems = new ArrayList<Ehr>();
//        mCardAdapter = new EhrAdapter(getBaseActivity(), mCardItems);
//        mLoadmore = new Button(getBaseActivity());
//        mLoadmore.setText(R.string.loadmore);
//        mLoadmore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadMore();
//            }
//        });
//        mListResult.addFooterView(mLoadmore);
//        mListResult.setEmptyView(mEmpty);
//        mListResult.setAdapter(mCardAdapter);
//
//        mDelPosition = -1;
//        loadMore();
//    }
//
//    void loadMore() {
//        String url = String.format(ehrLoadMoreUrl,
//                BaseApplication.getCurrentUser().getOrgCode(),
//                BaseApplication.getCurrentUser().getIdcard(),
//                String.valueOf(mCardItems.size()));
//        new HttpTask(getBaseActivity(), "查询中...", httpGet, tagEhrLoadMore, url, null)
//                .execute();
//    }
//
////    @OnItemClick(R.id.list_ehr)
////    void OnItemClick(int position) {
////        DebugLog.e("OnItemClick" + position);
////        Ehr temp = mCardItems.get(position);
////        if(temp!=null && temp.getPhotoNum()>0) {
////            Bundle bundle = new Bundle();
////            bundle.putString("ehr", new Gson().toJson(mCardItems.get(position)));
////            beginActivity(EhrDetailActivity.class, bundle);
////        }
////    }
//
//    int mDelPosition;
//    void deleteCard(int position){
//        mDelPosition = position;
//        HashMap<String,String> mFormBody = new HashMap<String, String>();
//        mFormBody.put("idcard", mCardItems.get(mDelPosition).getIdCard());
//        mFormBody.put("serialnum", mCardItems.get(mDelPosition).getSerialNum());
//        new HttpTask(getBaseActivity(), "正在删除...", httpPost,
//                tagEhrDelete, ehrDeleteUrl, mFormBody)
//                .execute();
//    }
//
//    @Subscribe
//    public void onCardEvent(CardEvent event) {
//        final int position = event.getPosition();
//        Ehr temp = mCardItems.get(position);
//        switch (event.getEventType()) {
//            case cardClickEvent:
//                if(temp!=null && temp.getPhotoNum()>0) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("ehr", new Gson().toJson(mCardItems.get(position)));
//                    beginActivityForResult(MyDescribeDetailActivity.class, deleteMyDescribeDetail, bundle);
//                }else{
//                    showMessage("该自述无附加图片。");
//                }
//                break;
//            case cardImageEvent:
//                new AlertDialog.Builder(getBaseActivity())
//                        .setTitle("提示")
//                        .setMessage("将从服务器上删除该记录及图片，是否确定？")
//                        .setNegativeButton("取消", null)
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                deleteCard(position);
//                            }
//                        })
//                        .show();
//                break;
//        }
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case deleteMyDescribeDetail:
//                    Bundle bundle = data.getExtras();
//                    if (bundle != null) {
//                        String serialNum = bundle.getString("serialNum");
//                        if (StringUtils.isNotEmpty(serialNum)){
//                            for (Ehr temp: mCardItems){
//                                if (StringUtils.equals(temp.getSerialNum(),serialNum)){
//                                    ArrayList<String> deleteFiles = bundle.getStringArrayList("deleteFiles");
//                                    if (deleteFiles!=null){
//                                        temp.setPhotoNum(temp.getPhotoNum()-deleteFiles.size());
//                                        mCardAdapter.notifyDataSetChanged();
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    break;
//            }
//        }
//    }
//
//    @Subscribe
//    public void onHttpEvent(HttpEvent event) {
//        if (event == null || StringUtils.isEmpty(event.getResponse())) {
//            showMessage("网络连接错误，请稍后重试。");
//        } else {
//            int tag = event.getTag();
//            DebugLog.e(event.getResponse());
//            switch (tag) {
//                case tagEhrLoadMore:
//                    BaseResponse<List<Ehr>> response = new Gson().fromJson(event.getResponse(), new TypeToken<BaseResponse<List<Ehr>>>() {
//                    }.getType());
//                    switch (response.getErrorCode()) {
//                        case 0:
//                            mCardItems.addAll(response.getData());
//                            mCardAdapter.notifyDataSetChanged();
//                            if (response.getData().size() < 30) {
////                                mLoadmore.setVisibility(View.GONE);
//                                mListResult.removeFooterView(mLoadmore);
//                            }
//                            break;
//                        default:
//                            String msg = response.getErrorMsg();
//                            if (StringUtils.isEmpty(msg)) {
//                                msg = "网络连接错误，请稍后重试。";
//                            }
//                            showMessage(msg);
//                            break;
//                    }
//                    break;
//                case tagEhrDelete:
//                    BaseResponse<String> responseDelete = new Gson().fromJson(event.getResponse(), new TypeToken<BaseResponse<String>>() {
//                    }.getType());
//                    switch (responseDelete.getErrorCode()){
//                        case 0:
//                            showMessage(responseDelete.getData());
//                            mCardItems.remove(mDelPosition);
//                            mCardAdapter.notifyDataSetChanged();
//                            mDelPosition = -1;
//                            break;
//                        default:
//                            String msg = responseDelete.getErrorMsg();
//                            if (StringUtils.isEmpty(msg)) {
//                                msg = "网络连接错误，请稍后重试。";
//                            }
//                            showMessage(msg);
//                            break;
//                    }
//                    break;
//            }
//        }
//    }
////    @Override
////    public boolean onCreateOptionsMenu(Menu menu) {
////        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.menu_my_ehr_list, menu);
////        return true;
////    }
////
////    @Override
////    public boolean onOptionsItemSelected(MenuItem item) {
////        // Handle action bar item clicks here. The action bar will
////        // automatically handle clicks on the Home/Up button, so long
////        // as you specify a parent activity in AndroidManifest.xml.
////        int id = item.getItemId();
////
////        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
////
////        return super.onOptionsItemSelected(item);
////    }
}
