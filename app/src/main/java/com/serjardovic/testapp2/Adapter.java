package com.serjardovic.testapp2;

import android.app.Activity;
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
    public List<SinglePostResponse> singlePostResponseList;

    public Adapter(List<SinglePostResponse> SinglePostResponseList, Activity activity) {
        mApplication = (MyApplication) activity.getApplicationContext();
        this.singlePostResponseList = SinglePostResponseList;
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        try {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.bindView(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        if(singlePostResponseList != null && singlePostResponseList.size() > 0){
            return singlePostResponseList.size()*7;
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView caption;
        public ImageView image;
        public RelativeLayout rl_container;

        public ViewHolder(View itemView) {
            super(itemView);
            // Find view by ID and initialize here
            rl_container = (RelativeLayout) itemView.findViewById(R.id.rl_container);
            caption = (TextView) itemView.findViewById(R.id.caption);
            image = (ImageView) itemView.findViewById(R.id.image);

        }

        public void bindView(int itemIndex) {

                // For now assume each page of exactly 7 items
                caption.setText(singlePostResponseList.get(itemIndex / 7).getImages()[(itemIndex % 7)]);
                imageLoader.DisplayImage(singlePostResponseList.get(itemIndex / 7).getImages()[(itemIndex % 7)], image);

                // set height in proportion to screen size
                int proportionalHeight = (int) ((double) (2 * mApplication.getDisplayWidth()) / 3);
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, proportionalHeight);
                rl_container.setLayoutParams(params);
            }
        }
    }
