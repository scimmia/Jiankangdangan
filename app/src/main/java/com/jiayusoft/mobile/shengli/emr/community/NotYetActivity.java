package com.jiayusoft.mobile.shengli.emr.community;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.jiayusoft.mobile.utils.app.BaseActivity;


public class NotYetActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int tempType = bundle.getInt(itemType, 0);
            getSupportActionBar().setTitle(tempType);
        }
    }

    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_not_yet);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_not_yet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
