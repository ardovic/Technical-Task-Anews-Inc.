package com.serjardovic.testapp2;

import android.content.Context;
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
import java.util.Collections;

import javax.net.ssl.HttpsURLConnection;

class SendPostRequest extends AsyncTask<String, Void, String> {

    private MyApplication mApplication;
    private Context context;

    SendPostRequest (Context context){

        mApplication = (MyApplication) context.getApplicationContext();
        this.context = context;

    }

    protected void onPreExecute() {
    }
    protected String doInBackground(String... args) {

        try {
            URL url = new URL("http://185.158.153.123/images.php");
            JSONObject postDataParams = new JSONObject();
            postDataParams.put("page", args[0]);
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
        if (result.contains("current_page")) {
            String[] resultArray = ParseJSONPage(result);
            Collections.addAll(mApplication.getLinkList(), resultArray);
            Log.d("ALPHA", "POST request successful!");
            Log.d("ALPHA", "List size: " + mApplication.getLinkList().size());
            new DownloadImages(context).execute("" + (mApplication.getLinkList().size() - 7), mApplication.getAppCode() + "");
        } else {
            Log.d("ALPHA", "POST request unsuccessful. No more pages left!");
        }
    }
    private String[] ParseJSONPage(String unparsedJSON) {

        String[] level1parse = unparsedJSON.split("\"");
        String[] level2parse = new String[7];

        level2parse[0] = level1parse[3];
        level2parse[1] = level1parse[5];
        level2parse[2] = level1parse[7];
        level2parse[3] = level1parse[9];
        level2parse[4] = level1parse[11];
        level2parse[5] = level1parse[13];
        level2parse[6] = level1parse[15];

        return level2parse;

    }
}
