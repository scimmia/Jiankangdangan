//package com.jiayusoft.mobile.shengli.bingan.card;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//import butterknife.OnClick;
//import com.jiayusoft.mobile.shengli.bingan.R;
//import com.jiayusoft.mobile.utils.DebugLog;
//import com.jiayusoft.mobile.utils.GlobalData;
//import com.jiayusoft.mobile.utils.eventbus.BusProvider;
//
//import java.util.ArrayList;
//
///**
// * Created by ASUS on 2014/12/2.
// */
//public class CardAdapter extends BaseAdapter implements GlobalData{
//
//    public Context context;
//    public ArrayList<BinganCard> cardItems;
//    public int cardImageType;
//    public CardAdapter(Context context, ArrayList<BinganCard> navDrawerItems,int cardImageType) {
//        this.context = context;
//        this.cardItems = navDrawerItems;
//        this.cardImageType = cardImageType;
//    }
//
//    @Override
//    public int getCount() {
//        return cardItems.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return cardItems.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder;
//        if (convertView==null){
//            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_card, null);
//            viewHolder = new ViewHolder(convertView);
//            convertView.setTag(viewHolder);
//        }else {
//            viewHolder = (ViewHolder)convertView.getTag();
//        }
//        viewHolder.mCardContent.setText(cardItems.get(position).getText());
//        viewHolder.mCardImage.setTag(position);
//        switch (cardImageType){
//            case cardImageStar:
//                if (cardItems.get(position).isShoucang()){
//                    viewHolder.mCardImage.setImageResource(R.drawable.ic_star_black);
//                }else{
//                    viewHolder.mCardImage.setImageResource(R.drawable.ic_star_outline);
//                }
//                break;
//            case cardImageBorrow:
//                if (cardItems.get(position).isJieyue()){
//                    viewHolder.mCardImage.setImageResource(R.drawable.ic_borrow_added);
//                }else{
//                    viewHolder.mCardImage.setImageResource(R.drawable.ic_borrow_add);
//                }
//                break;
//            case cardImageNull:
//                break;
//            default:
//                break;
//        }
//        return convertView;
//    }
//
//    /**
//     * This class contains all butterknife-injected Views & Layouts from layout file 'adapter_card.xml'
//     * for easy to all layout elements.
//     *
//     * @author ButterKnifeZelezny, plugin for Android Studio by Inmite Developers (http://inmite.github.io)
//     */
//    class ViewHolder {
//        @InjectView(R.id.card_content)
//        TextView mCardContent;
//        @InjectView(R.id.card_image)
//        ImageView mCardImage;
//
//        @OnClick(R.id.card_image)
//        void shoucang(){
//            int position = (Integer) mCardImage.getTag();
////            boolean shoucang = cardItems.get(position).isShoucang();
////            cardItems.get(position).setShoucang(!shoucang);
////            if (shoucang){
////                mCardImage.setImageResource(R.drawable.ic_star_outline);
////            }else{
////                mCardImage.setImageResource(R.drawable.ic_star_black);
////            }
//            BusProvider.getInstance().post(new CardEvent(position,cardImageEvent));
//        }
//        @OnClick(R.id.card_view)
//        void showPosition(){
//            int position = (Integer) mCardImage.getTag();
//            DebugLog.e("showPosition:" + position);
//            BusProvider.getInstance().post(new CardEvent(position,cardClickEvent));
//        }
//
//        ViewHolder(View view) {
//            ButterKnife.inject(this, view);
//        }
//
//    }
//
//}
//

package com.jiayusoft.mobile.shengli.emr.community.bingan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.jiayusoft.mobile.shengli.emr.community.R;
import com.jiayusoft.mobile.shengli.emr.community.beans.Bingan;
import com.jiayusoft.mobile.utils.DebugLog;
import com.jiayusoft.mobile.utils.GlobalData;
import com.jiayusoft.mobile.utils.app.cardview.CardEvent;
import com.jiayusoft.mobile.utils.eventbus.BusProvider;

import java.util.ArrayList;

/**
 * Created by ASUS on 2014/12/2.
 */
public class BinganCardAdapter extends BaseAdapter implements GlobalData {

    public Context context;
    public ArrayList<Bingan> cardItems;
    public int cardImageType;
    public BinganCardAdapter(Context context, ArrayList<Bingan> navDrawerItems, int cardImageType) {
        this.context = context;
        this.cardItems = navDrawerItems;
        this.cardImageType = cardImageType;
    }

    public void setCardImageType(int cardImageType) {
        this.cardImageType = cardImageType;
    }

    @Override
    public int getCount() {
        return cardItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cardItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_card, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.mCardContent.setText(cardItems.get(position).getText());
        viewHolder.mCardImage.setTag(position);
        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'adapter_card.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Inmite Developers (http://inmite.github.io)
     */
    class ViewHolder {
        @InjectView(R.id.card_content)
        TextView mCardContent;
        @InjectView(R.id.card_image)
        ImageView mCardImage;

        @OnClick(R.id.card_image)
        void imageClicked(){
            int position = (Integer) mCardImage.getTag();
            BusProvider.getInstance().post(new CardEvent(position, CardEvent.cardImageEvent));
        }
        @OnClick(R.id.card_view)
        void cardClicked(){
            int position = (Integer) mCardImage.getTag();
            DebugLog.e("showPosition:" + position);
            BusProvider.getInstance().post(new CardEvent(position, CardEvent.cardClickEvent));
        }

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }

}

