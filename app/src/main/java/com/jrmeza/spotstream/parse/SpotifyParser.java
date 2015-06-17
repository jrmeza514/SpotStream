package com.jrmeza.spotstream.parse;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jrmeza.spotstream.R;
import com.jrmeza.spotstream.results.SearchResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jrmeza on 6/16/15.
 */
public class SpotifyParser {
    public static List<SearchResult> parseTrackResults( String result ) throws JSONException {
        JSONObject jsonResults = new JSONObject( result );
        JSONObject jsonTracks = jsonResults.getJSONObject("tracks");
        JSONArray jsonItems = jsonTracks.getJSONArray("items");
        List<SearchResult> results = new ArrayList<SearchResult>();

        List<String> urls = new ArrayList<String>();
        for( int x = 0; x < jsonItems.length(); x++){
            JSONObject currentJSonObject = jsonItems.getJSONObject( x );
            String title = currentJSonObject.getString("name");
            String uri = currentJSonObject.getString("uri");
            String imageUrl = currentJSonObject.getJSONObject("album").getJSONArray("images").getJSONObject( 2 ).getString("url");
            SearchResult currentResult = new SearchResult();

            currentResult.setTitle(title);
            currentResult.setImageUrl( imageUrl );
            currentResult.setURI( uri );
            currentResult.setImage( BitmapFactory.decodeResource(Resources.getSystem() , R.mipmap.ic_launcher));
            results.add( currentResult );
            urls.add(imageUrl);
        }
        return results;
    }
}
