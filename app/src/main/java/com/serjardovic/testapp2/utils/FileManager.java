package com.serjardovic.testapp2.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;

public class FileManager {

    private static FileManager fileManager = null;

    private FileCache fileCache;


    private FileManager(Context context){

        fileCache = new FileCache(context);

    }

    public static FileManager getFileManager(Context context){
        if(fileManager == null){
            fileManager = new FileManager(context);
        }
        return fileManager;
    }

    public boolean isFileInCache(String fileName) {
        File file = fileCache.getFile(fileName);
        return file.exists();
    }

    public boolean isFileReady(String fileName) {
        if(isFileInCache(fileName)) {

            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                File file = fileCache.getFile(fileName);
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                Bitmap emptyBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), myBitmap.getConfig());
                if (myBitmap.sameAs(emptyBitmap)) {
                    // myBitmap is empty/blank
                    Log.d("ALPHA", "File is transparent: " + fileName);
                    return false;
                }

                Log.d("ALPHA", "File is okay: " + fileName);

                return true;
            } catch (Exception e) {
                e.printStackTrace();

                Log.d("ALPHA", "File is corrupt. Deleting from cache: : " + fileName);

                if(fileCache.getFile(fileName).delete()){
                    Log.d("ALPHA", "File successfully deleted from cache: " + fileName);
                } else {
                    Log.d("ALPHA", "Unable to delete file: " + fileName);
                }

                return false;
            }

        }
        return false;
    }

    public void deleteFileFromCache(String fileName) {
        fileCache.getFile(fileName).delete();
    }

    public File getFile(String fileName) {
        return fileCache.getFile(fileName);
    }
}
