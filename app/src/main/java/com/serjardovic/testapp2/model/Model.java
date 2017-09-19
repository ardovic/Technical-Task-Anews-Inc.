package com.serjardovic.testapp2.model;

import android.app.Application;
import android.os.AsyncTask;

import com.serjardovic.testapp2.MyApplication;

import java.util.List;

public class Model {

    private MyApplication application;
    private ImageDataInfo imageDataInfo;
    private DownloadImageAsyncTask downloadTask;
    private PostRequestAsyncTask postRequestTask;
    private boolean postReady;

    public Model(Application application) {
        this.application = (MyApplication) application;
        imageDataInfo = new ImageDataInfo();
    }

    public void downloadImages() {

        if (downloadTask == null || downloadTask.getStatus() == AsyncTask.Status.FINISHED) {
            downloadTask = new DownloadImageAsyncTask(application);
            downloadTask.execute();
        }
    }

    public void sendPostRequest() {

        postRequestTask = new PostRequestAsyncTask(application);
        postRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageDataInfo.getImageData().getNextPage());

    }

    public ImageDataInfo getImageDataInfo() {
        return imageDataInfo;
    }

    public AsyncTask.Status getPostRequestTaskStatus() {
        return postRequestTask.getStatus();
    }

    public boolean isPostReady() {
        return postReady;
    }

    public void setPostReady(boolean postReady) {
        this.postReady = postReady;
    }
}
