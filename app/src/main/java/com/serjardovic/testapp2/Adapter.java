package com.serjardovic.testapp2;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.serjardovic.testapp2.model.FileCache;
import com.serjardovic.testapp2.model.MemoryCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public MyApplication application;
    public ImageLoader imageLoader;
    public List<String> allImages;
    public List<String> downloadedImages;
    public Context context;
    public MemoryCache memoryCache;

    public Adapter(Context context) {
        this.context = context;
        application = (MyApplication) context.getApplicationContext();
        imageLoader = new ImageLoader();
        allImages = application.getModel().getImageDataInfo().getImageData().getAllImages();
        downloadedImages = application.getModel().getImageDataInfo().getImageData().getDownloadedImages();
        memoryCache = new MemoryCache();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        try {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.bindView(position);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return allImages.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvCaption;
        public ImageView ivImage;
        public RelativeLayout rlContainer;
        public ProgressBar pbProgress;

        public ViewHolder(View itemView) {
            super(itemView);

            // Find view by ID and initialize here
            rlContainer = (RelativeLayout) itemView.findViewById(R.id.rl_container);
            pbProgress = (ProgressBar) itemView.findViewById(R.id.pb_progress);
            tvCaption = (TextView) itemView.findViewById(R.id.tv_caption);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
        }

        public void bindView(int position) {

            tvCaption.setText(allImages.get(position));

            if (downloadedImages.contains(allImages.get(position))) {
                imageLoader.DisplayImage(allImages.get(position), ivImage);
                pbProgress.setVisibility(View.GONE);
            }

            if (allImages.get(position).contains("Image not found")) {
                ivImage.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                ivImage.setLayoutParams(params);
                tvCaption.setBackgroundColor(application.getResources().getColor(R.color.colorRed));
            } else {
                // set height in proportion to screen size
                if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    int proportionalHeight = (int) ((double) (2 * application.getDisplayWidth()) / 3);
                    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, proportionalHeight);
                    rlContainer.setLayoutParams(params);
                } else {
                    int proportionalHeight = (int) ((double) (2 * application.getDisplayHeight()) / 3);
                    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, proportionalHeight);
                    rlContainer.setLayoutParams(params);
                }
            }
        }
    }


    private class ImageLoader {

        private FileCache fileCache = new FileCache(application);
        private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
        private ExecutorService executorService;


        public ImageLoader() {
            executorService = Executors.newFixedThreadPool(application.getNumberOfCores() - 1);
        }


        public void DisplayImage(String url, ImageView imageView) {

            imageViews.put(imageView, url);
            Bitmap bitmap = memoryCache.get(url);
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
            else {
                queuePhoto(url, imageView);
            }
        }

        private void queuePhoto(String url, ImageView imageView) {

            PhotoToLoad p = new PhotoToLoad(url, imageView);
            executorService.submit(new PhotosLoader(p));
        }

        private Bitmap getBitmap(String imageURL) {
            File f = fileCache.getFile(imageURL);

            //from SD cache
            Bitmap b = decodeFile(f);
            if (b != null) {
                return b;
            } else {
                if(!imageURL.contains("Image not found")) {
                    L.d("File is corrupt: " + imageURL + ". Deleting the file and re-downloading...");
                    fileCache.getFile(imageURL).delete();
                    if(!application.getModel().getImageDataInfo().getImageData().getDownloadedImages().isEmpty()) {
                        application.getModel().getImageDataInfo().getImageData().getDownloadedImages().add(1, imageURL);
                    } else {
                        application.getModel().getImageDataInfo().getImageData().getDownloadedImages().add(imageURL);
                    }

                    application.getModel().downloadImages();
                }
            }
            return null;
        }

        //decodes image and scales it to reduce memory consumption
        private Bitmap decodeFile(File f) {
            try {
                //decode image size
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new FileInputStream(f), null, o);

                //o.outWidth = MainActivity.screenWidth;
                //o.outHeight = 2 * (o.outWidth / 3);

                //Find the correct scale value. It should be the power of 2.
                final int REQUIRED_SIZE = application.getDisplayWidth() / 2;
                int width_tmp = o.outWidth, height_tmp = o.outHeight;
                int scale = 1;

                while (true) {
                    if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                        break;
                    width_tmp /= 2;
                    height_tmp /= 0.5;
                    scale *= 2;
                }


                //decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        //Task for the queue
        private class PhotoToLoad {
            String url;
            ImageView imageView;

            PhotoToLoad(String u, ImageView i) {
                url = u;
                imageView = i;
            }
        }

        private class PhotosLoader implements Runnable {
            PhotoToLoad photoToLoad;

            PhotosLoader(PhotoToLoad photoToLoad) {
                this.photoToLoad = photoToLoad;
            }

            @Override
            public void run() {
                if (imageViewReused(photoToLoad))
                    return;
                Bitmap bmp = getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bmp);
                if (imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                Activity a = (Activity) photoToLoad.imageView.getContext();
                a.runOnUiThread(bd);
            }
        }

        private boolean imageViewReused(PhotoToLoad photoToLoad) {
            String tag = imageViews.get(photoToLoad.imageView);
            return (tag == null || !tag.equals(photoToLoad.url));
        }

        //Used to display bitmap in the UI thread
        private class BitmapDisplayer implements Runnable {
            Bitmap bitmap;
            PhotoToLoad photoToLoad;

            BitmapDisplayer(Bitmap b, PhotoToLoad p) {
                bitmap = b;
                photoToLoad = p;
            }

            public void run() {
                if (imageViewReused(photoToLoad))
                    return;
                if (bitmap != null) {
                    photoToLoad.imageView.setImageBitmap(bitmap);
                }

            }
        }
    }
}
