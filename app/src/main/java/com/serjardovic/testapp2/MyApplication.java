package com.serjardovic.testapp2;

import android.app.Application;

public class MyApplication extends Application {

    private int appCode, displayWidth, displayHeight;
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
}
