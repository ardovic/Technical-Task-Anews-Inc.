package com.serjardovic.testapp2.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.serjardovic.testapp2.MyApplication;

public class L {

    public static void d(String message) {
        Log.d("HEX", message);
    }

    public static void t(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
