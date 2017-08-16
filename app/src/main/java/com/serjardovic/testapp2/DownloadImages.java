package com.serjardovic.testapp2;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.serjardovic.testapp2.utils.FileCache;
import com.serjardovic.testapp2.utils.FileManager;
import com.serjardovic.testapp2.utils.MemoryCache;
import com.serjardovic.testapp2.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class DownloadImages extends AsyncTask<String, Void, String> {

    //private FileCache fileCache;
    private FileManager fileManager;
    private MemoryCache memoryCache;
    private MyApplication mApplication;
    private Callback callback;

    DownloadImages(Callback callback) {

        this.callback = callback;
        mApplication = (MyApplication) callback.getContext().getApplicationContext();
        //fileCache = new FileCache(callback.getContext());
        memoryCache = new MemoryCache();
        fileManager = FileManager.getFileManager(callback.getContext());

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... params) {

        int itemIndex = Integer.parseInt(params[0]);

        String fileName;

        if (mApplication.getModel().getDownloadQueue().size() > 0) {
            fileName = mApplication.getModel().getDownloadQueue().getFirst();
        } else {
            return "Error: Concurrency leak" + "*" + "_____" + "*" + itemIndex;
        }
        mApplication.setCurrentDownload(fileName);

        File file = fileManager.getFile(fileName);

        if (file.exists())
            return "Success: File already exists" + "*" + fileName + "*" + itemIndex;
        try {
            URL imageUrl = new URL(fileName);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(file);
            Utils.CopyStream(is, os);
            os.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return "Error: 404" + "*" + fileName + "*" + itemIndex;
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return "Error: Out of memory, clearing memory cache" + "*" + fileName + "*" + itemIndex;
        }
        //this string is returned as result
        return "Success: File downloaded" + "*" + fileName + "*" + itemIndex;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("ALPHA", result);
        String[] count = result.split("\\*");

        String response = count[0];
        String fileName = count[1];
        int itemIndex = Integer.parseInt(count[2]);

        mApplication.setCurrentDownload(null);

        if (response.contains("Success")) {

            getAdapter(callback).notifyItemChanged(itemIndex);

        } else if (response.equals("Error: 404")) {
            if (!fileName.substring(0, 3).equals("404")) {
                mApplication.getModel().getImageDataInfo().getImageData().getImages().set(itemIndex, "404" + fileName);
            }
            getAdapter(callback).notifyItemChanged(itemIndex);
        } else if (response.equals("Error: Concurrency leak")) {

            Log.d("ALPHA", "Bypassed file: " + mApplication.getModel().getImageDataInfo().getImageData().getImages().get(itemIndex));

        } else {
            // TODO
            // If out of memory, retry downloading the same item
            //new DownloadImages(callback).execute("" + current_page, "" + item_on_page, "" + app_code);
        }

        mApplication.getModel().getDownloadQueue().remove(fileName);

        //manageSituation(callback);

    }

    private Adapter getAdapter(Callback callback) {
        return callback.getAdapter();
    }

    private void manageSituation(Callback callback) {
        callback.manageSituation();
    }
}