package com.serjardovic.testapp2;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    public MyApplication mApplication;
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    //private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 2; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;


    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager, Callback callback) {
        this.mLinearLayoutManager = linearLayoutManager;
        mApplication = (MyApplication) callback.getContext().getApplicationContext();
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (!mApplication.isReady()) {

            if (totalItemCount > previousTotal) {

                //loading = false;
                previousTotal = totalItemCount;
            }
        }

        if (mApplication.isReady() && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            onLoadMore();

            //loading = true;

        }
    }



    public abstract void onLoadMore();
}
