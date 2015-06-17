package com.jrmeza.spotstream;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.jrmeza.spotstream.handler.ConnectionHandler;
import com.jrmeza.spotstream.handler.PlayerHandler;
import com.jrmeza.spotstream.http.DownloadWebpageTask;
import com.jrmeza.spotstream.parse.SpotifyParser;
import com.jrmeza.spotstream.results.SearchCallback;
import com.jrmeza.spotstream.results.SearchManager;
import com.jrmeza.spotstream.results.SearchResult;
import com.jrmeza.spotstream.results.SearchResultAdapter;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import org.json.JSONException;

import java.util.List;

public class SearchActivity extends Activity{

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    SearchResultAdapter searchResultAdapter;
    ImageButton searchButton;
    Context mContext;
    EditText searchTextField;
    List<SearchResult> results;
    private static final String CLIENT_ID = "356673a85fa140daadc9bef2efa38b5f";
    private static final String REDIRECT_URI = "jrmeza://callback";
    private int LOGIN_REQUEST_CODE = 1337;
    Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mContext = this;
        searchTextField = (EditText) findViewById(R.id.search_field);
        mRecyclerView = (RecyclerView) findViewById(R.id.search_results_recycler_view);
        layoutManager = new LinearLayoutManager(( this ));
        mRecyclerView.setAdapter( searchResultAdapter );
        mRecyclerView.setLayoutManager( layoutManager );
        searchButton = (ImageButton) findViewById(R.id.search_button);
        searchButton.setOnClickListener( new searchListener() );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
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
                        mPlayer.addConnectionStateCallback(new ConnectionHandler());
                        mPlayer.addPlayerNotificationCallback( new PlayerHandler());
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                });
            }
        }
    }

    class searchListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                String searchInputText = searchTextField.getText().toString();
                SearchManager.RequestBuilder builder = new SearchManager.RequestBuilder(searchInputText);
                builder.setType("track");
                final String requestUrl = builder.build();
                SearchCallback searchCallback  = new SearchCallback() {
                    @Override
                    public void call(String result) throws JSONException {
                        results = SpotifyParser.parseTrackResults(result);
                        SearchResultAdapter searchResultAdapter = new SearchResultAdapter(mContext , results);
                        mRecyclerView.setAdapter(searchResultAdapter);
                    }
                };
                new DownloadWebpageTask( searchCallback ).execute(requestUrl);
            }
        }
    }
}
