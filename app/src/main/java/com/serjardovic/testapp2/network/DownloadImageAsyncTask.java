package com.serjardovic.testapp2.network;

import android.content.Context;
import android.os.AsyncTask;

import com.serjardovic.testapp2.interfaces.NetworkListener;
import com.serjardovic.testapp2.utils.L;
import com.serjardovic.testapp2.utils.FileCache;
import com.serjardovic.testapp2.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImageAsyncTask extends AsyncTask<String, Void, Object[]> {

    private NetworkListener<String> mListener;

    public enum Status {
        FILE_ALREADY_IN_CACHE,
        FILE_NOT_FOUND,
        ERROR_UNKNOWN,
        DOWNLOAD_SUCCESSFUL
    }

    private FileCache fileCache;

    public DownloadImageAsyncTask(Context context, NetworkListener<String> listener) {
        fileCache = FileCache.getInstance(context);
        mListener = listener;
    }

    @Override
    protected Object[] doInBackground(String... params) {

        String imageURL = params[0];

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            File file = fileCache.getFile(imageURL);
            if (file.exists()) {
                return new Object[] {imageURL, Status.FILE_ALREADY_IN_CACHE};
            }
           // file = fileCache.getFile(imageURL + "temp");
            URL downloadURL = new URL(imageURL);
            connection = (HttpURLConnection) downloadURL.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setInstanceFollowRedirects(true);
            inputStream = connection.getInputStream();
            outputStream = new FileOutputStream(file);
            Utils.CopyStream(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new Object[] {imageURL, Status.FILE_NOT_FOUND};
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[] {imageURL, Status.ERROR_UNKNOWN};
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        //fileCache.renameFile(imageURL+"temp", imageURL);
        return new Object[] {imageURL, Status.DOWNLOAD_SUCCESSFUL};
    }

    @Override
    protected void onPostExecute(Object[] objects) {

        String imageUrl = (String) objects[0];
        Status status = (Status) objects[1];

        switch (status) {
            case DOWNLOAD_SUCCESSFUL:
                L.d("Download successful: " + imageUrl);
                mListener.onSuccess(imageUrl);
                break;
            case FILE_ALREADY_IN_CACHE:
                L.d("File already in cache: " + imageUrl);
                mListener.onSuccess(imageUrl);
                break;
            case FILE_NOT_FOUND:
                L.d("File not found: " + imageUrl);
                mListener.onError(imageUrl, "File not found");
                break;
            case ERROR_UNKNOWN:
                L.d("Download unsuccessful: " + imageUrl + ". Error unknown...");
                mListener.onError(imageUrl);
                break;
        }
    }
}

