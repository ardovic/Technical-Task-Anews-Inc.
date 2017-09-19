package com.serjardovic.testapp2;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.serjardovic.testapp2.model.Model;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    private Model model;
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private int visibleThreshold = 2; // The minimum amount of items to have below your current scroll position before loading more.
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    private LinearLayoutManager layoutManager;

    public EndlessRecyclerOnScrollListener(Context context, LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        model = ((MyApplication) context.getApplicationContext()).getModel();
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        if (!model.isPostReady()) {

            if (totalItemCount > previousTotal) {
                previousTotal = totalItemCount;
            }
        }

        if (model.isPostReady() && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

            // End has been reached
            onLoadMore();
        }
    }

    public abstract void onLoadMore();
}
