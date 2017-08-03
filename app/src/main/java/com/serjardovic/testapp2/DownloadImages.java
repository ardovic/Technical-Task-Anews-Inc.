package com.serjardovic.testapp2;

import android.os.AsyncTask;
import android.util.Log;

import com.serjardovic.testapp2.utils.FileCache;
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

    private FileCache fileCache;
    private MemoryCache memoryCache;
    private MyApplication mApplication;
    private Callback callback;

    DownloadImages(Callback callback) {

        this.callback = callback;
        mApplication = (MyApplication) callback.getContext().getApplicationContext();
        fileCache = new FileCache(callback.getContext());
        memoryCache = new MemoryCache();

    }

    @Override
    protected String doInBackground(String... params) {

        // params[0] - current_page (0 at first)
        // params[1] - item in the array on that page (0 at first)

        int current_page = Integer.parseInt(params[0]);
        int item_on_page = Integer.parseInt(params[1]);

        String fileName = mApplication.getModel().getSinglePostResponseList().get(current_page).getImages()[item_on_page];

        int position = (current_page * mApplication.getModel().getSinglePostResponseList().get(current_page).getImages().length) + item_on_page;

        File file = fileCache.getFile(fileName);
        if (file.exists())
            return "Success: File already exists" + "*" + fileName + "*" + current_page + "*" + item_on_page + "*" + position;
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
            return "Error: 404" + "*" + fileName + "*" + current_page + "*" + item_on_page + "*" + position;
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return "Error: Out of memory, clearing memory cache" + "*" + fileName + "*" + current_page + "*" + item_on_page + "*" + position;
        }
        //this string is returned as result
        return "Success: File downloaded" + "*" + fileName + "*" + current_page + "*" + item_on_page + "*" + position;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("ALPHA", result);
        String[] count = result.split("\\*");

        String response = count[0];
        String fileName = count[1];
        int current_page = Integer.parseInt(count[2]);
        int item_on_page = Integer.parseInt(count[3]);
        int position = Integer.parseInt(count[4]);

        if (response.contains("Success")) {

            getAdapter(callback).notifyItemChanged(position);

        } else {

            if (response.equals("Error: 404")) {
                mApplication.getModel().getSinglePostResponseList().get(current_page).getImages()[item_on_page] = fileName + "404";
                getAdapter(callback).notifyItemChanged(position);
            } else {
                // TODO
                // If out of memory, retry downloading the same item
                //new DownloadImages(callback).execute("" + current_page, "" + item_on_page, "" + app_code);
            }
        }
    }

    private Adapter getAdapter(Callback callback) {
        return callback.getAdapter();
    }
}