package com.jrmeza.spotstream.handler;

import com.jrmeza.spotstream.MainActivity;
import com.spotify.sdk.android.player.ConnectionStateCallback;

/**
 * Created by jrmeza on 6/16/15.
 */
public class ConnectionHandler implements ConnectionStateCallback {
    private Runnable emptyRunnable = new Runnable() {
        @Override
        public void run() {

        }
    };
    private Runnable loggedInRunnable;
    private Runnable loggedOutRunnable;
    private Runnable loginFailedRunnable;
    private Runnable temporaryErrorRunnable;
    private Runnable connectionMessageRunnable;

    public ConnectionHandler(){
        loggedInRunnable = emptyRunnable;
        loggedOutRunnable = emptyRunnable;
        loginFailedRunnable = emptyRunnable;
        temporaryErrorRunnable = emptyRunnable;
        connectionMessageRunnable = emptyRunnable;

    }

    @Override
    public void onLoggedIn() {
        loggedInRunnable.run();
    }

    @Override
    public void onLoggedOut() {
        loggedOutRunnable.run();
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        loginFailedRunnable.run();
    }

    @Override
    public void onTemporaryError() {
        temporaryErrorRunnable.run();
    }

    @Override
    public void onConnectionMessage(String s) {
        connectionMessageRunnable.run();
    }

    public void setLoggedInRunnable(Runnable loggedInRunnable) {
        this.loggedInRunnable = loggedInRunnable;
    }

    public void setConnectionMessageRunnable(Runnable connectionMessageRunnable) {
        this.connectionMessageRunnable = connectionMessageRunnable;
    }

    public void setLoggedOutRunnable(Runnable loggedOutRunnable) {
        this.loggedOutRunnable = loggedOutRunnable;
    }

    public void setLoginFailedRunnable(Runnable loginFailedRunnable) {
        this.loginFailedRunnable = loginFailedRunnable;
    }

    public void setTemporaryErrorRunnable(Runnable temporaryErrorRunnable) {
        this.temporaryErrorRunnable = temporaryErrorRunnable;
    }
}
