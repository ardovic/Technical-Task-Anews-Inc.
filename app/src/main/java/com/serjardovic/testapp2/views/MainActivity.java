package com.serjardovic.testapp2.views;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.serjardovic.testapp2.interfaces.Callback;
import com.serjardovic.testapp2.MyApplication;
import com.serjardovic.testapp2.R;
import com.serjardovic.testapp2.interfaces.NetworkListener;
import com.serjardovic.testapp2.model.images.ImageDataInfo;
import com.serjardovic.testapp2.model.images.PageInfo;
import com.serjardovic.testapp2.model.images.dto.PageData;
import com.serjardovic.testapp2.utils.L;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Callback {

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    private ImagesAdapter mImagesAdapter;
    private LinearLayoutManager mLayoutManager;
    private ImageDataInfo mImageDataInfo;
    private PageInfo mPageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageDataInfo = MyApplication.getInstance().getModel().imageDataInfo;
        mPageInfo = MyApplication.getInstance().getModel().pageInfo;
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loader);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mProgressBar.setVisibility(View.GONE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageInfo.setNetworkListener(mPageListener);
        if (mPageInfo.getPageData() == null && !mPageInfo.isUpdating()) {
            mPageInfo.getListImagesByPage();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mPageInfo.setNetworkListener(null);
    }

    @Override
    public void imageDownloaded(String imageURL) {

        List<Integer> indices = new ArrayList<>();
//
//        for (int i = 0; i < mImageDataInfo.getImageData().getAllImages().size(); i++) {
//            if (imageURL.equals(mImageDataInfo.getImageData().getAllImages().get(i))) {
//                indices.add(i);
//            }
//        }

        for (Integer i : indices) {
            mImagesAdapter.notifyItemChanged(i);
        }
    }

    @Override
    public void postExecuted() {
        mImagesAdapter.notifyDataSetChanged();
    }

    private NetworkListener mPageListener = new NetworkListener<PageData>() {
        @Override
        public void onSuccess(PageData data) {
            if (mImagesAdapter == null) {
                int height;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    height = (int) ((double) (2 * MyApplication.getInstance().getDisplayWidth()) / 3);
                } else {
                    height = (int) ((double) (2 * MyApplication.getInstance().getDisplayHeight()) / 3);
                }
                mImagesAdapter = new ImagesAdapter(data, height);
                mRecyclerView.setAdapter(mImagesAdapter);
                mProgressBar.setVisibility(View.GONE);
            } else {
                mImagesAdapter.setData(data);

            }


        }

        @Override
        public void onError(String error) {
            L.d("onError");
        }
    };
}

