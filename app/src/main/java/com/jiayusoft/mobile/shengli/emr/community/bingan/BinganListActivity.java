package com.jiayusoft.mobile.shengli.emr.community.bingan;

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
import com.jiayusoft.mobile.shengli.emr.community.beans.Bingan;
import com.jiayusoft.mobile.utils.DebugLog;
import com.jiayusoft.mobile.utils.app.BaseActivity;
import com.jiayusoft.mobile.utils.app.cardview.CardEvent;
import com.jiayusoft.mobile.utils.http.BaseResponse;
import com.jiayusoft.mobile.utils.http.HttpEvent;
import com.jiayusoft.mobile.utils.http.HttpTask;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.squareup.otto.Subscribe;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BinganListActivity extends BaseActivity {

    @InjectView(R.id.list_result)
    ListView mListResult;
    @InjectView(android.R.id.empty)
    TextView mEmpty;

    ArrayList<Bingan> cardItems;
    BinganCardAdapter mCardAdapter;
    Button mLoadmore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardItems = new ArrayList<Bingan>();
        mCardAdapter = new BinganCardAdapter(getBaseActivity(), cardItems, cardImageNull);
        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(mCardAdapter);
        animationAdapter.setAbsListView(mListResult);
        mLoadmore = new Button(getBaseActivity());
        mLoadmore.setText(R.string.loadmore);
        mLoadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore();
            }
        });
        mListResult.addFooterView(mLoadmore);
        mListResult.setEmptyView(mEmpty);
        mListResult.setAdapter(animationAdapter);

        loadMore();
    }

    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_bingan_list);
    }

    //    @OnClick(R.id.loadmore)
    void loadMore() {
        HashMap<String,String> mFormBody = new HashMap<String, String>();
        mFormBody.put("userid", BaseApplication.getCurrentUser().getLoginAccount());
        mFormBody.put("password", BaseApplication.getCurrentUser().getPassword());
        mFormBody.put("orgcode", BaseApplication.getCurrentUser().getOrgCode());
        mFormBody.put("startindex", String.valueOf(cardItems.size()));
        new HttpTask(getBaseActivity(), "查询中...", httpPost, tagcommunityBinganLoadMore, communityBinganLoadMoreUrl, mFormBody)
                .execute();
    }


    @Subscribe
    public void onHttpEvent(HttpEvent event) {
        if (event == null || StringUtils.isEmpty(event.getResponse())) {
            showMessage("网络连接错误，请稍后重试。");
        } else {
            int tag = event.getTag();
            DebugLog.e(event.getResponse());
            switch (tag) {
                case tagcommunityBinganLoadMore:
                    BaseResponse<List<Bingan>> response = new Gson().fromJson(event.getResponse(), new TypeToken<BaseResponse<List<Bingan>>>() {
                    }.getType());
                    switch (response.getErrorCode()) {
                        case 0:
                            cardItems.addAll(response.getData());
                            mCardAdapter.notifyDataSetChanged();
                            if (response.getData().size() < 30) {
//                                mLoadmore.setVisibility(View.GONE);
                                mListResult.removeFooterView(mLoadmore);
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
            }
        }
    }

    @Subscribe
    public void onCardEvent(CardEvent event) {
        Bingan binganCard;
        switch (event.getEventType()) {
            case CardEvent.cardClickEvent:
                binganCard = cardItems.get(event.getPosition());
                Bundle bundle = new Bundle();
                bundle.putString("bingan", new Gson().toJson(binganCard));
                beginActivity(BinganDetailActivity.class, bundle);
                break;
            case CardEvent.cardImageEvent:
//                binganCard = cardItems.get(event.getPosition());
//                if (binganCard.getShoucang()) {
//                    mDBHelper.removeFavourite(binganCard);
//                } else {
//                    mDBHelper.addFavourite(binganCard);
//                }
////                binganCard.setShoucang(!binganCard.getShoucang());
////                new DBHelper(getBaseActivity()).addFavourite(binganCard,!binganCard.getShoucang());
//                mCardAdapter.notifyDataSetChanged();
                break;
        }

    }

}
