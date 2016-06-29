package com.jiayusoft.mobile.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by ASUS on 2014/7/1.
 */
public interface GlobalData {
    public static final String versionCode = "versioncode";
    public static final String softName = "softname";
    public static final String defaultSoftName = "community";

    public static final String defaultNetErrorMsg = "网络连接错误，请检查网络或服务地址设置。";


    public static final String defaultServerUrl = "221.214.98.55:8880/mobile";
    public static final String innerServerUrl = "11.0.0.55:8880/mobile";
    public static final String serverUrl = "serverUrl";

    public static final String LOGIN_USER_NAME = "LOGIN_USER_NAME";
    public static final String LOGIN_PASSWORD = "LOGIN_PASSWORD";
    public static final String LOGIN_SAVE_PASSWORD = "LOGIN_SAVE_PASSWORD";
    public static final String loginAutoLogin = "loginAutoLogin";
    public static final String loginSuoshuJigouName = "loginSuoshuJigouName";
    public static final String loginSuoshuJigouID = "loginSuoshuJigouID";


    public static final String itemTitle = "itemTitle";
    public static final String itemType = "itemType";

    public static final int cardImageNull = 3;

    public static final int httpGet = 1;
    public static final int httpPost = 2;


    public static final int tagLogin = 1;
    String loginCommunity = "/user/login/community";
    public static final String loginUserID = "userid";
    public static final String loginPassword = "password";
    public static final String loginOrgcode = "orgcode";

    public static final int tagCheckUpdate = 2;
    String checkUpdateUrl = "/user/checkUpdate";
    public static final int tagDownloadNewFile = 3;

    public static final int tagGetOrgInfo = 4;
    String getOrgInfoUrl = "/user/orginfo";

    String logoImgUrl = "Logo/%s";//fileName


    int tagcommunityBinganLoadMore = 11;
    String communityBinganLoadMoreUrl = "/bingan/community/list";

    int tagBinganDetail = 12;
    String binganDetailUrl = "/bingan/community/detail/%s/%s";

    String binganDetailImageUrl = "Doc/%s";//fileName


    public final int addGallery = 101;
    public final int addPhoto = 102;
    public final int photoDetail = 103;
    public final int deleteMyDescribeDetail = 104;
    public final int deleteHealthcheckDetail = 301;
    public final int maxPhotoCount = 5;
    public final int maxDescribeCount = 200;

    int tagUploadDescribe = 7;
    String uploadSelfUrl = "/self/upload";
    int tagUploadPhoto = 8;
    String uploadSelfFileUrl = "/self/upload/file";

    int tagHealthcheckLoadMore = 21;
    String healthcheckLoadMoreUrl = "/self/list/%s/%s/%d/%d";
    //{orgcode}/{idcard}/{uploadtype}/{startindex}


    String mydescribeDetailPhotoUrl = "s/mydescribe/%s/%s/%s/%s";
    //{idcard0-6}/{idcard}/{date}/{name}
    int tagHealthcheckDetailDownload = 22;
    String healthcheckDetailDownloadUrl = "s/healthcheck/%s/%s/%s/%s";
    //{idcard0-6}/{idcard}/{date}/{name}

    int tagselfDelete = 23;
    String selfDeleteUrl = "/self/delete";

    int tagMyDescribeDetailDelete = 12;
    int tagHealthcheckDetailDelete = 24;
    String selfDetailDeleteUrl = "/self/delete/detail";

    String baseFolder = Environment.getExternalStorageDirectory().getPath()+ File.separator+"JiayuSoft"+ File.separator;
    String updateFolder = baseFolder + "update" + File.separator;
    String healthCheckFolder = baseFolder + "healthCheck" + File.separator;


}
