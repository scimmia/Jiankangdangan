package com.jiayusoft.mobile.utils.app.clientinfo;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.jiayusoft.mobile.shengli.emr.community.R;

import java.util.List;

public class ClientinfoAdapter extends BaseAdapter {

    Context mContext;
    protected List<ClientinfoItem> mSourceItems;

    public ClientinfoAdapter(Context mContext, List<ClientinfoItem> sourceItem) {
        this.mContext = mContext;
        mSourceItems = sourceItem;
    }

    @Override
    public int getCount() {
        if (mSourceItems != null) {
            return mSourceItems.size();
        }
        return 0;
    }

    @Override
    public ClientinfoItem getItem(int arg0) {
        if (mSourceItems != null) {
            return mSourceItems.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.clientinfo_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.item_title);
//            viewHolder.icon = (ImageView) convertView.findViewById(R.id.item_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(getItem(position).getmTitleId());
        Drawable top = mContext.getResources().getDrawable(getItem(position).getmPicId());
        viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
//        viewHolder.icon.setImageResource(getItem(position).getmPicId());
        return convertView;
    }

    static class ViewHolder {
        TextView title;
//        ImageView icon;
    }
}
