package com.serjardovic.testapp2.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.serjardovic.testapp2.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoaderOld {

    private static ImageLoaderOld instance;

    private FileCache fileCache;
    private MemoryCache memoryCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService executorService;

    @Nullable
    public static ImageLoaderOld getInstance() {
        return instance;
    }

    public static ImageLoaderOld createInstance(Context context) {
        instance = new ImageLoaderOld(context);
        return instance;
    }

    private ImageLoaderOld(Context context) {
        fileCache = FileCache.getInstance(context);
        memoryCache = MemoryCache.getInstance();
        executorService = Executors.newFixedThreadPool(MyApplication.getInstance().getNumberOfCores() - 1);
        //fileCache.clearTemp();
    }


    public void displayImage(String url, ImageView imageView, ProgressBar progressBar) {

        if (fileCache.getFile(url).exists()) {

            imageViews.put(imageView, url);
            Bitmap bitmap = memoryCache.get(url);
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
            else {
                queuePhoto(url, imageView);
            }
            progressBar.setVisibility(View.GONE);
        } else {
            imageView.setImageDrawable(null);
        }

    }

    private void queuePhoto(String url, ImageView imageView) {

        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String imageURL) {
        File f = fileCache.getFile(imageURL);

        //from SD cache
        Bitmap b = decodeFile(f);
        if (b != null) {
            return b;
        } else {
            if (!imageURL.contains("Image not found")) {
                L.d("File is corrupt: " + imageURL + ". Deleting the file and re-downloading...");
                fileCache.getFile(imageURL).delete();

                MyApplication.getInstance().getModel().imageInfo.addImageToQueueStart(imageURL);
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
            final int REQUIRED_SIZE = MyApplication.getInstance().getDisplayWidth() / 2;
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
            BitmapDisplayer bitmapDisplayer = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bitmapDisplayer);
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
}
