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

import java.util.Map;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FOOTER_VIEW = 1;

    public MyApplication mApplication;

    public MainActivity mainActivity;
    public Map<Integer, POJOItem> imageMap;
    public ImageLoader imageLoader;

    public Adapter(Map<Integer, POJOItem> linkMap, Activity activity) {
        mApplication = (MyApplication) activity.getApplicationContext();
        mainActivity = (MainActivity) activity;
        this.imageMap = linkMap;
        imageLoader = new ImageLoader(activity.getApplicationContext());
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

    // Now the critical part. You have return the exact item count of your list
// I've only one footer. So I returned data.size() + 1
// If you've multiple headers and footers, you've to return total count
// like, headers.size() + data.size() + footers.size()

    @Override
    public int getItemCount() {
        if (imageMap == null) {
            return 0;
        }
        if (imageMap.size() == 0) {
            //Return 1 here to show nothing
            return 1;
        }

        // Add extra view to show the footer view
        return imageMap.size();
    }

// Now define getItemViewType of your own.

    @Override
    public int getItemViewType(int position) {
        if (position == imageMap.size()) {
            // This is where we'll add footer.
            return FOOTER_VIEW;
        }
        return super.getItemViewType(position);
    }

// So you're done with adding a footer and its action on onClick.
// Now set the default ViewHolder for NormalViewHolder

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Define elements of a row here
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

            if(imageMap.get(itemIndex + 1) != null){
                caption.setText(imageMap.get(itemIndex + 1).getImageURL());
                imageLoader.DisplayImage(imageMap.get(itemIndex + 1).getImageURL(), image);
            } else {
                caption.setText("Error 404. File not found!");
                imageLoader.DisplayImage("404", image);
            }

                //set height in proportion to screen size
                int proportionalHeight = (int) ((double) (2 * mApplication.getDisplayWidth()) / 3);
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, proportionalHeight);
                rl_container.setLayoutParams(params);

            }
        }
    }
