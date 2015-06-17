package com.jrmeza.spotstream.results;

import org.json.JSONException;

import java.util.concurrent.Callable;

import javax.security.auth.callback.Callback;

/**
 * Created by jrmeza on 6/16/15.
 */
public interface SearchCallback{

   public void call( String result) throws JSONException;
}
