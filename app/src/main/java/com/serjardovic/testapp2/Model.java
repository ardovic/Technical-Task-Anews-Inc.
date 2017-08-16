package com.serjardovic.testapp2;

import java.util.LinkedList;

public class Model {

    private ImageDataInfo imageDataInfo;
    private LinkedList<String> downloadQueue;

    Model() {
        imageDataInfo = new ImageDataInfo();
        downloadQueue = new LinkedList<>();
    }

    public ImageDataInfo getImageDataInfo() {
        return imageDataInfo;
    }

    public void setImageDataInfo (ImageDataInfo imageDataInfo) {
        this.imageDataInfo = imageDataInfo;
    }

    public LinkedList<String> getDownloadQueue() {
        return downloadQueue;
    }

    public void setDownloadQueue(LinkedList<String> downloadQueue) {
        this.downloadQueue = downloadQueue;
    }
}
