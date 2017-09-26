package com.serjardovic.testapp2;

import android.app.Application;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.serjardovic.testapp2.interfaces.NotifyCallback;
import com.serjardovic.testapp2.model.images.dto.PageData;
import com.serjardovic.testapp2.utils.CoreManager;
import com.serjardovic.testapp2.model.Model;
import com.serjardovic.testapp2.utils.ImageLoader;
import com.serjardovic.testapp2.utils.L;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private Model model;

    private int displayWidth;
    private int displayHeight;
    private int numberOfCores;

    private static MyApplication instance;

    private NotifyCallback callback;


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

        ImageLoader.createInstance(this);
    }

    public void registerCallback(NotifyCallback callback) {
        this.callback = callback;
    }

    public NotifyCallback getCallback() {
        return callback;
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
