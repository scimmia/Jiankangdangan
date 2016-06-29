package com.jiayusoft.mobile.shengli.emr.community.selfupload;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import com.jiayusoft.mobile.utils.app.cardview.CardItem;
import com.squareup.phrase.Phrase;

import java.util.LinkedList;

/**
 * Created by ASUS on 2015/3/17.
 */
public class SelfUpload implements CardItem {
    String serialNum;
    String orgCode;
    String idCard;
    String describe;
    String uploadTime;
    int uploadType; // 1病情自述，2健康查体
    LinkedList<String> fileNames;

    public SelfUpload() {
        fileNames = new LinkedList<String>();
    }

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

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public int getUploadType() {
        return uploadType;
    }

    public void setUploadType(int uploadType) {
        this.uploadType = uploadType;
    }

    public LinkedList<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(LinkedList<String> fileNames) {
        this.fileNames = fileNames;
    }

    public int getFileCount(){
        if (fileNames!=null){
            return fileNames.size();
        }else{
            return 0;
        }
    }

    public String getFileName(int position){
        if (fileNames!=null && position>=0 && position <fileNames.size()){
            return fileNames.get(position);
        }else{
            return null;
        }
    }

    @Override
    public CharSequence getText() {
        return Phrase.from(getTemplate())
                .put("uploadtime", uploadTime != null ? uploadTime : "无")
                .put("selfdescribe", describe!=null?describe:"无")
                .put("filenum", getFileCount())
                .format();
    }
    static SpannableStringBuilder template;
    static SpannableStringBuilder getTemplate(){
        if (template == null){
            int color = Color.parseColor("#FFffffff");
            template = new SpannableStringBuilder(
                    "上传时间: {uploadtime}\n描述: {selfdescribe}\n文件数: {filenum}");
            template.setSpan(new ForegroundColorSpan(color), 6, 18, 0);
            template.setSpan(new ForegroundColorSpan(color), 23, 37, 0);
            template.setSpan(new ForegroundColorSpan(color), 43, 52, 0);
        }
        return template;
    }
}
