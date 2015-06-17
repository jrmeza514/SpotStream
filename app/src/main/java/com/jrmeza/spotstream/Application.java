package com.jrmeza.spotstream;

import android.content.Context;

/**
 * Created by jrmeza on 6/17/15.
 */
public class Application extends android.app.Application {
    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
    public static Application getInstance(){
        return application;
    }
    public Context getContext(){
        return getApplicationContext();
    }
}
