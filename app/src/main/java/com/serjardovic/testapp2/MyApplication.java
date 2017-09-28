package com.serjardovic.testapp2;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.serjardovic.testapp2.utils.CoreManager;
import com.serjardovic.testapp2.model.Model;
import com.serjardovic.testapp2.utils.L;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyApplication extends Application {

    private Model model;

    private int displayWidth;
    private int displayHeight;
    private int numberOfCores;

    private static MyApplication instance;

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

        Executor downloadExecutor = Executors.newFixedThreadPool(numberOfCores);
        Executor cachedExecutor = Executors.newSingleThreadExecutor();
        //ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //int memClass = am.getMemoryClass();
        //final int memoryCacheSize = 1024 * 1024 * memClass / 8;
        final int memoryCacheSize = (int)Runtime.getRuntime().maxMemory() / 2; // Half of the available memory
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(android.R.color.transparent)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
//              .showImageOnLoading(R.drawable.ic_circle_place_holder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(500, true, false, false))
                .build();
        File cacheDir = StorageUtils.getCacheDirectory(this);

        ImageLoaderConfiguration imageLoaderConfig = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .taskExecutor(downloadExecutor)
                .taskExecutorForCachedImages(cachedExecutor)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCache(new UsingFreqLimitedMemoryCache(memoryCacheSize))
                .diskCache(new LimitedAgeDiskCache(cacheDir, 100 * 1024 * 1024))
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)
                .defaultDisplayImageOptions(options)
                .build();
        ImageLoader.getInstance().init(imageLoaderConfig);
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

}
