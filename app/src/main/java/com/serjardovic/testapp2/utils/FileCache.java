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

    public File getFile(String fileName) {
        //I identify mImages by hashcode. Not a perfect solution, good for the demo.
        String filename = String.valueOf(fileName.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        return new File(cacheDirectory, filename);

    }

    public boolean renameFile(String oldName, String newName) {
        String filename1 = String.valueOf(oldName.hashCode());
        String filename2 = String.valueOf(newName.hashCode());
        File file = new File(cacheDirectory, filename1);
        File file2 = new File(cacheDirectory, filename2);
        return file.renameTo(file2);

    }

    public void clear() {
        File[] files = cacheDirectory.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

    public void clearTemp() {
        File[] files = cacheDirectory.listFiles();
        if (files == null)
            return;

        for(File file : files) {
            if(file.getPath().endsWith("TEMP"))
                file.delete();
        }
    }
}
