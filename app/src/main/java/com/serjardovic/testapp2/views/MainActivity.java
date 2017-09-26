package com.serjardovic.testapp2.views;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.serjardovic.testapp2.MyApplication;
import com.serjardovic.testapp2.R;
import com.serjardovic.testapp2.interfaces.NetworkListener;
import com.serjardovic.testapp2.model.images.ImageInfo;
import com.serjardovic.testapp2.model.images.PageInfo;
import com.serjardovic.testapp2.model.images.dto.PageData;
import com.serjardovic.testapp2.network.DownloadImageAsyncTask;
import com.serjardovic.testapp2.utils.FileCache;
import com.serjardovic.testapp2.utils.L;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NetworkListener<String> {

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    private ImagesAdapter mImagesAdapter;
    private LinearLayoutManager mLayoutManager;
    private PageInfo mPageInfo;
    private ImageInfo mImageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPageInfo = MyApplication.getInstance().getModel().pageInfo;
        mImageInfo = MyApplication.getInstance().getModel().imageInfo;

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loader);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(mRecyclerScrollListener);
        mProgressBar.setVisibility(View.GONE);

    }

    private RecyclerView.OnScrollListener mRecyclerScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            int firstPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

            if (firstPosition > -1 && firstPosition < mPageInfo.getPageData().getImages().size()) {
                if (!mPageInfo.getPageData().getImages().get(firstPosition).contains("File not found")
                        && !FileCache.getInstance(MainActivity.this).getFile(mPageInfo.getPageData().getImages().get(firstPosition)).exists()) {
                    if (mImageInfo.downloadQueue.size() > 0) {
                        if (!mImageInfo.downloadQueue.get(0).equals(mPageInfo.getPageData().getImages().get(firstPosition))) {
                            mImageInfo.addImageToQueueStart(mPageInfo.getPageData().getImages().get(firstPosition));
                        }
                    } else {
                        mImageInfo.addImageToQueueStart(mPageInfo.getPageData().getImages().get(firstPosition));
                    }
                }
            }
            int updatePosition = recyclerView.getAdapter().getItemCount() - 2;

            if (lastPosition >= updatePosition && !mPageInfo.isUpdating() && mPageInfo.getPageData().hasNextPage()) {

                mImagesAdapter.isPageLoading(true);
                mPageInfo.getListImagesByPage();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        mPageInfo.setNetworkListener(mPageListener);

        if (mPageInfo.getPageData() == null && !mPageInfo.isUpdating()) {
            mPageInfo.getListImagesByPage();
        }

        if (mPageInfo.getPageData() != null) {

            int imageHeight;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                imageHeight = (int) ((double) (2 * MyApplication.getInstance().getDisplayWidth()) / 3);
            } else {
                imageHeight = (int) ((double) (2 * MyApplication.getInstance().getDisplayHeight()) / 3);
            }

            mImagesAdapter = new ImagesAdapter(mPageInfo.getPageData().getImages(), imageHeight);
            mRecyclerView.setAdapter(mImagesAdapter);
            mProgressBar.setVisibility(View.GONE);

        }

        if (!mImageInfo.downloadQueue.isEmpty() && !mImageInfo.downloadActive) {
            mImageInfo.downloadActive = true;
            new DownloadImageAsyncTask(MainActivity.this, MainActivity.this).execute(mImageInfo.downloadQueue.get(0));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPageInfo.setNetworkListener(null);
    }

    private NetworkListener mPageListener = new NetworkListener<PageData>() {
        @Override
        public void onSuccess(PageData data) {

            for (String imageURL : data.getImages()) {
                mImageInfo.addImageToQueueEnd(imageURL);
            }

            if (mImagesAdapter == null) {
                int imageHeight;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    imageHeight = (int) ((double) (2 * MyApplication.getInstance().getDisplayWidth()) / 3);
                } else {
                    imageHeight = (int) ((double) (2 * MyApplication.getInstance().getDisplayHeight()) / 3);
                }
                mImagesAdapter = new ImagesAdapter(data.getImages(), imageHeight);
                mRecyclerView.setAdapter(mImagesAdapter);
                mProgressBar.setVisibility(View.GONE);
            } else {
                mImagesAdapter.isPageLoading(false);
                mImagesAdapter.setData(data.getImages());
            }

            if (!mImageInfo.downloadQueue.isEmpty() && !mImageInfo.downloadActive) {
                mImageInfo.downloadActive = true;
                new DownloadImageAsyncTask(MainActivity.this, MainActivity.this).execute(mImageInfo.downloadQueue.get(0));
            }

        }

        @Override
        public void onError(String... error) {
            if (mImagesAdapter != null) {
                mImagesAdapter.isPageLoading(false);
            }
            L.d("Post Execute Error");
        }
    };

    @Override
    public void onSuccess(String imageURL) {

        mImageInfo.downloadActive = false;
        mImageInfo.removeImageFromQueue(imageURL);

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < mPageInfo.getPageData().getImages().size(); i++) {
            if (imageURL.equals(mPageInfo.getPageData().getImages().get(i))) {
                indices.add(i);
            }
        }
        for (Integer i : indices) {
            mImagesAdapter.notifyItemChanged(i);
        }

        if (!mImageInfo.downloadQueue.isEmpty() && !mImageInfo.downloadActive) {
            mImageInfo.downloadActive = true;
            new DownloadImageAsyncTask(this, this).execute(mImageInfo.downloadQueue.get(0));
        }
    }

    @Override
    public void onError(String... error) {

        mImageInfo.downloadActive = false;
        mImageInfo.removeImageFromQueue(error[0]);

        if (error.length > 1 && error[1].equals("File not found")) {
            mPageInfo.getPageData().changeImageName(error[0], error[1] + ": " + error[0]);

            for (int i = 0; i < mPageInfo.getPageData().getImages().size(); i++) {
                if (mPageInfo.getPageData().getImages().get(i).contains("File not found")) {
                    mImagesAdapter.notifyItemChanged(i);
                }
            }
        }

        if (!mImageInfo.downloadQueue.isEmpty() && !mImageInfo.downloadActive) {
            mImageInfo.downloadActive = true;
            new DownloadImageAsyncTask(this, this).execute(mImageInfo.downloadQueue.get(0));
        }
    }
}

