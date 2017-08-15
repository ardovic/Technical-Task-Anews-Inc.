package com.serjardovic.testapp2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.serjardovic.testapp2.utils.ImageLoader;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public MyApplication mApplication;
    public ImageLoader imageLoader;
    public List<String> images;

    public Adapter(ImageDataInfo imageDataInfo, Callback callback) {
        mApplication = (MyApplication) callback.getContext().getApplicationContext();
        imageLoader = new ImageLoader(mApplication);
        images = imageDataInfo.getImageData().getImages();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        try {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.bindView(position);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {

        if (images == null) {
            return 0;
        } else if (images.size() == 0) {
            //Return 1 here to show nothing
            return 1;
        } else {
            return images.size();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView caption;
        public ImageView imageView;
        public RelativeLayout rl_container;

        public ViewHolder(View itemView) {
            super(itemView);
            //setIsRecyclable(false);

            // Find view by ID and initialize here
            rl_container = (RelativeLayout) itemView.findViewById(R.id.rl_container);
            caption = (TextView) itemView.findViewById(R.id.caption);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }

        public void bindView(int position) {

                caption.setText(images.get(position));
                imageLoader.DisplayImage(images.get(position), imageView);

                // set height in proportion to screen size
                int proportionalHeight = (int) ((double) (2 * mApplication.getDisplayWidth()) / 3);
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, proportionalHeight);
                rl_container.setLayoutParams(params);
            }
        }
    }
