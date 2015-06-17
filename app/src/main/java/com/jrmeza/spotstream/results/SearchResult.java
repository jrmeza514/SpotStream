package com.jrmeza.spotstream.results;

import android.graphics.Bitmap;

/**
 * Created by jrmeza on 6/13/15.
 */
public class SearchResult {
    String imageUrl;
    String title;
    String URI;
    Bitmap image;

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setURI( String uri ){
        this.URI = uri;
    }
    public void setImage(Bitmap image){
        this.image = image;
    }
}
