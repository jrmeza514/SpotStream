package com.jrmeza.spotstream;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.PlayerStateCallback;
import com.spotify.sdk.android.player.Spotify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements PlayerNotificationCallback, ConnectionStateCallback{
    private static final  int LOGIN_REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "jrmeza://callback";
    private static final String CLIENT_ID = "356673a85fa140daadc9bef2efa38b5f";
    private static final String DEBUG_TAG = "HTTPEXAMPLE";
    private PalyerStatusTask playerSatusTask;
    private ToggleButton mToglgeButton;
    private Player mPlayer;
    private CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                mPlayer.getPlayerState(new PlayerStateCallback() {
                    @Override
                    public void onPlayerState(PlayerState playerState) {
                        //Toast.makeText(getApplicationContext(), playerState.trackUri, Toast.LENGTH_LONG).show();
                        if (!playerState.trackUri.equals("") ){
                            mPlayer.resume();
                            //mPlayer.seekToPosition(290000);
                        }
                        playerSatusTask.cancel( true );
                        playerSatusTask = new PalyerStatusTask();
                        playerSatusTask.execute();
                    }
                });
            }else{
                mPlayer.pause();
            }
        }
    };
    private Button.OnClickListener searchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // fetch data
                playerSatusTask.cancel( true );
                new DownloadWebpageTask().execute(new SearchManager.RequestBuilder(searchTextField.getText().toString()).setType("track").build());
                playerSatusTask = new PalyerStatusTask();
                playerSatusTask.execute();
            } else {
                // display error
            }
        }
    };
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
        openLogin();
        mToglgeButton = (ToggleButton) findViewById(R.id.tb1);
        mToglgeButton.setOnCheckedChangeListener( changeListener );
        searchButton = (Button) findViewById(R.id.button);
        searchButton.setOnClickListener( searchListener );
        searchTextField = (EditText) findViewById(R.id.editText);
        resultsTextView = (TextView) findViewById(R.id.textView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setMax(1000);
        playerSatusTask = new PalyerStatusTask();

        String url = (new SearchManager.RequestBuilder("Thinking Out Loud")).build();
        Toast.makeText(this, url,Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if( id == R.id.logout_button){
            AuthenticationClient.logout(getApplication());
            openLogin();
        }

        return super.onOptionsItemSelected(item);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == LOGIN_REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            if(response.getType() == AuthenticationResponse.Type.TOKEN) {

                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);

                mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {

                    @Override
                    public void onInitialized(final Player player) {
                        mPlayer.addConnectionStateCallback(MainActivity.this);

                        mPlayer.addPlayerNotificationCallback(MainActivity.this);
                        //
                        // new PalyerStatusTask().execute();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }

                });
            }
        }
    }
    //j
    private void openLogin(){
        setContentView(R.layout.activity_main);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"user-read-private", "streaming"});

        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, LOGIN_REQUEST_CODE, request);
    }

    @Override
    public void onLoggedIn() {
        Toast.makeText(this, "Logged In", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoggedOut() {
        Toast.makeText(this, "Logged Out", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Toast.makeText(this, "Failed To Log In!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {

    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG);
    }

    public void getSearch(){

    }
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return e.getMessage();
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResults = new JSONObject( result );
                JSONObject jsonTracks = jsonResults.getJSONObject("tracks");
                JSONArray jsonItems = jsonTracks.getJSONArray("items");
                JSONObject item0 = jsonItems.getJSONObject(0);
                String uri = item0.getString("uri");
                track_duration_ms = item0.getInt("duration_ms");




                resultsTextView.setText(uri);
                mPlayer.play(uri);


                cancel(true);

            } catch (JSONException e) {
                Log.d("JSONException" ,e.getMessage());
            }


        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.


        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = "";
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = "";
            while ( (line =  bufferedReader.readLine()) != null ){
                contentAsString += line;
            }
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
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
