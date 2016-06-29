package com.jiayusoft.mobile.shengli.emr.community.selfupload.mydescribe;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.google.gson.Gson;
import com.jiayusoft.mobile.shengli.emr.community.R;
import com.jiayusoft.mobile.shengli.emr.community.ehr.MultiPhotoSelectActivity;
import com.jiayusoft.mobile.shengli.emr.community.ehr.PhotoDetailActivity;
import com.jiayusoft.mobile.utils.DebugLog;
import com.jiayusoft.mobile.utils.GlobalData;
import com.jiayusoft.mobile.utils.app.BaseActivity;
import com.jiayusoft.mobile.utils.app.cardview.CardItem;
import com.jiayusoft.mobile.utils.http.HttpEvent;
import com.jiayusoft.mobile.utils.http.HttpUploadTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class UploadMyDescribeActivity extends BaseActivity implements GlobalData {

    @InjectView(R.id.photo_gallery)
    GridView mPhotoGallery;
    @InjectView(R.id.photo_title)
    TextView mPhotoTitle;
    @InjectView(R.id.self_describe)
    MaterialEditText mSelfDescribe;

    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_upload_ehr);
    }

    ArrayList<String> selectedFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedFiles = new ArrayList<String>();
        adapter = new ImageAdapter();
        mPhotoGallery.setAdapter(adapter);
        refreshGallery();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_upload_ehr, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("selectedFiles", selectedFiles);
                beginActivityForResult(MultiPhotoSelectActivity.class, addGallery, bundle);
                break;
            case R.id.action_camera:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, addPhoto);
                break;
            case R.id.action_upload:
                String describe = mSelfDescribe.getText().toString();
                if (describe.length()>maxDescribeCount){
                    showMessage(String.format("自述不可超过%d字",maxDescribeCount));
                    return true;
                }else if (StringUtils.isEmpty(describe)){
                    showMessage("自述不可为空");
                    return true;
                }
                if (selectedFiles.size()>maxPhotoCount){
                    showMessage(String.format("附加照片不可超过%d张",maxPhotoCount));
                    return true;
                }
                new HttpUploadTask(getBaseActivity(),tagUploadDescribe,describe,selectedFiles, CardItem.typeMyDescribe).execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case addGallery:
                    selectedFiles.clear();
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        ArrayList<String> temp = bundle.getStringArrayList("selectedFiles");
                        if (temp != null) {
                            selectedFiles.addAll(temp);
                        }
                    }
                    refreshGallery();
                    DebugLog.e(selectedFiles.toString());
                    break;
                case addPhoto:
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor actualimagecursor = managedQuery(data.getData(), proj, null, null, null);
                    int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    actualimagecursor.moveToFirst();
                    String img_path = actualimagecursor.getString(actual_image_column_index);
                    selectedFiles.add(0, img_path);
                    refreshGallery();
                    break;
                case photoDetail:
                    Bundle bundleDetail = data.getExtras();
                    if (bundleDetail != null) {
                        if (selectedFiles.remove(bundleDetail.getString("deletePhoto"))) {
                            refreshGallery();
                        }
                    }
                    break;
            }
        }
    }

    ImageAdapter adapter;

    private class ImageAdapter extends BaseAdapter {

        DisplayImageOptions options;
        private LayoutInflater inflater;

        ImageAdapter() {
            inflater = LayoutInflater.from(getBaseActivity());
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_refresh)
                    .showImageForEmptyUri(R.drawable.ic_refresh)
                    .showImageOnFail(R.drawable.ic_refresh)
                    .cacheInMemory(true)
                    .cacheOnDisk(false)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }

        @Override
        public int getCount() {
            return selectedFiles.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = (ImageView) convertView;
            if (imageView == null) {
                imageView = (ImageView) inflater.inflate(R.layout.item_gallery_imageview, parent, false);
            }
            ImageLoader.getInstance().displayImage("file://" + selectedFiles.get(position), imageView, options);
            return imageView;
        }
    }

    @OnItemClick(R.id.photo_gallery)
    void clickItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("photoPath", selectedFiles.get(position));
        beginActivityForResult(PhotoDetailActivity.class, photoDetail, bundle);
    }

    void refreshGallery() {
        mPhotoTitle.setText(String.format("附加图片:  %d张", selectedFiles.size()));
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onHttpEvent(HttpEvent event) {
        if (event == null || StringUtils.isEmpty(event.getResponse())) {
            showMessage("网络连接错误，请稍后重试。");
        } else {
            int tag = event.getTag();
            DebugLog.e(event.getResponse());
            switch (tag) {
                case tagUploadDescribe:
                    SparseBooleanArray response = new Gson().fromJson(event.getResponse(), SparseBooleanArray.class);
                    DebugLog.e(response.toString());
                    showMessage("上传成功。");
                    break;
            }
        }
    }

}
