package com.serjardovic.testapp2.model.images;

import com.serjardovic.testapp2.utils.L;

import java.util.ArrayList;
import java.util.List;

public class ImageInfo {

    public boolean downloadActive = false;
    public List<String> downloadQueue;

    public ImageInfo() {
        downloadQueue = new ArrayList<>();
    }

    public void addImageToQueueEnd(String imageURL) {
        if(!downloadQueue.contains(imageURL)) {
            L.d("Image added to queue end: " + imageURL);
            downloadQueue.add(imageURL);
        }
    }
    public void addImageToQueueStart(String imageURL) {
        if(!downloadQueue.isEmpty() && !downloadQueue.get(0).equals(imageURL)) {
            L.d("Image added to queue start: " + imageURL);
            downloadQueue.remove(imageURL);
            downloadQueue.add(0, imageURL);
            L.q();
        } else {
            L.d("Image added to queue start: " + imageURL);
            downloadQueue.add(imageURL);
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


}
