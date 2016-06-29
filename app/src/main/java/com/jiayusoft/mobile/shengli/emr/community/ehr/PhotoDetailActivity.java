package com.jiayusoft.mobile.shengli.emr.community.ehr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.InjectView;
import com.jiayusoft.mobile.shengli.emr.community.R;
import com.jiayusoft.mobile.utils.app.BaseActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import uk.co.senab.photoview.PhotoView;

public class PhotoDetailActivity extends BaseActivity {

    @InjectView(R.id.photo_detail)
    PhotoView mPhotoDetail;

    String photoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            photoPath = bundle.getString("photoPath");
            if (photoPath!=null) {
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .showImageOnLoading(R.drawable.ic_detail_loading)
                        .showImageForEmptyUri(R.drawable.ic_ehr_detail_empty)
                        .showImageOnFail(R.drawable.ic_ehr_detail_empty)
                        .cacheInMemory(true)
                        .cacheOnDisk(false)
                        .considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();
                ImageLoader.getInstance().displayImage("file://" + photoPath, mPhotoDetail,options);
            }
        }
    }

    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_photo_detail);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            Bundle bundle = new Bundle();
            bundle.putString("deletePhoto", photoPath);
            Intent it = new Intent();
            it.putExtras(bundle);
            setResult(RESULT_OK, it);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
