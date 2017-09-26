package com.serjardovic.testapp2.model.images;

import com.serjardovic.testapp2.MyApplication;
import com.serjardovic.testapp2.interfaces.NetworkListener;
import com.serjardovic.testapp2.network.DownloadImageAsyncTask;
import com.serjardovic.testapp2.utils.L;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ImageInfo implements NetworkListener<String> {

    private boolean downloadActive = false;
    private LinkedList<String> downloadQueue;

    public boolean isDownloadActive() {
        return downloadActive;
    }

    public void setDownloadActive(boolean downloadActive) {
        this.downloadActive = downloadActive;
    }

    public LinkedList<String> getDownloadQueue() {
        return downloadQueue;
    }

    public ImageInfo() {
        downloadQueue = new LinkedList<>();
    }

    public void addImageToQueueEnd(String imageURL) {
        if(!downloadQueue.contains(imageURL)) {
            L.d("Image added to queue end: " + imageURL);
            downloadQueue.addLast(imageURL);
        }
    }
    public void addImageToQueueStart(String imageURL) {
        if(!downloadQueue.isEmpty() && !downloadQueue.getFirst().equals(imageURL)) {
            L.d("Image added to queue start: " + imageURL);
            downloadQueue.remove(imageURL);
            downloadQueue.addFirst(imageURL);
            L.q();
        } else {
            L.d("Image added to queue start: " + imageURL);
            downloadQueue.addFirst(imageURL);
            L.q();
        }
    }
    public void removeImageFromQueue(String imageURL) {
        if(!downloadQueue.isEmpty() && downloadQueue.contains(imageURL)) {
            L.d("Image removed from queue: " + imageURL);
            downloadQueue.remove(imageURL);
            L.q();
        }
    }

    @Override
    public void onSuccess(String imageURL) {

        downloadActive = false;
        removeImageFromQueue(imageURL);

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < MyApplication.getInstance().getModel().pageInfo.getPageData().getImages().size(); i++) {
            if (imageURL.equals(MyApplication.getInstance().getModel().pageInfo.getPageData().getImages().get(i))) {
                indices.add(i);
            }
        }

        for(int i : indices) {
//            MyApplication.getInstance().getCallback().notifyItem(i);
        }

        if (!downloadQueue.isEmpty() && !downloadActive) {
            downloadActive = true;
            new DownloadImageAsyncTask(MyApplication.getInstance(), this).execute(downloadQueue.getFirst());
        }
    }

    @Override
    public void onError(String... error) {

        downloadActive = false;
        removeImageFromQueue(error[0]);

        if (error.length > 1 && error[1].equals("File not found")) {
            MyApplication.getInstance().getModel().pageInfo.getPageData().changeImageName(error[0], error[1] + ": " + error[0]);
        }

        if (!downloadQueue.isEmpty() && !downloadActive) {
            downloadActive = true;
            new DownloadImageAsyncTask(MyApplication.getInstance(), this).execute(downloadQueue.getFirst());
        }

    }
}
