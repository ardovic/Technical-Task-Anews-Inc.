package com.serjardovic.testapp2.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.serjardovic.testapp2.Callback;
import com.serjardovic.testapp2.MyApplication;
import com.serjardovic.testapp2.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    private FileManager fileManager;
    private MyApplication myApplication;
    private MemoryCache memoryCache = new MemoryCache();
    private Callback callback;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService executorService;

    public ImageLoader(Callback callback) {
        myApplication = (MyApplication) callback.getContext().getApplicationContext();
        this.callback = callback;
        fileManager = FileManager.getFileManager(callback.getContext());
        executorService = Executors.newFixedThreadPool(myApplication.getNumberOfCores() - 1);
    }

    public void DisplayImage(String url, ImageView imageView) {
        if (url.substring(0, 3).equals("404")) {
            imageViews.put(imageView, "404");
            Bitmap bm = BitmapFactory.decodeResource(callback.getContext().getResources(), R.drawable.error404);
            imageView.setImageBitmap(bm);
        } else {
            imageViews.put(imageView, url);
            Bitmap bitmap = memoryCache.get(url);
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
            else {
                queuePhoto(url, imageView);
            }
        }
    }

    private void queuePhoto(String url, ImageView imageView) {

        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String imageURL) {
        File f = fileManager.getFile(imageURL);

        //from SD cache
        Bitmap b = decodeFile(f);
        if (b != null) {
            return b;
        } else {
            if(!fileManager.isFileReady(imageURL) && !myApplication.getModel().getDownloadQueue().contains(imageURL) && !myApplication.getCurrentDownload().equals(imageURL)) {

                Log.d("ALPHA", "File added to priority queue for re-download: " + imageURL);
                // Add URL to priority
                myApplication.getModel().getDownloadQueue().addFirst(imageURL);
                manageSituation(callback);

            } else if (!fileManager.isFileReady(imageURL)){
                manageSituation(callback);
            }
        }

        return null;
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //o.outWidth = MainActivity.screenWidth;
            //o.outHeight = 2 * (o.outWidth / 3);

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = myApplication.getDisplayWidth() / 2;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;

            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 0.5;
                scale *= 2;
            }


            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Task for the queue
    private class PhotoToLoad {
        String url;
        ImageView imageView;

        PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    private class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    private boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        return (tag == null || !tag.equals(photoToLoad.url));
    }

    //Used to display bitmap in the UI thread
    private class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
            }

        }
    }

    private void manageSituation(Callback callback) {
        callback.manageSituation();
    }
}