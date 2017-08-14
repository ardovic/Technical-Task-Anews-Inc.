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

    private static final int FOOTER_VIEW = 1;

    public void setFooter(int footer) {
        this.footer = footer;
    }

    private int footer = 0;

    public MyApplication mApplication;
    public ImageLoader imageLoader;
    public ImageData imageData;
    public List<String> images;

    public Adapter(ImageData imageData, Callback callback) {
        mApplication = (MyApplication) callback.getContext().getApplicationContext();
        this.imageData = imageData;
        imageLoader = new ImageLoader(mApplication);
        images = imageData.getImages();
    }

    public class NormalViewHolder extends ViewHolder {
        public NormalViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class FooterViewHolder extends ViewHolder {
        public FooterViewHolder(final View itemView) {
            super(itemView);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == FOOTER_VIEW) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.footer_item, parent, false);

            FooterViewHolder footerViewHolder = new FooterViewHolder(view);

            return footerViewHolder;
        }

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        NormalViewHolder normalViewHolder = new NormalViewHolder(view);

        return normalViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        try {
            if (holder instanceof NormalViewHolder) {
                NormalViewHolder normalViewHolder = (NormalViewHolder) holder;

                normalViewHolder.bindView(position);
            } else if (holder instanceof FooterViewHolder) {
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        if (images == null) {
            return 0;
        } else if (images.size() == 0) {
            //Return 1 here to show nothing
            return 1;
        } else {
            return images.size() + footer;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == images.size()) {
            // This is where we'll add footer.
            return FOOTER_VIEW;
        }

        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView caption;
        public ImageView imageView;
        public RelativeLayout rl_container;

        public ViewHolder(View itemView) {
            super(itemView);
            setIsRecyclable(false);
            // Find view by ID and initialize here
            rl_container = (RelativeLayout) itemView.findViewById(R.id.rl_container);
            caption = (TextView) itemView.findViewById(R.id.caption);
            imageView = (ImageView) itemView.findViewById(R.id.image);

        }

        public void bindView(int itemIndex) {

                caption.setText(images.get(itemIndex));
                imageLoader.DisplayImage(images.get(itemIndex), imageView);

                // set height in proportion to screen size
                int proportionalHeight = (int) ((double) (2 * mApplication.getDisplayWidth()) / 3);
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, proportionalHeight);
                rl_container.setLayoutParams(params);
            }
        }
    }
