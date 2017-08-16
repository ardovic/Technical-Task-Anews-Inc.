package com.serjardovic.testapp2;

import android.app.Application;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.serjardovic.testapp2.utils.CoreManager;

import java.util.ArrayList;
import java.util.LinkedList;

public class MyApplication extends Application {

    private int displayWidth;
    private int displayHeight;
    private int numberOfCores;
    private Model model;
    private Adapter adapter;
    private String currentDownload;
    private boolean ready;

    @Override
    public void onCreate() {
        super.onCreate();
        model = new Model();

        model.setImageDataInfo(new ImageDataInfo());
        model.getImageDataInfo().setImageData(new ImageData(new ArrayList<String>(), 1, 0));
        model.setDownloadQueue(new LinkedList<String>());

        numberOfCores = CoreManager.getNumberOfCores();

        Log.d("ALPHA", "Number of cores available on the device: " + numberOfCores);

        ready = false;

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

    public void setDisplayWidth(int displayWidth) {
        this.displayWidth = displayWidth;
    }

    public void setDisplayHeight(int displayHeight) {
        this.displayHeight = displayHeight;
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    public int getNumberOfCores() {
        return numberOfCores;
    }

    public String getCurrentDownload() {
        return currentDownload;
    }

    public void setCurrentDownload(String currentDownload) {
        this.currentDownload = currentDownload;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
