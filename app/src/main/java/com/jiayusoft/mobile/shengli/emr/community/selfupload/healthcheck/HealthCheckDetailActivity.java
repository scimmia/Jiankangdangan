package com.jiayusoft.mobile.shengli.emr.community.selfupload.healthcheck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.InjectView;
import com.devspark.appmsg.AppMsg;
import com.jiayusoft.mobile.shengli.emr.community.R;
import com.jiayusoft.mobile.utils.app.BaseActivity;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class HealthCheckDetailActivity extends BaseActivity implements OnPageChangeListener {


    @InjectView(R.id.pdfview)
    PDFView pdfview;

    String mSerialNum;
    String mFileName;
    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_health_check_detail);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mSerialNum = bundle.getString("serialNum");
            mFileName = bundle.getString("fileName");
            if (!StringUtils.isAnyEmpty(mSerialNum,mFileName) && mFileName.endsWith(".pdf")){
                File targetFile = new File(healthCheckFolder+mSerialNum+ File.separator+mFileName);
                if (targetFile.exists()){
                    pdfview.fromFile(targetFile)
                            .defaultPage(1)
                            .showMinimap(false)
                            .enableSwipe(true)
                            .onPageChange(this)
                            .load();
                }else {
                    showMessage("文件不存在，请刷新后重试", AppMsg.STYLE_ALERT);
                }
            }else{
                showMessage("不能打开非PDF格式的文件", AppMsg.STYLE_ALERT);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_health_check_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            new AlertDialog.Builder(getBaseActivity())
                    .setTitle("提示")
                    .setMessage("将从服务器上删除该文件，是否确定？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Bundle bundle = new Bundle();
                            bundle.putString("filename", mFileName);
                            Intent it = new Intent();
                            it.putExtras(bundle);
                            setResult(RESULT_OK, it);
                            finish();
                        }
                    })
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPageChanged(int page, int pageCount) {
        getSupportActionBar().setSubtitle(String.format("第%s / %s页", page, pageCount));
    }
}
