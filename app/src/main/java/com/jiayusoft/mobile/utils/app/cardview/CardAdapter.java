package com.jiayusoft.mobile.utils.app.cardview;

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
import com.jiayusoft.mobile.utils.DebugLog;
import com.jiayusoft.mobile.utils.eventbus.BusProvider;

import java.util.ArrayList;

/**
 * Created by ASUS on 2015/3/17.
 */
public class CardAdapter extends BaseAdapter {
    public Context context;
    public ArrayList<CardItem> cardItems;
    boolean mShowImage;

    public CardAdapter(Context context, ArrayList<CardItem> cardItems) {
        this(context,cardItems,true);
    }
    public CardAdapter(Context context, ArrayList<CardItem> cardItems, boolean showImage) {
        this.context = context;
        this.cardItems = cardItems;
        mShowImage = showImage;
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_card, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.mCardContent.setText(cardItems.get(position).getText());
        if (mShowImage) {
            viewHolder.mCardImage.setImageResource(android.R.drawable.ic_menu_delete);
            viewHolder.mCardImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mCardImage.setVisibility(View.GONE);
        }
        viewHolder.mCardImage.setTag(position);

        return convertView;
    }

    class ViewHolder {
        @InjectView(R.id.card_content)
        TextView mCardContent;
        @InjectView(R.id.card_image)
        ImageView mCardImage;

        @OnClick(R.id.card_image)
        void imageClicked(){
            int position = (Integer) mCardImage.getTag();
            BusProvider.getInstance().post(new CardEvent(position,CardEvent.cardImageEvent));
        }
        @OnClick(R.id.card_view)
        void cardClicked(){
            int position = (Integer) mCardImage.getTag();
            DebugLog.e("showPosition:" + position);
            BusProvider.getInstance().post(new CardEvent(position,CardEvent.cardClickEvent));
        }

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
}
