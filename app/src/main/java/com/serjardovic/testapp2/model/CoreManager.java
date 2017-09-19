package com.serjardovic.testapp2.model;

import android.os.Build;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public abstract class CoreManager {

    public static int getNumberOfCores() {
        if (Build.VERSION.SDK_INT >= 17) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return getNumCoresOldPhones();
        }
    }
    private static int getNumCoresOldPhones() {

        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {

                if (Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            //Default to return 1 core
            return 1;

        }
    }

}
