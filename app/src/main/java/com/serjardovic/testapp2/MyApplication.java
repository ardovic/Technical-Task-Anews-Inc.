package com.serjardovic.testapp2;

import android.app.Application;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.serjardovic.testapp2.utils.CoreManager;

import java.util.ArrayList;

public class MyApplication extends Application {

    private int displayWidth;
    private int displayHeight;
    private int numberOfCores;
    private Model model;
    private Adapter adapter;

    @Override
    public void onCreate() {
        super.onCreate();
        model = new Model();

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        displayWidth = point.x;
        displayHeight = point.y;

        model.setImageDataInfo(new ImageDataInfo());
        model.getImageDataInfo().setImageData(new ImageData(new ArrayList<String>(), 1, 0));

        Log.d("ALPHA", "Device screen resolution: " + displayWidth + " x " + displayHeight);

        numberOfCores = CoreManager.getNumberOfCores();

        Log.d("ALPHA", "Number of cores available on the device: " + numberOfCores);

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

    public Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    public int getNumberOfCores() {
        return numberOfCores;
    }
}
