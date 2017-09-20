package com.serjardovic.testapp2.network;

import android.app.Application;
import android.os.AsyncTask;

import com.serjardovic.testapp2.interfaces.NetworkListener;
import com.serjardovic.testapp2.model.images.dto.PageData;
import com.serjardovic.testapp2.model.images.dto.PageWrapper;
import com.serjardovic.testapp2.utils.L;
import com.serjardovic.testapp2.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
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

import javax.net.ssl.HttpsURLConnection;

public class PostRequestAsyncTask extends AsyncTask<Integer, Void, PageWrapper> {

    private NetworkListener<PageData> mListener;

    public PostRequestAsyncTask(NetworkListener<PageData> listener) {
        mListener = listener;
    }

    @Override
    protected PageWrapper doInBackground(Integer... page) {

        OutputStream outputStream = null;
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        PageWrapper pageWrapper = null;

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
                return parseJSONPage(sb.toString());

            } else {
                return new PageWrapper(connection.getResponseMessage());
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
        return new PageWrapper("unknown");
    }

    @Override
    protected void onPostExecute(PageWrapper pageWrapper) {
        if (pageWrapper.getPageData() != null) {
            mListener.onSuccess(pageWrapper.getPageData());
        } else {
            mListener.onError(pageWrapper.getError());
        }
    }

    private PageWrapper parseJSONPage(String unparsedJSONPage) {
        PageWrapper pageWrapper = null;
        PageData pageData = new PageData();

        try {
            JSONObject json = new JSONObject(unparsedJSONPage);
            int currentPage = json.getInt("current_page");
            int nextPage = json.getInt("next_page");
            JSONArray jsonArray = json.getJSONArray("images");
            ArrayList<String> images = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                images.add(jsonArray.get(i).toString());
            }
            pageData.setCurrentPage(currentPage);
            pageData.setNextPage(nextPage);
            pageData.addImages(images);
            pageWrapper = new PageWrapper(pageData);

        } catch (JSONException e) {
            e.printStackTrace();
            pageWrapper = new PageWrapper("JSONException");
        }

        return pageWrapper;

    }
}
