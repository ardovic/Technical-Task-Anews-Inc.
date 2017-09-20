package com.serjardovic.testapp2.model.images;

import android.os.AsyncTask;

import com.serjardovic.testapp2.model.images.dto.ImageData;
import com.serjardovic.testapp2.network.DownloadImageAsyncTask;
import com.serjardovic.testapp2.network.PostRequestAsyncTask;

public class ImageDataInfo {

    private ImageData imageData;
    private DownloadImageAsyncTask downloadTask;
    private PostRequestAsyncTask postRequestTask;
    private boolean postReady;

    public ImageDataInfo() {

    }

//    public ImageData getImageData() {
//        return imageData;
//    }
//
//
//    public void downloadImages() {
//
//        if (downloadTask == null || downloadTask.getStatus() == AsyncTask.Status.FINISHED) {
//            downloadTask = new DownloadImageAsyncTask(application);
//            downloadTask.execute();
//        }
//    }
//
//    public void sendPostRequest() {
//
//        postRequestTask = new PostRequestAsyncTask(application);
//        postRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageDataInfo.getImageData().getNextPage());
//
//    }
//
//    public ImageDataInfo getImageDataInfo() {
//        return imageDataInfo;
//    }
//
//    public AsyncTask.Status getPostRequestTaskStatus() {
//        return postRequestTask.getStatus();
//    }
//
//    public boolean isPostReady() {
//        return postReady;
//    }
//
//    public void setPostReady(boolean postReady) {
//        this.postReady = postReady;
//    }

}
