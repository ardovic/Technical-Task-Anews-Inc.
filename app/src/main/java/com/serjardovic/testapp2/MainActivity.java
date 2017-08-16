package com.serjardovic.testapp2;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.serjardovic.testapp2.utils.FileManager;

import java.util.List;

public class MainActivity extends AppCompatActivity implements Callback {

    public MyApplication mApplication;
    public RecyclerView mRecyclerView;
    public LinearLayoutManager mLayoutManager;
    public ProgressBar mProgressBar;
    public LinearLayout mLinearLayout;
    public Adapter mAdapter;
    public List<String> mImages;
    public FileManager mFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connecting to MyApplication class
        mApplication = (MyApplication) getApplicationContext();
        mImages = mApplication.getModel().getImageDataInfo().getImageData().getImages();
        mFileManager = FileManager.getFileManager(this);

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        mApplication.setDisplayWidth(point.x);
        mApplication.setDisplayHeight(point.y);
        Log.d("ALPHA", "Device screen resolution: " + point.x + " x " + point.y);

        // Declaring all UI components
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_layer);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loader);

        if (mImages.size() == 0) {
            // Upon first ever launch send the first POST request
            sendPostRequest(1);
        } else {
            manageSituation();
        }

    }

    public void manageSituation() {
        if (mAdapter == null) {
            initializeRecycler();
            displayList();
        }

        // Get the URL of the first image in line for download
        Log.d("ALPHA", "Download queue size: " + mApplication.getModel().getDownloadQueue().size());

        if(!mApplication.getModel().getDownloadQueue().isEmpty()) {
            String imageURL = mApplication.getModel().getDownloadQueue().getFirst();
            if (!mFileManager.isFileInCache(imageURL)) {
                Log.d("ALPHA", "Downloading image - " + imageURL);
                new DownloadImages(this).execute(mImages.indexOf(imageURL) + "");
            } else {
                Log.d("ALPHA", "Image already in cache - " + imageURL);
                mApplication.getModel().getDownloadQueue().removeFirst();
                manageSituation();
            }
        } else {
            Log.d("ALPHA", "Download queue is empty!");
        }

        /*
        for (String imageURL : mImages) {
            if (!imageURL.substring(imageURL.length() - 3).equals("404")) {
                if(!mFileManager.isFileInCache(imageURL)) {
                    Log.d("ALPHA", "Downloading image - " + imageURL);
                    new DownloadImages(this).execute(mImages.indexOf(imageURL) + "");
                }
            }
        }
        */
    }

    @Override
    protected void onDestroy() {
        if (mApplication.getCurrentDownload() != null) {
            mFileManager.deleteFileFromCache(mApplication.getCurrentDownload());
            Log.d("ALPHA", "Deleting unfinished download - " + mApplication.getCurrentDownload());
        }
        super.onDestroy();
    }

    public void initializeRecycler() {
        if (mApplication.getAdapter() != null) {
            mAdapter = mApplication.getAdapter();
        } else {
            mApplication.setAdapter(new Adapter(mApplication.getModel().getImageDataInfo(), this));
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

                if (nextPage != 0) {

                    Log.d("ALPHA", "Sending POST Request for page: " + nextPage);
                    sendPostRequest(nextPage);
                } else {
                    Log.d("ALPHA", "No more pages left!");
                    mApplication.setReady(false);
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

