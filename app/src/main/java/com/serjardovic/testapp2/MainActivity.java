package com.serjardovic.testapp2;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.serjardovic.testapp2.utils.FileCache;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Callback {

    public MyApplication mApplication;
    public RecyclerView mRecyclerView;
    public LinearLayoutManager mLayoutManager;
    public ProgressBar mProgressBar;
    public LinearLayout mLinearLayout;
    public Adapter mAdapter;
    public FileCache fileCache;
    public List<String> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connecting to MyApplication class
        mApplication = (MyApplication) getApplicationContext();
        images = mApplication.getModel().getImageDataInfo().getImageData().getImages();

        fileCache = new FileCache(this);

        // Declaring all UI components
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_layer);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loader);





        if (images.size() == 0) {
            // Upon first ever launch send the first POST request
            sendPostRequest(1);
        } else {
            manageSituation();
        }
    }

    public void manageSituation() {
        if(mAdapter == null){
            initializeRecycler();
            displayList();
        }

        for(String imageUrl : images) {
            File file = fileCache.getFile(imageUrl);
            int itemIndex = images.indexOf(imageUrl);
            if (!file.exists()) {
                new DownloadImages(this).execute(itemIndex + "");
            }
        }
    }


    @Override
    protected void onDestroy() {
        //mRecyclerView.setAdapter(null);
        super.onDestroy();
    }

    public void initializeRecycler() {
        if (mApplication.getAdapter() != null) {
            mAdapter = mApplication.getAdapter();
        } else {
            mApplication.setAdapter(new Adapter(mApplication.getModel().getImageDataInfo().getImageData(), this));
            mAdapter = mApplication.getAdapter();
        }
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager, this) {
            @Override
            public synchronized void onLoadMore() {

                int nextPage = mApplication.getModel().getImageDataInfo().getImageData().getNextPage();

                if(nextPage != 0) {

                    Log.d("ALPHA", "Sending POST Request for page: " + nextPage);
                    sendPostRequest(nextPage);
                } else {
                    Log.d("ALPHA", "No more pages left!");
                }
            }
        });
    }

    public void sendPostRequest(int page) {

            new SendPostRequest(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page + "");

    }

    public void displayList() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    public Adapter getAdapter() {
        return mAdapter;
    }

    public Context getContext() {
        return this;
    }
}

