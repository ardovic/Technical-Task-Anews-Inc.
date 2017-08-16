package com.serjardovic.testapp2;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Queue;

import javax.net.ssl.HttpsURLConnection;

class SendPostRequest extends AsyncTask<String, Void, String> {

    private Callback callback;
    private MyApplication mApplication;

    SendPostRequest(Callback callback) {

        this.callback = callback;
        mApplication = (MyApplication) callback.getContext().getApplicationContext();
    }

    protected void onPreExecute() {

        mApplication.setReady(false);

    }

    protected String doInBackground(String... args) {

        try {
            URL url = new URL("http://185.158.153.123/images.php");
            JSONObject postDataParams = new JSONObject();
            postDataParams.put("page", Integer.parseInt(args[0]));
            Log.d("ALPHA", "Posting parameters to server: " + postDataParams.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(postDataParams.toString());
            writer.flush();
            writer.close();
            os.close();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();
                return sb.toString();
            } else {
                return "Unsuccessful : " + responseCode;
            }
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {

        String[] resultArray = ParseJSONPage(result);

        if (resultArray[0].equals("Parser error")) {
            Log.d("ALPHA", "POST request failed");
        } else if (resultArray[0].equals("Page is missing")) {
            Log.d("ALPHA", "POST request successful");
            Log.d("ALPHA", "Page is missing");
        } else {
            Log.d("ALPHA", "POST request successful");

            for(String s : resultArray){
                Log.d("ALPHA", s);
            }

            List<String> imageData = mApplication.getModel().getImageDataInfo().getImageData().getImages();
            Queue<String> downloadQueue = mApplication.getModel().getDownloadQueue();

            if (resultArray[resultArray.length - 4].equals("next_page")) {

                Log.d("ALPHA", "Current page: " + resultArray[resultArray.length - 1] + ", next page: " + resultArray[resultArray.length - 3]);

                for (int i = 0; i < resultArray.length - 4; i++) {
                    imageData.add(resultArray[i]);
                    downloadQueue.add(resultArray[i]);
                }

                mApplication.getModel().getImageDataInfo().getImageData().setImages(imageData);
                mApplication.getModel().getImageDataInfo().getImageData().setCurrentPage(Integer.parseInt(resultArray[resultArray.length - 1]));
                mApplication.getModel().getImageDataInfo().getImageData().setNextPage(Integer.parseInt(resultArray[resultArray.length - 3]));
            } else {

                Log.d("ALPHA", "Current page: " + resultArray[resultArray.length - 1] + ", last page");

                for (int i = 0; i < resultArray.length - 2; i++) {
                    imageData.add(resultArray[i]);
                    downloadQueue.add(resultArray[i]);
                }

                mApplication.getModel().getImageDataInfo().getImageData().setImages(imageData);
                mApplication.getModel().getImageDataInfo().getImageData().setCurrentPage(Integer.parseInt(resultArray[resultArray.length - 1]));
                mApplication.getModel().getImageDataInfo().getImageData().setNextPage(0);
            }

            if(resultArray[resultArray.length - 1].equals("1")) {
                manageSituation(callback);
            }

            if(mApplication.getAdapter() != null) {
                mApplication.getAdapter().notifyItemChanged(mApplication.getModel().getImageDataInfo().getImageData().getImages().size()-1);
            }

            mApplication.setReady(true);
        }
    }

    private String[] ParseJSONPage(String unparsedJSON) {

        if (unparsedJSON.contains("Unsuccessful") || unparsedJSON.contains("Exception")) {
            String[] parser_error = {"Parser error"};
            return parser_error;
        }

        String[] level1parse = unparsedJSON.split("\"");

        if (level1parse.length < 6 || !unparsedJSON.contains("current_page")) {
            String[] page_missing = {"Page is missing"};
            return page_missing;
        } else {

            String[] level2parse = null;

            for (int i = 0; i < level1parse.length; i++) {
                if (level1parse[i].equals("current_page")) {
                    level2parse = new String[i - 8];
                }
            }

            if (unparsedJSON.contains("next_page")) {

                level2parse[0] = level1parse[3];

                for (int i = 5, j = 1; i < level1parse.length - 4; i = i + 2, j++) {
                    level2parse[j] = level1parse[i];
                }

                level2parse[level2parse.length - 4] = level1parse[level1parse.length - 4];
                level2parse[level2parse.length - 3] = level1parse[level1parse.length - 3].replace(":", "").replace(",", "");
                level2parse[level2parse.length - 2] = level1parse[level1parse.length - 2];
                level2parse[level2parse.length - 1] = level1parse[level1parse.length - 1].replace(":", "").replace("}", "");
            } else {

                level2parse[0] = level1parse[3];

                for (int i = 3, j = 1; i < level1parse.length - 2; i = i + 2, j++) {
                    level2parse[j] = level1parse[i];
                }

                level2parse[level2parse.length - 2] = level1parse[level1parse.length - 2];
                level2parse[level2parse.length - 1] = level1parse[level1parse.length - 1].replace(":", "").replace("}", "");

            }

            return level2parse;
        }
    }

    private void manageSituation(Callback callback) {
        callback.manageSituation();
    }
}
