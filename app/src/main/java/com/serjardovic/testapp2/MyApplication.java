package com.serjardovic.testapp2;

import android.app.Activity;
import android.app.Application;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.serjardovic.testapp2.model.CoreManager;
import com.serjardovic.testapp2.model.Model;

public class MyApplication extends Application {

    private Model model;

    private Callback callback;

    private int displayWidth;
    private int displayHeight;
    private int numberOfCores;

    @Override
    public void onCreate() {
        super.onCreate();

        model = new Model(this);

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        displayWidth = point.x;
        displayHeight = point.y;
        L.d("Device screen resolution: " + displayWidth + " x " + displayHeight);

        numberOfCores = CoreManager.getNumberOfCores();
        L.d("Number of cores available on the device: " + numberOfCores);
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

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Callback getCallback() {
        return callback;
    }
}
