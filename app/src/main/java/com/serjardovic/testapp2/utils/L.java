package com.serjardovic.testapp2.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.serjardovic.testapp2.MyApplication;

public class L {

    public static void d(String message) {
        Log.d("HEX", message);
    }

    public static void d(String[] messages) {
        int line = 1;
        for(String message : messages) {
            Log.d("HEX(" + line + ")", message);
            line++;
        }
    }

    public static void t(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void q() {
        StringBuilder sb = new StringBuilder("Current que: ");
        if(!MyApplication.getInstance().getModel().imageInfo.getDownloadQueue().isEmpty()) {
            for (int i = 0; i < MyApplication.getInstance().getModel().imageInfo.getDownloadQueue().size(); i++) {
                sb.append(MyApplication.getInstance().getModel().imageInfo.getDownloadQueue().get(i).substring(
                        MyApplication.getInstance().getModel().imageInfo.getDownloadQueue().get(i).lastIndexOf("/") + 1
                ) + ", ");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        d(sb.toString().trim());
    }

}
