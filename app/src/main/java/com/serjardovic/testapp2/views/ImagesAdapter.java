package com.serjardovic.testapp2.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.serjardovic.testapp2.MyApplication;
import com.serjardovic.testapp2.model.images.dto.PageData;
import com.serjardovic.testapp2.utils.L;
import com.serjardovic.testapp2.R;
import com.serjardovic.testapp2.utils.FileCache;
import com.serjardovic.testapp2.utils.MemoryCache;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_HOLDER = 0;
    private ArrayList<String> mImages;
    private int mHeight;
    private boolean isFooterEnabled;
    private ImageLoader imageLoader;
    private FileCache fileCache;
    private MemoryCache memoryCache;

    public ImagesAdapter(ArrayList<String> images, int height) {
        fileCache = FileCache.getInstance(MyApplication.getInstance());
        memoryCache = MemoryCache.getInstance();
        mImages = new ArrayList<>(images);
        mHeight = height;
        imageLoader = new ImageLoader();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_HOLDER) {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
        } else {
            return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_footer, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ItemHolder viewHolder = (ItemHolder) holder;
            viewHolder.bindView(mImages.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == getItemCount() - 1 && isFooterEnabled ? 1 : ITEM_HOLDER;
    }

    public void isPageLoading(boolean isLoading) {
        isFooterEnabled = isLoading;
        notifyItemChanged(getItemCount());
    }

    @Override
    public int getItemCount() {
        return mImages == null ? 0 : isFooterEnabled ? mImages.size() + 1 : mImages.size();
    }

    public void setData(ArrayList<String> images) {
        int oldItemCount = getItemCount();
        mImages.addAll(images);
        notifyItemRangeInserted(oldItemCount - 1, getItemCount() - oldItemCount);

    }

    public class FooterHolder extends RecyclerView.ViewHolder {

        public FooterHolder(View itemView) {
            super(itemView);
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        public TextView textViewCaption;
        public ImageView imageViewPicture;
        public ProgressBar progressBar;

        public ItemHolder(View itemView) {
            super(itemView);

            textViewCaption = (TextView) itemView.findViewById(R.id.tv_caption);
            imageViewPicture = (ImageView) itemView.findViewById(R.id.iv_image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pb_progress);

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, mHeight);
            itemView.setLayoutParams(params);
        }

        public void bindView(String imageUrl) {
            textViewCaption.setText(imageUrl);
            L.d("Setting text");

            if (fileCache.getFile(imageUrl).exists()) {
                L.d("File exists, setting image");
                imageLoader.DisplayImage(imageUrl, imageViewPicture);
                progressBar.setVisibility(View.GONE);
            } else {
                imageViewPicture.setImageDrawable(null);
            }
        }
    }


    private class ImageLoader {

        private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
        private ExecutorService executorService;


        public ImageLoader() {
            executorService = Executors.newFixedThreadPool(MyApplication.getInstance().getNumberOfCores() - 1);
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
                    // TODO
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
                final int REQUIRED_SIZE = MyApplication.getInstance().getDisplayWidth() / 2;
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
