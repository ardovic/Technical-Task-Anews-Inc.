package com.serjardovic.testapp2;

import android.content.Context;
import android.util.Log;

import com.serjardovic.testapp2.utils.FileManager;

import java.util.LinkedList;
import java.util.List;

public class DownloadManager {

    private static DownloadManager downloadManager = null;

    public MyApplication mApplication;
    public FileManager mFileManager;
    public LinkedList<String> mQueue;
    public List<String> mImages;
    private Context context;

    private DownloadManager(Context context) {

        this.context = context;
        mApplication = (MyApplication) context.getApplicationContext();
        mFileManager = FileManager.getFileManager(context);
        mImages = mApplication.getModel().getImageDataInfo().getImageData().getImages();
        mQueue = mApplication.getModel().getDownloadQueue();
    }

    public static DownloadManager getDownloadManager(Context context) {
        if (downloadManager == null) {
            downloadManager = new DownloadManager(context);
        }
        return downloadManager;
    }

    public void downloadNextItemInQue() {

        if (mApplication.getModel().getDownloadQueue().size() > 0) {

            StringBuilder que = new StringBuilder("Current que: ");
            for (String item : mApplication.getModel().getDownloadQueue()) {

                String[] parts = item.split("/");

                String shortItem = parts[parts.length - 1];

                que.append(shortItem + ", ");
            }
            Log.d("ALPHA", que.toString().substring(0, que.toString().length() - 2) + ".");
        }
        // Get the URL of the first image in line for download
        // Log.d("ALPHA", "Download queue size: " + mApplication.getModel().getDownloadQueue().size());

        if (!mApplication.getModel().getDownloadQueue().isEmpty()) {

            String imageURL = mApplication.getModel().getDownloadQueue().getFirst();

            if (!mFileManager.isFileInCache(imageURL)) {
                Log.d("ALPHA", "Downloading image - " + imageURL);
                if (mImages.indexOf(imageURL) != -1) {
                    new DownloadImages((Callback) context).execute(mImages.indexOf(imageURL) + "");
                } else {
                    downloadNextItemInQue();
                }
            } else {
                Log.d("ALPHA", "Image already in cache - " + imageURL);
                mApplication.getModel().getDownloadQueue().remove(imageURL);
                downloadNextItemInQue();
            }
        } else {

            Log.d("ALPHA", "Download queue is empty!");
        }

    }


}
