package com.jiayusoft.mobile.shengli.emr.community.ehr;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.jiayusoft.mobile.shengli.emr.community.R;
import com.jiayusoft.mobile.utils.DebugLog;
import com.jiayusoft.mobile.utils.GlobalData;
import com.jiayusoft.mobile.utils.app.BaseActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class MultiPhotoSelectActivity extends BaseActivity implements GlobalData {

    SparseBooleanArray selectedFiles;
    GalleryAdapter galleryAdapter;
    ArrayList<String> imageUrls;

    @InjectView(R.id.gallerygridview)
    GridView mGridview;
    DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedFiles = new SparseBooleanArray();
        DebugLog.e(selectedFiles.toString());

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_refresh)
                .showImageForEmptyUri(R.drawable.ic_refresh)
                .showImageOnFail(R.drawable.ic_refresh)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        imageUrls = new ArrayList<String>();
        galleryAdapter = new GalleryAdapter();
        mGridview.setAdapter(galleryAdapter);
        new LoadGallery().execute();
    }

    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_multi_photo_select);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multi_photo_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_ok:
                ArrayList<String> temp =new ArrayList<String>();
                int selectedSize = selectedFiles.size();
                for (int i=0;i<selectedSize;i++){
                    if (selectedFiles.valueAt(i)) {
                        temp.add(imageUrls.get(selectedFiles.keyAt(i)));
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("selectedFiles", temp);
                Intent it = new Intent();
                it.putExtras(bundle);
                setResult(RESULT_OK, it);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public class LoadGallery extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress("加载图片");
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<String> selecteds = new ArrayList<String>();
            Bundle bundle = getIntent().getExtras();
            if (bundle!=null){
                ArrayList<String> temp = bundle.getStringArrayList("selectedFiles");
                if (temp!=null) {
                    selecteds.addAll(temp);
                }
            }
            // 只查询jpeg和png的图片
            Cursor cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{ MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID },
                    MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png"},
                    MediaStore.Images.Media.DATE_MODIFIED + " desc");
            imageUrls.clear();
            int count = cursor.getCount();
            for (int i=0;i<count;i++){
                cursor.moveToNext();
                String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                if (selecteds.remove(fileName)){
                    selectedFiles.put(i,true);
                }
                imageUrls.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            }
            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            cancelProgress();
            galleryAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cancelProgress();
        }
    }

    @OnItemClick(R.id.gallerygridview)
    void selectItem(int position){
        DebugLog.e("position"+position);
        if (selectedFiles.get(position)){
            selectedFiles.delete(position);
        }else {
            if (selectedFiles.size()>=maxPhotoCount){
                showMessage(String.format("附加照片不可超过%d张",maxPhotoCount));
                return;
            }
            selectedFiles.put(position,true);
        }
        galleryAdapter.notifyDataSetChanged();
    }

    public class GalleryAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public String getItem(int i) {
            return imageUrls.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view != null) {
                holder = (ViewHolder) view.getTag();
            } else {
                view = LayoutInflater.from(getBaseActivity()).inflate(R.layout.item_multiphoto, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            }
            if (selectedFiles.get(i)){
                holder.check.setImageResource(android.R.drawable.checkbox_on_background);
            }else{
                holder.check.setImageResource(android.R.drawable.checkbox_off_background);
            }
            ImageLoader.getInstance().displayImage("file://" + getItem(i), holder.photo, options);
            return view;
        }
    }

    class ViewHolder {
        @InjectView(R.id.photoView) ImageView photo;
        @InjectView(R.id.photocheckBox) ImageView check;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}


