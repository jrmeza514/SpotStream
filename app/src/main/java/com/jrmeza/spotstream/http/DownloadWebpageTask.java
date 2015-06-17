package com.jrmeza.spotstream.http;

import android.os.AsyncTask;

import com.jrmeza.spotstream.results.SearchCallback;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by jrmeza on 6/16/15.
 */
public class DownloadWebpageTask extends AsyncTask<String, Void, String> {
    private SearchCallback callback;
    public DownloadWebpageTask( SearchCallback searchCallback){
        callback = searchCallback;
    }
    @Override
    protected String doInBackground(String... urls) {
        try {
            return HttpManager.readUrlContents(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onPostExecute(String result ) {
        try {
            callback.call( result );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
};