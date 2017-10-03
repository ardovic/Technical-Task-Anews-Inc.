package com.serjardovic.testapp2.views;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.serjardovic.testapp2.R;
import com.serjardovic.testapp2.interfaces.FragmentCommunicator;
import com.serjardovic.testapp2.utils.L;

public class MainActivity extends AppCompatActivity implements FragmentCommunicator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getFragmentManager().findFragmentByTag("CF") == null) {
            CollectiveFragment mCollectiveFragment = new CollectiveFragment();
            FragmentTransaction mTransaction = getFragmentManager().beginTransaction();
            mTransaction.add(R.id.root_layout, mCollectiveFragment, "CF");
            mTransaction.commit();
        }
    }

    @Override
    public void showFullImage(String imageURL) {
        SingleFragment mSingleFragment = SingleFragment.newInstance(imageURL);
        FragmentTransaction mTransaction = getFragmentManager().beginTransaction();
        mTransaction.replace(R.id.root_layout, mSingleFragment);
        mTransaction.addToBackStack("replaceColWithSin");
        mTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getFragmentManager().popBackStack();
    }
}