//public class MultiPhotoSelectActivity extends BaseActivity {
//
//    HashSet<String> selectedFiles;
//    GalleryAdapter galleryAdapter;
//    ArrayList<String> imageUrls;
//
//    @InjectView(R.id.gallerygridview)
//    GridView mGridview;
//    DisplayImageOptions options;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        selectedFiles = new HashSet<String>();
//        Bundle bundle = getIntent().getExtras();
//        if (bundle!=null){
//            ArrayList<String> temp = bundle.getStringArrayList("selectedFiles");
//            if (temp!=null) {
//                selectedFiles.addAll(temp);
//            }
//        }
//        DebugLog.e(selectedFiles.toString());
//
//        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.ic_add)
//                .showImageForEmptyUri(R.drawable.ic_empty)
//                .showImageOnFail(R.drawable.ic_camera)
//                .cacheInMemory(true)
//                .cacheOnDisk(false)
//                .considerExifParams(true)
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .build();
//
//        imageUrls = new ArrayList<String>();
//        galleryAdapter = new GalleryAdapter();
//        mGridview.setAdapter(galleryAdapter);
//        new LoadGallery().execute();
//    }
//
//    @Override
//    protected void initContentView() {
//        setContentView(R.layout.activity_multi_photo_select);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_multi_photo_select, menu);
//        return true;
//    }
//
//    final int addPhoto = 102;
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        switch (id){
//            case R.id.action_ok:
//                ArrayList<String> temp =new ArrayList<String>();
//                temp.addAll(selectedFiles);
//                Bundle bundle = new Bundle();
//                bundle.putStringArrayList("selectedFiles", temp);
//                Intent it = new Intent();
//                it.putExtras(bundle);
//                setResult(RESULT_OK, it);
//                finish();
//                break;
//            case R.id.action_camera:
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(takePictureIntent, addPhoto);
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case addPhoto:
//                    String[] proj = {MediaStore.Images.Media.DATA};
//                    Cursor actualimagecursor = managedQuery(data.getData(), proj, null, null, null);
//                    int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                    actualimagecursor.moveToFirst();
//                    String img_path = actualimagecursor.getString(actual_image_column_index);
//                    selectedFiles.add(img_path);
//                    imageUrls.add(0,img_path);
//                    galleryAdapter.notifyDataSetChanged();
//                    break;
//            }
//        }
//    }
//
//    public class LoadGallery extends AsyncTask<Void,Void,Void>{
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            showProgress("加载图片");
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            // 只查询jpeg和png的图片
//            Cursor cursor = getContentResolver().query(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    new String[]{ MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID },
//                    MediaStore.Images.Media.MIME_TYPE + "=? or "
//                            + MediaStore.Images.Media.MIME_TYPE + "=?",
//                    new String[]{"image/jpeg", "image/png"},
//                    MediaStore.Images.Media.DATE_MODIFIED + " desc");
//            imageUrls.clear();
//            while (cursor.moveToNext()){
//                imageUrls.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
//            }
//            cursor.close();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void o) {
//            super.onPostExecute(o);
//            cancelProgress();
//            galleryAdapter.notifyDataSetChanged();
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//            cancelProgress();
//        }
//    }
//
//    @OnItemClick(R.id.gallerygridview)
//    void selectItem(int position){
//        DebugLog.e("position"+position);
//        String selected = imageUrls.get(position);
//        if (selectedFiles.contains(selected)){
//            selectedFiles.remove(selected);
//        }else {
//            selectedFiles.add(selected);
//        }
//        galleryAdapter.notifyDataSetChanged();
//    }
//
//    public class GalleryAdapter extends BaseAdapter{
//
//        @Override
//        public int getCount() {
//            return imageUrls.size();
//        }
//
//        @Override
//        public String getItem(int i) {
//            return imageUrls.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            ViewHolder holder;
//            if (view != null) {
//                holder = (ViewHolder) view.getTag();
//            } else {
//                view = LayoutInflater.from(getBaseActivity()).inflate(R.layout.item_multiphoto, viewGroup, false);
//                holder = new ViewHolder(view);
//                view.setTag(holder);
//            }
//            if (selectedFiles.contains(getItem(i))){
//                holder.check.setImageResource(android.R.drawable.checkbox_on_background);
//            }else{
//                holder.check.setImageResource(android.R.drawable.checkbox_off_background);
//            }
////            holder.check.setChecked(selectedFiles.contains(getItem(i)));
////            holder.check.setTag(i);
////            holder.check.setOnCheckedChangeListener(mCheckedChangeListener);
//            ImageLoader.getInstance().displayImage("file://" + getItem(i), holder.photo,options);
//            return view;
//        }
//
//
//    }
//    CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
//            int index = (Integer) checkBox.getTag();
//            if (index>=0 && index < imageUrls.size()) {
//                if (isChecked) {
//                    selectedFiles.add(imageUrls.get(index));
//                } else {
//                    selectedFiles.remove(imageUrls.get(index));
//                }
//            }
//        }
//    };
//    class ViewHolder {
//        @InjectView(R.id.photoView) ImageView photo;
//        @InjectView(R.id.photocheckBox) ImageView check;
//
//        public ViewHolder(View view) {
//            ButterKnife.inject(this, view);
//        }
//    }
//
//
////    public class GalleryAdapter extends ResourceCursorAdapter{
////
////        public GalleryAdapter(Context context, int layout, Cursor c, int flags) {
////            super(context, layout, c, flags);
////        }
////
////        @Override
////        public View newView(Context context, Cursor cursor, ViewGroup parent) {
////            View view = super.newView(context, cursor, parent);
////            GalleryItemCache cache = new GalleryItemCache();
////            cache.photoCheckBox = (CheckBox) view.findViewById(R.id.photocheckBox);
////            cache.photoView = (ImageView) view.findViewById(R.id.photoView);
////            view.setTag(cache);
////            return view;
////        }
////
////        @Override
////        public void bindView(View view, Context context, Cursor cursor) {
////            final GalleryItemCache cache = (GalleryItemCache) view.getTag();
////            String file = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
////            ImageLoader.getInstance().displayImage("file://"+file, cache.photoView);
////            cache.photoCheckBox.setChecked(selectedFiles.contains(file));
////
////        }
////    }
////
////    final static class GalleryItemCache {
////        public ImageView photoView;
////        public CheckBox photoCheckBox;
////    }
//
//
//
////    public class GalleryAdapter extends CursorAdapter {
////        final LayoutInflater mInflater;
////        public GalleryAdapter(Context context) {
////            super(context, null, false);
////            mInflater = LayoutInflater.from(context);
////        }
////        @Override
////        public View newView(Context context, Cursor cursor, ViewGroup parent) {
////            return mInflater.inflate(R.layout.item_multiphoto, parent, false);
////        }
////        @Override
////        public long getItemId(int position) {
////            return super.getItemId(position);
////        }
////        @Override
////        public boolean hasStableIds() {
////            return super.hasStableIds();
////        }
////        @Override
////        public void bindView(View view, final Context context, Cursor cursor) {
////            final ImageView iv = (ImageView) view;
////            final String path = cursor.getString(GalleryPickerActivity.DATA_INDEX);
////            final long id = cursor.getLong(GalleryPickerActivity.ID_INDEX);
////            final Uri uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, id);
////            final int width = iv.getWidth();
////            if (width <= 0) {
////                iv.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
////                    @SuppressWarnings("deprecation")
////                    @Override
////                    public void onGlobalLayout() {
////                        iv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
////                        Picasso.with(context).load(uri).placeholder(R.drawable.picker_photo_holder)
////                                .resize(iv.getWidth(), iv.getHeight()).centerCrop().into(iv);
////                    }
////                });
////            } else {
////                Picasso.with(context).load(uri).placeholder(R.drawable.picker_photo_holder).resize(iv.getWidth(), iv.getHeight())
////                        .centerCrop().into(iv);
////            }
////        }
////    }
//}
