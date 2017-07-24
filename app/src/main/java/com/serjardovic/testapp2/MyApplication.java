package com.serjardovic.testapp2;

import android.app.Application;
import android.content.Context;

import java.util.List;
import java.util.Map;

public class MyApplication extends Application {

    private int appCode, displayWidth, displayHeight;
    private Adapter adapter;
    private List<String> linkList;
    public Map<Integer, POJOItem> imageMap;
    public MainActivity mainActivity;

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public Map<Integer, POJOItem> getImageMap() {
        return imageMap;
    }

    public void setImageMap(Map<Integer, POJOItem> imageMap) {
        this.imageMap = imageMap;
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

    public List<String> getLinkList() {
        return linkList;
    }

    public void setLinkList(List<String> linkList) {
        this.linkList = linkList;
    }
}
