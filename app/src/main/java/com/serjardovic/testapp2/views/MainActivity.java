package com.serjardovic.testapp2.views;

import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.serjardovic.testapp2.R;
import com.serjardovic.testapp2.interfaces.FragmentCommunicator;


public class MainActivity extends AppCompatActivity implements FragmentCommunicator {

    private FragmentManager mFragmentManager;
    private CollectiveFragment mCollectiveFragment;
    private SingleFragment mSingleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentManager = getFragmentManager();
        if (mFragmentManager.findFragmentByTag("CF") != null) {
            mCollectiveFragment = (CollectiveFragment) mFragmentManager.findFragmentByTag("CF");
        } else {
            addColFrag();
        }
        if (mFragmentManager.findFragmentByTag("SF") != null) {
            mSingleFragment = (SingleFragment) mFragmentManager.findFragmentByTag("SF");
        }
    }

    public void addColFrag() {
        mCollectiveFragment = new CollectiveFragment();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        mTransaction.add(R.id.root_layout, mCollectiveFragment, "CF");
        mTransaction.commit();
    }

    public void replaceColWithSin() {
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        mTransaction.replace(R.id.root_layout, mSingleFragment, "SF");
        mTransaction.addToBackStack("replaceColWithSin");
        mTransaction.commit();
    }

    @Override
    public void showFullImage() {
        if (mSingleFragment == null) {
            mSingleFragment = new SingleFragment();
        }
        replaceColWithSin();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mFragmentManager.popBackStack();
    }
}

