package com.jrmeza.spotstream;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jrmeza.spotstream.handler.ConnectionHandler;
import com.jrmeza.spotstream.handler.PlayerHandler;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.PlayerStateCallback;
import com.spotify.sdk.android.player.Spotify;

public class MainActivity extends AppCompatActivity {

    private static final  int LOGIN_REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "jrmeza://callback";
    private static final String CLIENT_ID = "356673a85fa140daadc9bef2efa38b5f";
    private static final String DEBUG_TAG = "HTTPEXAMPLE";

    private PalyerStatusTask playerSatusTask;
    private ToggleButton mToglgeButton;
    public static Player mPlayer;
    private Button A2B;
    private Button searchButton;
    private EditText searchTextField;
    private TextView resultsTextView;
    private int track_duration_ms;
    private ProgressBar mProgressBar;
    private int mProgressStatus = 0;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startLoginActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        else if( id == R.id.logout_button)
        {
            AuthenticationClient.logout(this);
        }
        else if( id == R.id.search_action_button)
        {

            Intent intent = new Intent( getApplicationContext() , SearchActivity.class);
            startActivity( intent );
        }

        return super.onOptionsItemSelected(item);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == LOGIN_REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if(response.getType() == AuthenticationResponse.Type.TOKEN)
            {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Player.InitializationObserver initializationObserver = new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(final Player player) {
                        ConnectionHandler connectionHandler = new ConnectionHandler();
                        PlayerHandler playerHandler = new PlayerHandler();
                        player.addConnectionStateCallback( connectionHandler );
                        player.addPlayerNotificationCallback( playerHandler );
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                };
                mPlayer = Spotify.getPlayer(playerConfig, this, initializationObserver);
            }
        }
    }
    protected void startLoginActivity()
    {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, LOGIN_REQUEST_CODE, request);
    }

    void updateStatusBar(){
        mPlayer.getPlayerState(new PlayerStateCallback() {
            @Override
            public void onPlayerState(PlayerState playerState) {
                if(!playerState.trackUri.equals("")){
                    mProgressStatus = (playerState.positionInMs * 1000 ) / playerState.durationInMs;
                }
            }
        });
    }
    private class PalyerStatusTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            mProgressStatus = 0;
        }

        @Override
        protected void onCancelled() {

            super.onCancelled();
            mProgressStatus = 0;
        }

        @Override
        protected String doInBackground(Integer... params) {

            while( mProgressStatus  < 1000 && !isCancelled()){

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                updateStatusBar();
                Log.d("STATUS_BAR_JRM" , mProgressStatus + "");
                publishProgress(mProgressStatus);
            }
            mProgressStatus = 0;
            cancel( true );
            return null;
        }
    }
}
