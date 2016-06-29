package com.jiayusoft.mobile.utils.app.clientinfo;


/**
 * Created with IntelliJ IDEA.
 * User: mac
 * Date: 12-6-21
 * Time: ����10:58
 * To change this template use File | Settings | File Templates.
 */
public class ClientinfoItem {
    Class<?> launche;
    int mPicId;
    int mTitleId;

    public ClientinfoItem(Class<?> launche, int mPicId, int mTitleId) {
        this.launche = launche;
        this.mPicId = mPicId;
        this.mTitleId = mTitleId;
    }

    public Class<?> getLaunche() {
        return launche;
    }

    public void setLaunche(Class<?> launche) {
        this.launche = launche;
    }

    public int getmPicId() {
        return mPicId;
    }

    public void setmPicId(int mPicId) {
        this.mPicId = mPicId;
    }

    public int getmTitleId() {
        return mTitleId;
    }

    public void setmTitleId(int mTitleId) {
        this.mTitleId = mTitleId;
    }

    @Override
    public String toString() {
        return "ClientinfoItem{" +
                "launche=" + launche +
                ", mPicId=" + mPicId +
                ", mTitleId=" + mTitleId +
                '}';
    }
}