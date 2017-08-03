package com.serjardovic.testapp2;

import android.content.Context;
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

import com.serjardovic.testapp2.utils.FileCache;

import java.io.File;

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

        fileCache = new FileCache(this);

        // Declaring all UI components
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_layer);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loader);

        if (mApplication.getModel().getSinglePostResponseList().size() == 0) {
            // Upon first ever launch send the first POST request
            sendPostRequest(1);
        } else {
            manageSituation();
        }
    }

    public void manageSituation() {
            initializeRecycler();
            displayList();

        for (int i = 0; i < mApplication.getModel().getSinglePostResponseList().size(); i++) {
            for (int j = 0; j < mApplication.getModel().getSinglePostResponseList().get(i).getImages().length; j++) {
                String current_page = i + "";
                String item_on_page = j + "";
                String fileName = mApplication.getModel().getSinglePostResponseList().get(i).getImages()[j];
                File file = fileCache.getFile(fileName);
                if (!file.exists()) {
                    new DownloadImages(this).execute(current_page, item_on_page);
                }
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
            mApplication.setAdapter(new Adapter(mApplication.getModel().getSinglePostResponseList(), MainActivity.this));
            mAdapter = mApplication.getAdapter();
        }
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                // do something...
                int page = mApplication.getModel().getSinglePostResponseList().size() + 1;
                Log.d("ALPHA", "Sending POST Request for page: " + page);

                sendPostRequest(page);
            }
        });


    }

    public void sendPostRequest(int page) {

        if (page != 0) {
            new SendPostRequest(this).execute(page + "");
        } else {
            Log.d("ALPHA", "No more pages left!");
        }
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

