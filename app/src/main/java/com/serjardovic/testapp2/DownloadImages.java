package com.serjardovic.testapp2;

import android.content.Context;
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
    private Context context;

    DownloadImages (Context context){


        mApplication = (MyApplication) context.getApplicationContext();
        this.context = context;
        fileCache = new FileCache(context);
        memoryCache = new MemoryCache();

    }

    @Override
    protected String doInBackground(String... params) {
        String fileName = mApplication.getLinkList().get(Integer.parseInt(params[0]));
        File file = fileCache.getFile(fileName);
        if (file.exists())
            return "(200)" + fileName + "*" + Integer.parseInt(params[0]) + "*" + params[1];
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
            return "(404)" + fileName + "*" + Integer.parseInt(params[0]) + "*" + params[1];
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return "Out of memory, clearing memory cache!";
        }
        //this string is returned as result
        return "(202)" + fileName + "*" + Integer.parseInt(params[0]) + "*" + params[1];
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("ALPHA", result);
        String[] count = result.split("\\*");
        int i = Integer.parseInt(count[1]);
        int appCode = Integer.parseInt(count[2]);

        if(appCode == mApplication.getAppCode()) {
            if (result.contains("202") || result.contains("200")) {

                mApplication.getImageMap().put(linkNumber(mApplication.getLinkList().get(i)), new POJOItem(mApplication.getLinkList().get(i)));
                if (mApplication.getAdapter() == null) {
                    mApplication.getMainActivity().initializeRecycler();
                }
                mApplication.getMainActivity().displayList();
                mApplication.getAdapter().notifyDataSetChanged();
            } else if (result.contains("404")) {

                mApplication.getImageMap().put(linkNumber(mApplication.getLinkList().get(i)), null);
                mApplication.getAdapter().notifyDataSetChanged();
            }

            int item = i + 1;

            if (item < mApplication.getLinkList().size()) {

                new DownloadImages(context).execute(item + "", mApplication.getAppCode() + "");

            } else {
                Log.d("ALPHA", "Download manager idle!");
                mApplication.getMainActivity().loadNextPage();
            }
        }
    }
    private int linkNumber(String link) {
        String[] level1 = link.split("\\.");
        String[] level2 = level1[3].split("/");
        int i = Integer.parseInt(level2[2]);
        return i;
    }
}