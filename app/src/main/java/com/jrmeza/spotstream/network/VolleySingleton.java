package com.jrmeza.spotstream.network;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.jrmeza.spotstream.Application;

/**
 * Created by jrmeza on 6/17/15.
 */
public class VolleySingleton {
    private static VolleySingleton volleySingleton;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public VolleySingleton(){
        mRequestQueue = Volley.newRequestQueue(Application.getInstance().getContext());
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private LruCache<String , Bitmap> lruCache = new LruCache<>((int)(Runtime.getRuntime().maxMemory() / 1024) / 8);
            @Override
            public Bitmap getBitmap(String url) {
                return lruCache.get( url );
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                lruCache.put( url , bitmap  );
            }
        });
    }
    public static VolleySingleton getInstance(){
       if ( volleySingleton == null)
       {
          volleySingleton = new VolleySingleton();
       }
        return volleySingleton;
    }
    public  RequestQueue getRequestQueue(){
        return mRequestQueue ;
    }
    public ImageLoader getImageLoader(){
        return mImageLoader;
    }

}
