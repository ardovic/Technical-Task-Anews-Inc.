package com.serjardovic.testapp2;

import android.app.Application;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.serjardovic.testapp2.model.images.dto.PageData;
import com.serjardovic.testapp2.utils.CoreManager;
import com.serjardovic.testapp2.model.Model;
import com.serjardovic.testapp2.utils.L;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private Model model;

    private int displayWidth;
    private int displayHeight;
    private int numberOfCores;

    private static MyApplication instance;

    // TODO temp
    public boolean downloadActive = false;
    public List<String> downloadQueue;
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



    @Override
    public void onCreate() {
        super.onCreate();

        model = new Model();
        instance = this;
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        if(point.x > point.y){
            displayWidth = point.y;
            displayHeight = point.x;
        } else {
            displayWidth = point.x;
            displayHeight = point.y;
        }
        L.d("Device screen resolution: " + displayWidth + " x " + displayHeight);

        numberOfCores = CoreManager.getNumberOfCores();
        L.d("Number of cores available on the device: " + numberOfCores);

        downloadQueue = new ArrayList<>();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public Model getModel() {
        return model;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public int getNumberOfCores() {
        return numberOfCores;
    }
}
