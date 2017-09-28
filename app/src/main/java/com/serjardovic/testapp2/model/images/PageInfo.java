package com.serjardovic.testapp2.model.images;

import com.serjardovic.testapp2.MyApplication;
import com.serjardovic.testapp2.interfaces.ApiInterface;
import com.serjardovic.testapp2.interfaces.NetworkListener;
import com.serjardovic.testapp2.model.images.dto.PageData;
import com.serjardovic.testapp2.network.ApiClient;
import com.serjardovic.testapp2.network.RequestData;
import com.serjardovic.testapp2.utils.L;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PageInfo {

    private boolean mIsUpdating = false;
    private NetworkListener<PageData> mListener;
    private PageData mPageData;
    private ApiInterface apiInterface;

    public PageInfo() {}

    public void setNetworkListener(NetworkListener<PageData> listener) {
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
        int page = 1;

        if(MyApplication.getInstance().getModel().getPageInfo().getPageData() != null) {
            page = MyApplication.getInstance().getModel().getPageInfo().getPageData().getNextPage();
        }

        if(mPageData.hasNextPage()) {
            apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Call<PageData> call = apiInterface.getPageData(new RequestData(page));
            call.enqueue(new Callback<PageData>() {
                @Override
                public void onResponse(Call<PageData> call, Response<PageData> response) {
                    mIsUpdating = false;
                    mListener.onSuccess(response.body());
                    if (mPageData == null) {
                        mPageData = response.body();
                    } else {
                        mPageData.updateData(response.body());
                    }
                }

                @Override
                public void onFailure(Call<PageData> call, Throwable t) {
                    mIsUpdating = false;
                    mListener.onError();
                }
            });
        } else {
            L.d("No more pages left...");
        }
    }
}
