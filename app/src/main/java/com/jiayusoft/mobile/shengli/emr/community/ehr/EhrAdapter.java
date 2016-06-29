package com.jiayusoft.mobile.shengli.emr.community.ehr;

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
import com.jiayusoft.mobile.utils.GlobalData;
import com.jiayusoft.mobile.utils.app.cardview.CardEvent;
import com.jiayusoft.mobile.utils.eventbus.BusProvider;

import java.util.ArrayList;

import static com.jiayusoft.mobile.utils.app.cardview.CardEvent.cardClickEvent;
import static com.jiayusoft.mobile.utils.app.cardview.CardEvent.cardImageEvent;

/**
 * Created by ASUS on 2015/3/9.
 */
public class EhrAdapter extends BaseAdapter implements GlobalData {
    public Context context;
    public ArrayList<Ehr> cardItems;

    public EhrAdapter(Context context, ArrayList<Ehr> cardItems) {
        this.context = context;
        this.cardItems = cardItems;
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
        viewHolder.mCardImage.setImageResource(android.R.drawable.ic_menu_delete);
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
            BusProvider.getInstance().post(new CardEvent(position,cardImageEvent));
        }
        @OnClick(R.id.card_view)
        void cardClicked(){
            int position = (Integer) mCardImage.getTag();
            DebugLog.e("showPosition:" + position);
            BusProvider.getInstance().post(new CardEvent(position,cardClickEvent));
        }

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
}
