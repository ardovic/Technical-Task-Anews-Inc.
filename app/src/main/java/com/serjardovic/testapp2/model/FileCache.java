package com.serjardovic.testapp2.model;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileCache {

    private File cacheDir;

    public FileCache(Application application){
        //Find the dir to save cached mImages
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED))
            cacheDir=new File(Environment.getExternalStorageDirectory(),"TTImages_cache");
        else
            cacheDir=application.getApplicationContext().getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url){
        //I identify mImages by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        return new File(cacheDir, filename);

    }

    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}
