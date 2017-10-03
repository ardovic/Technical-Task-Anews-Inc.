package com.serjardovic.testapp2.views;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.serjardovic.testapp2.R;

public class SingleFragment extends Fragment {

    public static final String TAG = "single_fragment";
    public final static String IMAGE_URL = "image_url";

    private PhotoView photoView;
    private TextView textView;

    public static SingleFragment newInstance(String imageURL) {
        SingleFragment singleFragment = new SingleFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_URL, imageURL);
        singleFragment.setArguments(args);
        return singleFragment;
    }

    public void updateFragmentImage(String imageURL) {
        this.getArguments().clear();
        Bundle args = new Bundle();
        args.putString(IMAGE_URL, imageURL);
        this.getArguments().putAll(args);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_single, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        photoView = (PhotoView) getActivity().findViewById(R.id.photo_view);
        textView = (TextView) getActivity().findViewById(R.id.tv_captionx);
    }

    @Override
    public void onResume() {
        super.onResume();
        String imageURL = getArguments().getString(IMAGE_URL, null);
        if(imageURL != null) {
            textView.setText(imageURL);
            ImageLoader.getInstance().displayImage(imageURL, photoView);
        }
    }
}
