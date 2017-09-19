package com.serjardovic.testapp2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Callback {

    public MyApplication mApplication;

    public RecyclerView mRecyclerView;
    public ProgressBar mProgressBar;
    public LinearLayout mLinearLayout;

    public Adapter mAdapter;
    public LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_layer);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loader);

        mApplication = (MyApplication) getApplicationContext();
        mApplication.setCallback(this);

        mAdapter = new Adapter(this);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(this, mLayoutManager) {
            @Override
            public synchronized void onLoadMore() {
                int nextPage = mApplication.getModel().getImageDataInfo().getImageData().getNextPage();
                if (nextPage != 0) {
                    L.d("Sending POST request...");
                    mApplication.getModel().sendPostRequest();
                } else {
                    L.d("No more pages left");
                    mRecyclerView.setOnScrollListener(null);
                }
            }
        });

        if(mApplication.getModel().getImageDataInfo().getImageData().getCurrentPage() == 0) {
            mApplication.getModel().sendPostRequest();
        } else {
            if(mLinearLayout.getVisibility() == View.VISIBLE) {
                mLinearLayout.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);

            }
        }
    }

    @Override
    public void imageDownloaded(String imageURL) {

        List<Integer> indices = new ArrayList<>();

        for(int i = 0; i < mApplication.getModel().getImageDataInfo().getImageData().getAllImages().size(); i++) {
            if(imageURL.equals(mApplication.getModel().getImageDataInfo().getImageData().getAllImages().get(i))) {
                indices.add(i);
            }
        }

        for(Integer i : indices) {
            if(mLinearLayout.getVisibility() == View.VISIBLE) {
                mLinearLayout.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
            }
            mAdapter.notifyItemChanged(i);
        }
    }

    @Override
    public void postExecuted() {
        mAdapter.notifyDataSetChanged();
    }
}

