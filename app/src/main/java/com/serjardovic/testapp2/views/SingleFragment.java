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
import com.serjardovic.testapp2.MyApplication;
import com.serjardovic.testapp2.R;

public class SingleFragment extends Fragment {

    private PhotoView photoView;
    private TextView textView;

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

        textView.setText(MyApplication.getInstance().getModel().getPageInfo().getCurrentFullImage());
        ImageLoader.getInstance().displayImage(textView.getText().toString(), photoView);
    }
}
