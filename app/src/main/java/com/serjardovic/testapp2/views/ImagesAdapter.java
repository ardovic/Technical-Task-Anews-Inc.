package com.serjardovic.testapp2.views;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.serjardovic.testapp2.MyApplication;
import com.serjardovic.testapp2.R;
import com.serjardovic.testapp2.interfaces.FragmentCommunicator;

import java.util.ArrayList;
import java.util.List;

class ImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_HOLDER = 0;
    private ArrayList<String> mImages;
    private int mHeight;
    private boolean isFooterEnabled;

    private FragmentCommunicator mCommunicator;

    ImagesAdapter(Activity activity, List<String> images, int height) {
        mCommunicator = (FragmentCommunicator) activity;
        mImages = new ArrayList<>(images);
        mHeight = height;
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
            Log.d("viewHolder", viewHolder.toString());
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

    public void setData(List<String> images) {
        int oldItemCount = getItemCount();
        mImages.addAll(images);
        notifyItemRangeInserted(oldItemCount - 1, getItemCount() - oldItemCount);
    }

    private class FooterHolder extends RecyclerView.ViewHolder {

        FooterHolder(View itemView) {
            super(itemView);
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewCaption;
        ImageView imageViewPicture;
        ProgressBar progressBar;

        ItemHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            textViewCaption = (TextView) itemView.findViewById(R.id.tv_caption);
            imageViewPicture = (ImageView) itemView.findViewById(R.id.iv_image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pb_progress);

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, mHeight);
            itemView.setLayoutParams(params);

        }

        void bindView(String imageUrl) {
            textViewCaption.setText(imageUrl);
            ImageLoader.getInstance().displayImage(imageUrl, imageViewPicture);

        }

        @Override
        public void onClick(View v) {
            MyApplication.getInstance().getModel().getPageInfo().setCurrentFullImage(textViewCaption.getText().toString());
            mCommunicator.showFullImage();
        }
    }
}
