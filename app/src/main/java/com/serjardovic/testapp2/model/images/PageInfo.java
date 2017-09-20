package com.serjardovic.testapp2.model.images;

import com.serjardovic.testapp2.interfaces.NetworkListener;
import com.serjardovic.testapp2.model.images.dto.PageData;
import com.serjardovic.testapp2.network.PostRequestAsyncTask;

public class PageInfo implements NetworkListener<PageData> {

    private boolean mIsUpdating = false;
    private NetworkListener<PageData> mListener;
    private PageData mPageData;


    public PageInfo() {

    }

    public void setNetworkListener(NetworkListener<PageData> listener){
        mListener = listener;
    }

    public boolean isUpdating() {
        return mIsUpdating;
    }

    public PageData getPageData() {
        return mPageData;
    }

    public void getListImagesByPage() {
        mIsUpdating = true;
        new PostRequestAsyncTask(this).execute(mPageData == null ? 1 : mPageData.getNextPage());
    }

    @Override
    public void onSuccess(PageData data) {
        mIsUpdating = false;
        mListener.onSuccess(data);
        if (mPageData == null){
            mPageData = data;
        }else {
            mPageData.updateData(data);
        }

    }

    @Override
    public void onError(String error) {
        mIsUpdating = false;
        mListener.onError(error);
    }
}
