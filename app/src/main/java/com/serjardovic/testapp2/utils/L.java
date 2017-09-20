package com.serjardovic.testapp2.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class L {

    public static void d (String message) {
        Log.d("HEX", message);
    }

    public static void d (String[] messages) {
        int line = 1;
        for(String message : messages) {
            Log.d("HEX(" + line + ")", message);
            line++;
        }
    }

    public static void t (Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
