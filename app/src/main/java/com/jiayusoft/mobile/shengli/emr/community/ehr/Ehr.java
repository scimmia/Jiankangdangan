package com.jiayusoft.mobile.shengli.emr.community.ehr;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import com.squareup.phrase.Phrase;

/**
 * Created by Administrator on 15-3-3.
 */
public class Ehr {
    String serialNum;
    String orgCode;
    String idCard;
    String selfDescribe;
    String uploadTime;
    int photoNum;

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getSelfDescribe() {
        return selfDescribe;
    }

    public void setSelfDescribe(String selfDescribe) {
        this.selfDescribe = selfDescribe;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public int getPhotoNum() {
        return photoNum;
    }

    public void setPhotoNum(int photoNum) {
        this.photoNum = photoNum;
    }

    public CharSequence getText() {

        CharSequence formatted = Phrase.from(getTemplate())
                .put("uploadtime", uploadTime!=null?uploadTime:"无")
                .put("selfdescribe", selfDescribe!=null?selfDescribe:"无")
                .put("photonum", photoNum)
                .format();
        return formatted;
    }
    static SpannableStringBuilder template;
    static SpannableStringBuilder getTemplate(){
        if (template == null){
            int color = Color.parseColor("#FFffffff");
            template = new SpannableStringBuilder(
                    "上传时间: {uploadtime}\n描述: {selfdescribe}\n图片数: {photonum}");
            template.setSpan(new ForegroundColorSpan(color), 6, 18, 0);
            template.setSpan(new ForegroundColorSpan(color), 23, 37, 0);
            template.setSpan(new ForegroundColorSpan(color), 43, 53, 0);
        }
        return template;
    }
}
