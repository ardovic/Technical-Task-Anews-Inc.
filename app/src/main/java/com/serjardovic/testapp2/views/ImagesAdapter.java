package com.serjardovic.testapp2.views;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.serjardovic.testapp2.MyApplication;
import com.serjardovic.testapp2.model.images.ImageInfo;
import com.serjardovic.testapp2.network.DownloadImageAsyncTask;
import com.serjardovic.testapp2.utils.ImageLoader;
import com.serjardovic.testapp2.utils.L;
import com.serjardovic.testapp2.R;
import com.serjardovic.testapp2.utils.FileCache;
import com.serjardovic.testapp2.utils.MemoryCache;


import java.util.ArrayList;

class ImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_HOLDER = 0;
    private ArrayList<String> mImages;
    private int mHeight;
    private boolean isFooterEnabled;
    private ImageInfo mImageInfo;

    ImagesAdapter(ArrayList<String> images, int height) {
        mImages = new ArrayList<>(images);
        mHeight = height;
        mImageInfo = MyApplication.getInstance().getModel().imageInfo;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_HOLDER) {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
        } else {
            return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_footer, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ItemHolder viewHolder = (ItemHolder) holder;
            viewHolder.bindView(mImages.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == getItemCount() - 1 && isFooterEnabled ? 1 : ITEM_HOLDER;
    }

    void isPageLoading(boolean isLoading) {
        isFooterEnabled = isLoading;
        notifyItemChanged(getItemCount());
    }

    @Override
    public int getItemCount() {
        return mImages == null ? 0 : isFooterEnabled ? mImages.size() + 1 : mImages.size();
    }

    public void setData(ArrayList<String> images) {
        int oldItemCount = getItemCount();
        mImages.addAll(images);
        notifyItemRangeInserted(oldItemCount - 1, getItemCount() - oldItemCount);

    }

    private class FooterHolder extends RecyclerView.ViewHolder {

        FooterHolder(View itemView) {
            super(itemView);
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        TextView textViewCaption;
        ImageView imageViewPicture;
        ProgressBar progressBar;

        ItemHolder(View itemView) {
            super(itemView);

            textViewCaption = (TextView) itemView.findViewById(R.id.tv_caption);
            imageViewPicture = (ImageView) itemView.findViewById(R.id.iv_image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pb_progress);

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, mHeight);
            itemView.setLayoutParams(params);


        }

        void bindView(String imageUrl) {
            textViewCaption.setText(imageUrl);
            L.d("Setting text");

            if (!MyApplication.getInstance().getModel().imageInfo.getDownloadQueue().isEmpty() && !mImageInfo.isDownloadActive()) {
                mImageInfo.setDownloadActive(true);
                new DownloadImageAsyncTask(MyApplication.getInstance(), mImageInfo).execute(mImageInfo.getDownloadQueue().getFirst());
            }

            ImageLoader.getInstance().displayImage(imageUrl, imageViewPicture, progressBar);

        }
    }
}
