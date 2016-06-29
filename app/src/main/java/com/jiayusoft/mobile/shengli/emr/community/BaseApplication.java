package com.jiayusoft.mobile.shengli.emr.community;

import android.app.Application;
import android.graphics.Bitmap;
import com.jiayusoft.mobile.shengli.emr.community.beans.UserCommunity;
import com.jiayusoft.mobile.utils.GlobalData;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.squareup.okhttp.OkHttpClient;

/**
 * Created by ASUS on 2014/7/1.
 */
public class BaseApplication extends Application implements GlobalData {
    private static final String DB_NAME = "green.db";
    private static BaseApplication mInstance;
//    private static DaoMaster daoMaster;
//    private static DaoSession daoSession;


    public static BaseApplication getInstance() {
        return mInstance;
    }

    private static UserCommunity currentUser;
    public static UserCommunity getCurrentUser() {
        if (currentUser == null){
            currentUser = new UserCommunity();
        }
        return currentUser;
    }

    public static void setCurrentUser(UserCommunity currentUser) {
        BaseApplication.currentUser = currentUser;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(mInstance == null)
            mInstance = this;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .diskCacheSize(100 * 1024 * 1024) // 100 Mb
                    .tasksProcessingOrder(QueueProcessingType.LIFO).build();
            ImageLoader.getInstance().init(config);
        }
        mImgOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_detail_loading)
                .showImageForEmptyUri(R.drawable.ic_detail_empty)
                .showImageOnFail(R.drawable.ic_detail_empty)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public static DisplayImageOptions mImgOptions;


//    /**
//     * 取得DaoMaster
//     *
//     * @param context
//     * @return
//     */
//    public static DaoMaster getDaoMaster(Context context) {
//        if (daoMaster == null) {
//            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context,DB_NAME, null);
//            daoMaster = new DaoMaster(helper.getWritableDatabase());
//        }
//        return daoMaster;
//    }
//
//    /**
//     * 取得DaoSession
//     *
//     * @param context
//     * @return
//     */
//    public static DaoSession getDaoSession(Context context) {
//        if (daoSession == null) {
//            if (daoMaster == null) {
//                daoMaster = getDaoMaster(context);
//            }
//            daoSession = daoMaster.newSession();
//        }
//        return daoSession;
//    }

    private static OkHttpClient okHttpClient;
    public static OkHttpClient getOkHttpClient(){
        if (okHttpClient == null){
            okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }
}
