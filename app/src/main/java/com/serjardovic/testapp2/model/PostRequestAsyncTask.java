package com.serjardovic.testapp2.model;

import android.app.Activity;
import android.app.Application;
import android.os.AsyncTask;

import com.serjardovic.testapp2.L;
import com.serjardovic.testapp2.MainActivity;
import com.serjardovic.testapp2.MyApplication;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import javax.net.ssl.HttpsURLConnection;

class PostRequestAsyncTask extends AsyncTask<Integer, Void, Void> {

    private MyApplication application;

    PostRequestAsyncTask(Application application) {
        this.application = (MyApplication) application;

    }

    @Override
    protected void onPreExecute() {
        application.getModel().setPostReady(false);
    }

    @Override
    protected Void doInBackground(Integer... page) {

        OutputStream outputStream = null;
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;

        try {
            URL url = new URL("http://185.158.153.123/images.php");
            JSONObject postDataParams = new JSONObject();
            postDataParams.put("page", page[0]);
            L.d("Posting parameters to server: " + postDataParams.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            outputStream = connection.getOutputStream();
            bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(postDataParams.toString());
            bufferedWriter.flush();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                bufferedReader = new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                L.d("POST request successful");

                // Continue
                String[] parsedJSONPage = parseJSONPage(sb.toString());
                if(parsedJSONPage.length == 1) {
                    L.d("JSON parsing error: " + parsedJSONPage[0]);
                    return null;
                }
                L.d("Image data updated, current list size: " + updateImageData(parsedJSONPage) +  ". Initializing download...");
                application.getModel().downloadImages();
                return null;
            } else {
                L.d("POST request unsuccessful: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        application.getModel().setPostReady(true);
        application.getCallback().postExecuted();
    }

    private int updateImageData(String[] parsedJSONPage) {

        List<String> imagesToDownload = new ArrayList<>();

        if (parsedJSONPage[parsedJSONPage.length - 4].equals("next_page")) {

            L.d("Current page: " + parsedJSONPage[parsedJSONPage.length - 1] + ", next page: " + parsedJSONPage[parsedJSONPage.length - 3]);

            for (int i = 0; i < parsedJSONPage.length - 4; i++) {
                imagesToDownload.add(parsedJSONPage[i]);
            }

            application.getModel().getImageDataInfo().getImageData().setCurrentPage(Integer.parseInt(parsedJSONPage[parsedJSONPage.length - 1]));
            application.getModel().getImageDataInfo().getImageData().setNextPage(Integer.parseInt(parsedJSONPage[parsedJSONPage.length - 3]));
        } else {

            L.d("Current page: " + parsedJSONPage[parsedJSONPage.length - 1] + ", last page");

            for (int i = 0; i < parsedJSONPage.length - 2; i++) {
                imagesToDownload.add(parsedJSONPage[i]);
            }

            application.getModel().getImageDataInfo().getImageData().setCurrentPage(Integer.parseInt(parsedJSONPage[parsedJSONPage.length - 1]));
            application.getModel().getImageDataInfo().getImageData().setNextPage(0);
        }

        application.getModel().getImageDataInfo().getImageData().getAllImages().addAll(imagesToDownload);

        return application.getModel().getImageDataInfo().getImageData().getAllImages().size();
    }

    private String[] parseJSONPage(String unparsedJSONPage) {

        String[] levelOneParse = unparsedJSONPage.split("\"");

        if (levelOneParse.length < 6 || !unparsedJSONPage.contains("current_page")) {

            return new String[] {"Page is missing"};

        } else {

            String[] levelTwoParse = null;

            for (int i = 0; i < levelOneParse.length; i++) {
                if (levelOneParse[i].equals("current_page")) {
                    levelTwoParse = new String[i - 8];
                }
            }

            if (unparsedJSONPage.contains("next_page")) {

                levelTwoParse[0] = levelOneParse[3];

                for (int i = 5, j = 1; i < levelOneParse.length - 4; i = i + 2, j++) {
                    levelTwoParse[j] = levelOneParse[i];
                }

                levelTwoParse[levelTwoParse.length - 4] = levelOneParse[levelOneParse.length - 4];
                levelTwoParse[levelTwoParse.length - 3] = levelOneParse[levelOneParse.length - 3].replace(":", "").replace(",", "");
                levelTwoParse[levelTwoParse.length - 2] = levelOneParse[levelOneParse.length - 2];
                levelTwoParse[levelTwoParse.length - 1] = levelOneParse[levelOneParse.length - 1].replace(":", "").replace("}", "");
            } else {

                levelTwoParse[0] = levelOneParse[3];

                for (int i = 3, j = 1; i < levelOneParse.length - 2; i = i + 2, j++) {
                    levelTwoParse[j] = levelOneParse[i];
                }

                levelTwoParse[levelTwoParse.length - 2] = levelOneParse[levelOneParse.length - 2];
                levelTwoParse[levelTwoParse.length - 1] = levelOneParse[levelOneParse.length - 1].replace(":", "").replace("}", "");

            }

            return levelTwoParse;
        }
    }
}
