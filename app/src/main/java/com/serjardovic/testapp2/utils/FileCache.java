package com.serjardovic.testapp2.utils;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileCache {

    private static FileCache instance;

    private File cacheDirectory;

    private FileCache(Context context) {
        //Find the dir to save cached mImages
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED))
            cacheDirectory = new File(Environment.getExternalStorageDirectory(), "TTImages_cache");
        else
            cacheDirectory = context.getCacheDir();
        if (!cacheDirectory.exists())
            cacheDirectory.mkdirs();
    }

    public static FileCache getInstance(Context context) {
        if (instance == null) {
            instance = new FileCache(context);
        }
        return instance;
    }

    public File getFile(String url) {
        //I identify mImages by hashcode. Not a perfect solution, good for the demo.
        String filename = String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        return new File(cacheDirectory, filename);

    }

    public void clear() {
        File[] files = cacheDirectory.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

}
