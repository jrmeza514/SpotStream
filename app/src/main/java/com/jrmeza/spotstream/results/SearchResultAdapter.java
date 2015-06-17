package com.jrmeza.spotstream.results;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.jrmeza.spotstream.MainActivity;
import com.jrmeza.spotstream.R;
import com.jrmeza.spotstream.network.VolleySingleton;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jrmeza on 6/13/15.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchViewHolder> {
    LayoutInflater inflater;
    List<SearchResult> searchResults = Collections.emptyList();
    Context context;
    RequestQueue mRequestQueue;
    ImageLoader mImageLoader;
    public SearchResultAdapter(Context context, List<SearchResult> results){
        inflater = LayoutInflater.from( context );
        searchResults = results;
        this.context = context;
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        mImageLoader = VolleySingleton.getInstance().getImageLoader();

    }
    @Override

    public SearchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate( R.layout.search_result, viewGroup , false);
        SearchViewHolder searchViewHolder = new SearchViewHolder( view );
        return searchViewHolder;
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder searchViewHolder, int i) {
        final SearchResult result = searchResults.get(i);
        if(result.imageUrl != null){
            mImageLoader.get(result.imageUrl , new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    searchViewHolder.imageView.setImageBitmap( response.getBitmap() );
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    searchViewHolder.imageView.setImageBitmap( BitmapFactory.decodeResource(Resources.getSystem() , R.mipmap.ic_launcher ));
                }
            });
        }
        searchViewHolder.title.setText( result.title  );
        searchViewHolder.uri = result.URI;
    }


    @Override
    public int getItemCount() {
        return searchResults.size();
    }
    class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        TextView title;
        String uri;
        ImageButton playButton;
        public SearchViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.search_result_title);
            imageView = (ImageView) itemView.findViewById(R.id.result_image_view);
            playButton = (ImageButton) itemView.findViewById(R.id.playButton);
            playButton.setOnClickListener( this );
        }

        @Override
        public void onClick(View v) {
            MainActivity.mPlayer.play( uri );
        }
    }
}
