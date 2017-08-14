package com.serjardovic.testapp2;

import android.app.Application;

public class MyApplication extends Application {

    private int appCode;
    private int displayWidth;
    private int displayHeight;
    private int numberOfCores;
    private Model model;
    private Adapter adapter;

    @Override
    public void onCreate() {
        super.onCreate();
        model = new Model();
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public int getAppCode() {
        return appCode;
    }

    public void setAppCode(int appCode) {
        this.appCode = appCode;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayWidth(int displayWidth) {
        this.displayWidth = displayWidth;
    }

    public int getDisplayHeight() {
        return displayHeight;
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

    public void setNumberOfCores(int numberOfCores) {
        this.numberOfCores = numberOfCores;
    }
}
