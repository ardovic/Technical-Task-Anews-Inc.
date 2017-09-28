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
import com.serjardovic.testapp2.model.images.PageInfo;
import com.serjardovic.testapp2.model.images.dto.PageData;
import com.serjardovic.testapp2.utils.L;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    private ImagesAdapter mImagesAdapter;
    private LinearLayoutManager mLayoutManager;
    private PageInfo mPageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPageInfo = MyApplication.getInstance().getModel().getPageInfo();

        mProgressBar = (ProgressBar) findViewById(R.id.pb_loader);
        mProgressBar.setVisibility(View.GONE);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(mRecyclerScrollListener);
    }

    private RecyclerView.OnScrollListener mRecyclerScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            int updatePosition = recyclerView.getAdapter().getItemCount() - 2;
            if (position >= updatePosition && !mPageInfo.isUpdating() && mPageInfo.getPageData().hasNextPage()) {
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPageInfo.setNetworkListener(null);
    }

    private NetworkListener mPageListener = new NetworkListener<PageData>() {
        @Override
        public void onSuccess(PageData data) {
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
        }

        @Override
        public void onError(String... error) {
            if (mImagesAdapter != null) {
                mImagesAdapter.isPageLoading(false);
            }
            L.d("Post Execute Error");
        }
    };
}

