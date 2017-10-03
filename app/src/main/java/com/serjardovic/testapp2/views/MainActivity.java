package com.serjardovic.testapp2.views;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.serjardovic.testapp2.R;
import com.serjardovic.testapp2.interfaces.FragmentCommunicator;
import com.serjardovic.testapp2.utils.L;

import io.reactivex.Single;

public class MainActivity extends AppCompatActivity implements FragmentCommunicator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getFragmentManager().findFragmentByTag(CollectiveFragment.TAG) == null) {
            CollectiveFragment mCollectiveFragment = new CollectiveFragment();
            SingleFragment mSingleFragment = SingleFragment.newInstance(null);
            FragmentTransaction mTransaction = getFragmentManager().beginTransaction();
            mTransaction.add(R.id.root_layout, mCollectiveFragment, CollectiveFragment.TAG);
            mTransaction.add(R.id.root_layout, mSingleFragment, SingleFragment.TAG);
            mTransaction.detach(mSingleFragment);
            mTransaction.commit();
        }
    }

    @Override
    public void showFullImage(String imageURL) {
        SingleFragment mSingleFragment = (SingleFragment) getFragmentManager().findFragmentByTag(SingleFragment.TAG);
        mSingleFragment.updateFragmentImage(imageURL);
        FragmentTransaction mTransaction = getFragmentManager().beginTransaction();
        mTransaction.detach(getFragmentManager().findFragmentByTag(CollectiveFragment.TAG));
        mTransaction.attach(mSingleFragment);
        mTransaction.addToBackStack(null);
        mTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getFragmentManager().popBackStack();
    }
}

