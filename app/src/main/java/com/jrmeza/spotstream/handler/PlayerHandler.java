package com.jrmeza.spotstream.handler;

import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

/**
 * Created by jrmeza on 6/16/15.
 */
public class PlayerHandler implements PlayerNotificationCallback{

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {

    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {

    }
}
