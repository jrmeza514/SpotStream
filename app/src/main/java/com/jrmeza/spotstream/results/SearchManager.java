package com.jrmeza.spotstream.results;

import android.text.TextUtils;

/**
 * Created by jrmeza on 6/7/15.
 */
public class SearchManager {
    public SearchManager(){

    }
    public static class RequestBuilder{
        private String type = "";
        private String market = "US";
        private final String BASE_URL = "https://api.spotify.com/v1/search?";
        private String query = "";
        private String limit = "";
        private String offset = "";

        final static String TYPE_TRACK = "track";
        final static String TYPE_ALBUM = "album";
        final static String TYPE_ARTIST = "artist";
        final static String TYPE_PLAYLIST = "playlist";

        public RequestBuilder( String stringQuery ){
//            ?q=Winter+four+seasons&type=track&market=US&limit=1&offset=1
            query = TextUtils.join("+" , stringQuery.split(" "));


        }
        public String build(){
            String url = BASE_URL + "q=" + query;
            if(type.equals(TYPE_PLAYLIST) || type.equals(TYPE_ARTIST) || type.equals(TYPE_ALBUM) || type.equals(TYPE_TRACK)){
                url += "&type=" + type;
            }
            return  url;
        };
        public SearchManager.RequestBuilder setType( String type ){
            this.type = type;
            return this;
        }
    }

}
