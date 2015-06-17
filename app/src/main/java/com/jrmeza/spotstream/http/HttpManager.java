package com.jrmeza.spotstream.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jrmeza on 6/16/15.
 */
public class HttpManager {
    static String readUrlContents(String targetUrl) throws IOException {
        InputStream inputStream = null;
        String contentAsString = "";
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            contentAsString = "";
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = "";
            while ( (line =  bufferedReader.readLine()) != null ){
                contentAsString += line;
            }
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return  contentAsString;
    }
}
