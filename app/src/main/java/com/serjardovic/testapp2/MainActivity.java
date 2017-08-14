package com.serjardovic.testapp2;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Point;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.serjardovic.testapp2.utils.CoreManager;
import com.serjardovic.testapp2.utils.FileCache;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements Callback {

    public MyApplication mApplication;
    public RecyclerView mRecyclerView;
    public LinearLayoutManager mLayoutManager;
    public ProgressBar mProgressBar;
    public LinearLayout mLinearLayout;
    public Adapter mAdapter;
    public FileCache fileCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connecting to MyApplication class
        mApplication = (MyApplication) getApplicationContext();

        // Retrieving display parameters and passing them to MyApplication
        final Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mApplication.setDisplayWidth(size.x);
        mApplication.setDisplayHeight(size.y);
        Log.d("ALPHA", "Device screen resolution: " + mApplication.getDisplayWidth() + " x " + mApplication.getDisplayHeight());
        mApplication.setNumberOfCores(CoreManager.getNumberOfCores());
        Log.d("ALPHA", "Number of cores available on the device: " + CoreManager.getNumberOfCores());

        fileCache = new FileCache(this);

        // Declaring all UI components
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_layer);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loader);

        if (mApplication.getModel().getImageData().getImages().size() == 0) {
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

        for(String imageUrl : mApplication.getModel().getImageData().getImages()) {
            File file = fileCache.getFile(imageUrl);
            int itemIndex = mApplication.getModel().getImageData().getImages().indexOf(imageUrl);
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
            mApplication.setAdapter(new Adapter(mApplication.getModel().getImageData(), this));
            mAdapter = mApplication.getAdapter();
        }
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public synchronized void onLoadMore(int current_page) {

                if(mApplication.getModel().getImageData().getNext_page() != 0) {

                    Log.d("ALPHA", "Sending POST Request for page: " + mApplication.getModel().getImageData().getNext_page());

                    sendPostRequest(mApplication.getModel().getImageData().getNext_page());

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

