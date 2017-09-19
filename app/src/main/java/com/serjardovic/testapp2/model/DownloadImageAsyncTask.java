package com.serjardovic.testapp2.model;

import android.app.Application;
import android.os.AsyncTask;

import com.serjardovic.testapp2.Callback;
import com.serjardovic.testapp2.L;
import com.serjardovic.testapp2.MainActivity;
import com.serjardovic.testapp2.MyApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

class DownloadImageAsyncTask extends AsyncTask<Void, String, Void> {

    private FileCache fileCache;
    private MyApplication application;

    DownloadImageAsyncTask(Application application) {
        this.application = (MyApplication) application;
        fileCache = new FileCache(application);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        while(true) {
            List<String> imagesToDownload = imagesToDownload();
            if(imagesToDownload.isEmpty()) {
                L.d("No images to download");
                break;
            }

            for(String imageURL : imagesToDownload) {

                HttpURLConnection connection = null;
                InputStream inputStream = null;
                FileOutputStream outputStream = null;

                try {
                    File file = fileCache.getFile(imageURL);
                    if (file.exists()) {
                        L.d("File already in cache: " + imageURL);
                        application.getModel().getImageDataInfo().getImageData().getDownloadedImages().add(imageURL);
                        publishProgress(imageURL);
                        continue;
                    }
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
                    application.getModel().getImageDataInfo().getImageData().getDownloadedImages().add("Image not found: " + imageURL);

                    application.getModel().getImageDataInfo().getImageData().getAllImages()
                            .add(application.getModel().getImageDataInfo().getImageData().getAllImages().indexOf(imageURL), "Image not found: " + imageURL);

                    application.getModel().getImageDataInfo().getImageData().getAllImages().remove(imageURL);
                    publishProgress(imageURL);
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
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
                L.d("File successfully downloaded: " + imageURL);
                application.getModel().getImageDataInfo().getImageData().getDownloadedImages().add(imageURL);
                // TODO Let everybody know that a file was downloaded
                publishProgress(imageURL);
            }
            break;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        application.getCallback().imageDownloaded(values[0]);
    }

    private List<String> imagesToDownload() {
        List<String> allImages = application.getModel().getImageDataInfo().getImageData().getAllImages();
        List<String> downloadedImages = application.getModel().getImageDataInfo().getImageData().getDownloadedImages();
        List<String> imagesToDownload = new ArrayList<>();

        for(String image : allImages) {
            if(!downloadedImages.contains(image)) {
                imagesToDownload.add(image);
            }
        }
        return imagesToDownload;
    }
}

