package com.serjardovic.testapp2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.serjardovic.testapp2.utils.FileCache;
import com.serjardovic.testapp2.utils.MemoryCache;
import com.serjardovic.testapp2.utils.Utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    public MyApplication mApplication;
    public RecyclerView mRecyclerView;
    public ProgressBar mProgressBar;
    public LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connecting to MyApplication class
        mApplication = (MyApplication)getApplicationContext();

        // Creating an AppCode for this session and passing it to MyApplication
        mApplication.setAppCode((int)(Math.random() * 100000));

        // Passing shared parameters to MyApplication
        mApplication.setMainActivity(this);
        mApplication.setLinkList(new ArrayList<String>());
        mApplication.setImageMap(new TreeMap<Integer, POJOItem>());

        // Retrieving display parameters and passing them to MyApplication
        final Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mApplication.setDisplayWidth(size.x);
        mApplication.setDisplayHeight(size.y);
        Log.d("ALPHA", "Device screen resolution: " + mApplication.getDisplayWidth() + " x " + mApplication.getDisplayHeight());

        // Declaring all UI components
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_layer);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loader);

        loadNextPage();
    }

    @Override
    protected void onDestroy() {
        mApplication.getLinkList().clear();
        mApplication.getImageMap().clear();
        mRecyclerView.setAdapter(null);
        super.onDestroy();
    }

    //TODO
    //public void display..
    public void initializeRecycler() {
        mApplication.setAdapter(new Adapter(mApplication.getImageMap(), MainActivity.this));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mApplication.getAdapter());
    }

    public void loadNextPage() {
        new SendPostRequest(this).execute("" + (mApplication.getLinkList().size() / 7 + 1));
    }

    public void displayList() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }
}

